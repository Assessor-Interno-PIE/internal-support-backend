package app.auth.service;

import app.auth.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Serviço responsável por recuperar um token de acesso
 * usando as credenciais administrativas configuradas.
 */
@Service
public class KeycloakTokenService {

    private final RestTemplate http = new RestTemplate();

    @Value("${keycloak.admin.token-url}")
    private String tokenEndpoint;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUser;

    @Value("${keycloak.admin.password}")
    private String adminPass;

    /**
     * Faz uma requisição ao endpoint de token do Keycloak usando
     * as credenciais de administrador.
     *
     * @return token JWT de acesso como String
     */
    public String retrieveAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formPayload = "grant_type=password"
                + "&client_id=" + clientId
                + "&username=" + adminUser
                + "&password=" + adminPass;

        HttpEntity<String> request = new HttpEntity<>(formPayload, headers);

        ResponseEntity<TokenResponse> response = http.postForEntity(
                tokenEndpoint,
                request,
                TokenResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getAccessToken();
        }

        throw new RuntimeException("Falha ao obter token administrativo do Keycloak.");
    }
}
