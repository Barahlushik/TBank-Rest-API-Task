package org.example.service.translate.impl;

import org.example.req.TranslateRequest;
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

    @Autowired
    @Qualifier("LRUCache")
    private Cache<String,String> cache;

    private final ExecutorService executorService;

    public CachedYandexTranslateService(@Qualifier("yandexTranslateService") Translator translatorService,
                                        @Value("${yandex.translate.thread.pool-size}") int poolSize) {
        this.translatorService = translatorService;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public String translate(TranslateRequest request) {
        String[] words = request.getText().split("\\s+");
        List<Future<String>> futures = new ArrayList<>();

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
