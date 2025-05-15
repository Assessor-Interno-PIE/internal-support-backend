package app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GroupService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Keycloak config
    @Value("${keycloak.admin.token-url}")
    private String tokenUrl;
    @Value("${keycloak.admin.client-id}")
    private String clientId;
    @Value("${keycloak.admin.username}")
    private String username;
    @Value("${keycloak.admin.password}")
    private String password;
    @Value("${keycloak.admin.groups-url}")
    private String groupsUrl;

    public Object listGroups() {
        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(
                groupsUrl,
                HttpMethod.GET,
                entity,
                Object.class
        );

        return response.getBody();
    }

    private String getToken() {
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

    // Token Mapper
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
