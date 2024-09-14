package org.example.service;

import jakarta.annotation.PostConstruct;
import org.example.model.AvailableLanguage;
import org.example.model.AvailableLanguageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AvailableLanguageLoader {
    private final RestTemplate restTemplate;

    @Value("${yandex.translate.api.listLanguages.url}")
    private String LANGUAGES_URL;

    private Map<String, String> availableLanguages;
    private TokenService tokenService;
    @Value("${yandex.translate.api.folder-id}")
    private String folderId;

    public AvailableLanguageLoader(RestTemplate restTemplate, TokenService tokenService) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
        this.availableLanguages = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        loadLanguages();
    }

    @Scheduled(fixedRate = 3600000) // Обновление каждый час
    public void updateLanguages() {
        loadLanguages();
    }

    private void loadLanguages() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(tokenService.getIamToken());
            Map<String, Object> body = new HashMap<>();
            body.put("folderId", folderId);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<AvailableLanguageResponse> response = restTemplate.exchange(LANGUAGES_URL, HttpMethod.POST, entity, AvailableLanguageResponse.class);
            if (response.getBody().languages() != null) {
                Map<String, String> newLanguages = new ConcurrentHashMap<>();
                for (AvailableLanguage language : response.getBody().languages()) {
                    if (language.code() != null && language.name() != null) {
                        newLanguages.put(language.code(), language.name());
                    }
                }
                availableLanguages = newLanguages;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLanguageSupported(String languageCode) {
        return languageCode != null && availableLanguages.containsKey(languageCode);
    }


}
