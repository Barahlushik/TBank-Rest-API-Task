package org.example.service;

import jakarta.annotation.PostConstruct;
import org.example.model.AvailableLanguage;
import org.example.model.AvailableLanguageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class AvailableLanguageLoader {
    private static final Logger log = LoggerFactory.getLogger(AvailableLanguageLoader.class);
    private final RestTemplate restTemplate;
    private static final long HOURLY_UPDATE_RATE_MS = 3_600_000;
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

    @Scheduled(fixedRate = HOURLY_UPDATE_RATE_MS) // Обновление каждый час
    public void updateLanguages() {
        loadLanguages();
    }

    private void loadLanguages() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(tokenService.getIamToken());

            Map<String, Object> body = Map.of("folderId", folderId);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<AvailableLanguageResponse> response = restTemplate.exchange(
                    LANGUAGES_URL,
                    HttpMethod.POST,
                    entity,
                    AvailableLanguageResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, String> newLanguages = response.getBody().languages().stream()
                        .filter(lang -> lang.code() != null && lang.name() != null)
                        .collect(Collectors.toConcurrentMap(
                                AvailableLanguage::code,
                                AvailableLanguage::name));

                availableLanguages = newLanguages;
                log.info("Successfully loaded {} languages", newLanguages.size());
            } else {
                log.warn("Failed to load languages. Response status: {}", response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("Error loading languages from Yandex Translate API", e);
        }
    }


    public boolean isLanguageSupported(String languageCode) {
        return languageCode != null && availableLanguages.containsKey(languageCode);
    }


}
