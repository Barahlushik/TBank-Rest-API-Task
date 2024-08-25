import org.example.TranslationServiceApplication;
import org.example.service.TokenService;

import org.example.service.TranslateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = TranslationServiceApplication.class)
@Component
public class TokenServiceTest {

    @MockBean
    private RestTemplate restTemplate;


    @Value("${yandex.translate.api.url-token}")
    private String tokenUrl;

    @Autowired
    private TokenService tokenService;  // Внедрение зависимости через @Autowired


    @Test
    public void testGetIamToken() {
        String expectedIamToken = "dummyToken";
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("iamToken", expectedIamToken);
        mockResponse.put("expiresAt", expiresAt.toString());

        Mockito.when(restTemplate.postForObject(
                Mockito.eq(tokenUrl),
                Mockito.any(Map.class),
                Mockito.eq(Map.class)
        )).thenReturn(mockResponse);

        String iamToken = tokenService.getIamToken();
        assertEquals(expectedIamToken, iamToken);
    }

    @Test
    public void testRefreshTokenFailure() {
        Mockito.when(restTemplate.postForObject(
                Mockito.eq(tokenUrl),
                Mockito.any(Map.class),
                Mockito.eq(Map.class)
        )).thenReturn(null);
        assertThrows(RuntimeException.class, () -> {
            tokenService.getIamToken();
        });
    }
}
