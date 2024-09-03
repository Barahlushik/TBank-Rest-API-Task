package org.example.service.translate.impl;

import org.example.repository.TranslationRequestLogRepository;
import org.example.model.TranslateRequest;
import org.example.model.TranslationRequestLog;
import org.example.service.TokenService;
import org.example.service.translate.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("yandexTranslateService")
public class YandexTranslateService implements Translator {

    @Value("${yandex.translate.api.url}")
    private String apiUrl;


    @Value("${yandex.translate.api.folder-id}")
    private String folderId;

    @Autowired
    private RestTemplate restTemplate;

    private final TokenService tokenService;
    private final TranslationRequestLogRepository logRepository;

    @Autowired
    public YandexTranslateService(TranslationRequestLogRepository logRepository, TokenService tokenService) {
        this.tokenService = tokenService;
        this.logRepository = logRepository;
    }
    public String translate(TranslateRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenService.getIamToken());
        Map<String, Object> body = new HashMap<>();
        body.put("folderId", folderId);
        body.put("texts", new String[]{request.getText()});
        body.put("targetLanguageCode", request.getTargetLang());
        body.put("sourceLanguageCode", request.getSourceLang());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
        if (response.getStatusCode() != HttpStatus.OK || !response.getBody().containsKey("translations")) {
            throw new RuntimeException("Translation API error");
        }
        Map<String, Object> translations = (Map<String, Object>) ((List<Object>) response.getBody().get("translations")).get(0);
        String translatedText = (String) translations.get("text");
        TranslationRequestLog log = new TranslationRequestLog();
        log.setIpAddress(request.getRemoteAddress());
        log.setInputText(request.getText());
        log.setTranslatedText(translatedText);
        log.setRequestTime(LocalDateTime.now());
        logRepository.save(log);
        return translatedText;
    }
}