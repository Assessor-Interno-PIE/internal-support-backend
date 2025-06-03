package app.auth.service;

import app.auth.dto.TokenResponse;
import app.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TokenService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private LogService logService;

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

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    entity,
                    TokenResponse.class
            );

            logService.registrar("Token", username, "/auth/admin-token", "Token obtido com sucesso");

            return response.getBody().getAccessToken();
        } catch (Exception e) {
            logService.registrar("Token", username, "/auth/admin-token", "Erro ao obter token: " + e.getMessage());
            throw new RuntimeException("Erro ao obter token do Keycloak: " + e.getMessage());
        }
    }
}
