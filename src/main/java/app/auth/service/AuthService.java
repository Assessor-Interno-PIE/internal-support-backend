package app.auth.service;

import app.exception.handler.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela autenticação e operações relacionadas a usuários no Keycloak
 */
@Service
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Realiza a autenticação do usuário através do Keycloak
     *
     * @param username Nome de usuário
     * @param password Senha do usuário
     * @return Resposta da autenticação com os tokens
     * @throws AuthenticationException Em caso de falha na autenticação
     */
    public Map<String, Object> login(String username, String password) {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("username", username);
        params.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new AuthenticationException("Falha na autenticação: " + e.getMessage());
        }
    }

    /**
     * Obtém informações do usuário autenticado
     *
     * @param authentication Objeto de autenticação do Spring Security
     * @return Mapa com informações do usuário
     */
    public Map<String, Object> getUserInfo(Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // Informações básicas do usuário
            userInfo.put("id", jwt.getSubject());
            userInfo.put("username", jwt.getClaim("preferred_username"));
            userInfo.put("name", jwt.getClaim("name"));
            userInfo.put("email", jwt.getClaim("email"));

            // Roles do usuário
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            userInfo.put("roles", roles);

            // Departamentos do usuário
            List<String> groups = jwt.getClaim("groups");
            if (groups != null) {
                List<String> departments = groups.stream()
                        .filter(group -> group.startsWith("DEPT_"))
                        .map(group -> group.replace("DEPT_", ""))
                        .collect(Collectors.toList());
                userInfo.put("departments", departments);

                // Departamento atual do usuário
                String currentDepartment = groups.stream()
                        .filter(group -> group.startsWith("DEPT_"))
                        .findFirst()
                        .map(group -> group.replace("DEPT_", ""))
                        .orElse(null);
                userInfo.put("currentDepartment", currentDepartment);
            }

            // Status da autenticação
            userInfo.put("isAuthenticated", authentication.isAuthenticated());
        }

        return userInfo;
    }

    /**
     * Verifica se o usuário está autenticado
     *
     * @param authentication Objeto de autenticação do Spring Security
     * @return true se autenticado, false caso contrário
     */
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

}