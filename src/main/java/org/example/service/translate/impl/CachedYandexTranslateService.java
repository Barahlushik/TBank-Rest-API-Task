package org.example.service.translate.impl;

import org.example.exception.SourceLanguageNotFoundException;
import org.example.exception.TargetLanguageNotFoundException;
import org.example.model.TranslateRequest;
import org.example.service.AvailableLanguageLoader;
import org.example.service.translate.Translator;
import org.example.service.translate.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Service("cachedYandexTranslateService")
public class CachedYandexTranslateService implements Translator {


    private final Translator translatorService;

    private final Cache<String,String> cache;

    private final ExecutorService executorService;

    private final AvailableLanguageLoader availLangs;

    public CachedYandexTranslateService(@Qualifier("yandexTranslateService") Translator translatorService,
                                        @Qualifier("LRUCache")Cache<String,String> cache,
                                        @Value("${yandex.translate.thread.pool-size}") int poolSize,
                                        AvailableLanguageLoader availLangs) {
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
            throw new SourceLanguageNotFoundException("Не найден язык исходного сообщения");
        }
        if (!availLangs.isLanguageSupported(request.getTargetLang())) {
            throw new TargetLanguageNotFoundException("Не найден язык для перевода сообщения");
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
                e.printStackTrace();
            }
        }

        return result.toString().trim();
    }

    private String translateWord(TranslateRequest request) {
        String cacheKey = String.format("%s:%s:%s", request.getText(), request.getSourceLang(), request.getTargetLang());
        String translation = cache.get(cacheKey).orElse(null);
        if (translation != null) {
            return translation;
        }
        translation = translatorService.translate(request);
        cache.put(cacheKey, translation);
        return translation;

    }
}
