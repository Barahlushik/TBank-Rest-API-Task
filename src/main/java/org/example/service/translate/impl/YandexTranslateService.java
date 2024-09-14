package org.example.service.translate.impl;
import org.example.model.TranslateRequest;
import org.example.service.TokenService;
import org.example.service.translate.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("yandexTranslateService")
public class YandexTranslateService implements Translator {

    @Value("${yandex.translate.api.url}")
    private String apiUrl;

    private static final Logger logger = LoggerFactory.getLogger(YandexTranslateService.class);

    @Value("${yandex.translate.api.folder-id}")
    private String folderId;

    @Autowired
    private RestTemplate restTemplate;

    private final TokenService tokenService;


    @Autowired
    public YandexTranslateService(TokenService tokenService) {
        this.tokenService = tokenService;
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
            logger.error("{}:Ошибка при обращении к Yandex Translate API. Статус '{}'. Сообщение: {}",request.getRemoteAddress(), response.getStatusCode(), response.getBody());
            throw new RuntimeException("Translation API error");
        }
        Map<String, Object> translations = (Map<String, Object>) ((List<Object>) response.getBody().get("translations")).get(0);
        return  (String) translations.get("text");


    }
}