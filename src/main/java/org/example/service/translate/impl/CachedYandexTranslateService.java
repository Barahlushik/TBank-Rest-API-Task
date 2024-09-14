package org.example.service.translate.impl;

import org.example.controller.TranslateController;
import org.example.exception.SourceLanguageNotFoundException;
import org.example.exception.TargetLanguageNotFoundException;
import org.example.model.TranslateRequest;
import org.example.model.TranslationRequestLog;
import org.example.repository.TranslationRequestLogRepository;
import org.example.service.AvailableLanguageLoader;
import org.example.service.translate.Translator;
import org.example.service.translate.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Service("cachedYandexTranslateService")
public class CachedYandexTranslateService implements Translator {

    private static final Logger logger = LoggerFactory.getLogger(CachedYandexTranslateService.class);
    private final Translator translatorService;

    private final Cache<String,String> cache;

    private final ExecutorService executorService;

    private final AvailableLanguageLoader availLangs;
    private final TranslationRequestLogRepository logRepository;

    public CachedYandexTranslateService(@Qualifier("yandexTranslateService") Translator translatorService,
                                        @Qualifier("LRUCache")Cache<String,String> cache,
                                        @Value("${yandex.translate.thread.pool-size}") int poolSize,
                                        TranslationRequestLogRepository logRepository,
                                        AvailableLanguageLoader availLangs) {

        this.logRepository = logRepository;
        this.translatorService = translatorService;
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.availLangs=availLangs;
        this.cache=cache;
    }

    @Override
    public String translate(TranslateRequest request) {
        String[] words = request.getText().split("\\s+");
        List<Future<String>> futures = new ArrayList<>();
        if (!availLangs.isLanguageSupported(request.getSourceLang())) {
            throw new SourceLanguageNotFoundException(String.format("%s: Не найден поддерживаемый исходный язык: %s ", request.getRemoteAddress(), request.getSourceLang()));
        }
        if (!availLangs.isLanguageSupported(request.getTargetLang())) {
            throw new TargetLanguageNotFoundException(String.format("%s: Не найден поддерживаемый целевой язык: %s ", request.getRemoteAddress(), request.getTargetLang()));
        }
        for (String word : words) {
            Future<String> future = executorService.submit(() -> translateWord(new TranslateRequest(request.getSourceLang(), request.getTargetLang(), word, request.getRemoteAddress())));
            futures.add(future);
        }

        StringBuilder result = new StringBuilder();
        for (Future<String> future : futures) {
            try {
                result.append(future.get()).append(" ");
            } catch (InterruptedException | ExecutionException e) {
                logger.error("{}: Ошибка при получении результата перевода для слова '{}'. Ошибка: {}", request.getRemoteAddress(), future, e.getMessage(), e);
            }
            }


        String translatedText = result.toString().trim();
        TranslationRequestLog log = new TranslationRequestLog();
        log.setIpAddress(request.getRemoteAddress());
        log.setInputText(request.getText());
        log.setTranslatedText(translatedText);
        log.setRequestTime(LocalDateTime.now());
        logRepository.save(log);
        return translatedText;
    }

    private String translateWord(TranslateRequest request) {
        String cacheKey = String.format("%s:%s:%s", request.getText(), request.getSourceLang(), request.getTargetLang());
        String translation = cache.get(cacheKey).orElse(null);
        if (translation != null) {
            logger.info("Перевод слова по ключу '{}' вернулся из кэша.", cacheKey);
            return translation;
        }
        translation = translatorService.translate(request);
        cache.put(cacheKey, translation);
        return translation;

    }
}
