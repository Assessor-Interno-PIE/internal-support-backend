package app.auth.service;

import app.auth.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.admin.token-url}")
    private String tokenUrl;
    @Value("${keycloak.admin.client-id}")
    private String clientId;
    @Value("${keycloak.admin.username}")
    private String username;
    @Value("${keycloak.admin.password}")
    private String password;

    public String getToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=password"
                + "&client_id=" + clientId
                + "&username=" + username
                + "&password=" + password;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                tokenUrl,
                entity,
                TokenResponse.class
        );

        return response.getBody().getAccessToken();
    }
}
