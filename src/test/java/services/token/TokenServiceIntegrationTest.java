package services.token;

import org.example.TranslationServiceApplication;
import org.example.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TranslationServiceApplication.class)
public class TokenServiceIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Test
    public void getTokenTest() {
        String iamToken = tokenService.getIamToken();
        System.out.println(iamToken);
        assertNotNull(iamToken, "i-am token должен быть получен");
    }
}