package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${yandex.translate.api.oauth-token}")
    private String oauthToken;
    private String iAmToken;
    private Instant expiresAt;
    @Value("${yandex.translate.api.url-token}")
    private String tokenUrl;


    public synchronized String getIamToken() {
        if (iAmToken == null || Instant.now().isAfter(expiresAt.minus(5, ChronoUnit.MINUTES))) {
            refreshToken();
        }
        return iAmToken;
    }

    private void refreshToken() {
        Map<String, String> body = new HashMap<>();
        body.put("yandexPassportOauthToken", oauthToken);
        Map<String, Object> response = restTemplate.postForObject(tokenUrl, body, Map.class);
        if (response == null || !response.containsKey("iamToken") || !response.containsKey("expiresAt")) {
            throw new RuntimeException("Failed to get IAM token");
        }

        this.iAmToken = (String) response.get("iamToken");
        this.expiresAt = Instant.parse((String) response.get("expiresAt"));
    }

}
