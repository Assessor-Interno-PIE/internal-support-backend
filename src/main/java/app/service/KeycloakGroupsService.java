package app.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class KeycloakGroupsService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Configurações do Keycloak
    private final String tokenUrl = "http://localhost:8080/realms/master/protocol/openid-connect/token";
    private final String clientId = "admin-cli";
    private final String username = "admin";
    private final String password = "admin123";

    private final String gruposUrl = "http://localhost:8080/admin/realms/master/groups";

    public Object listarGrupos() {
        String token = obterToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                gruposUrl,
                HttpMethod.GET,
                entity,
                Object.class
        );

        return response.getBody();
    }

    private String obterToken() {
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

    // Classe auxiliar para mapear o token
    private static class TokenResponse {
        private String access_token;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
    }
}
