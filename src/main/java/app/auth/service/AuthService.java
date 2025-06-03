package app.auth.service;

import app.exception.handler.AuthenticationException;
import app.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela autenticação e operações relacionadas a usuários no Keycloak
 */
@Service
public class AuthService {

    private final RestTemplate restTemplate;

    @Autowired
    private LogService logService;

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
            logService.registrar("Login", username, "/auth/login", "Login realizado com sucesso");
            return response.getBody();
        } catch (Exception e) {
            logService.registrar("Login", username, "/auth/login", "Falha ao autenticar: " + e.getMessage());
            throw new AuthenticationException("Falha na autenticação: " + e.getMessage());
        }
    }

    /**
     * Obtém informações do usuário autenticado
     */
    public Map<String, Object> getUserInfo(Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String username = jwt.getClaim("preferred_username");

            userInfo.put("id", jwt.getSubject());
            userInfo.put("username", username);
            userInfo.put("name", jwt.getClaim("name"));
            userInfo.put("email", jwt.getClaim("email"));

            // Roles
            List<String> roles = new ArrayList<>();
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            if (resourceAccess != null && resourceAccess.containsKey(clientId)) {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);
                if (clientAccess != null && clientAccess.containsKey("roles")) {
                    roles.addAll((List<String>) clientAccess.get("roles"));
                }
            }
            userInfo.put("roles", roles);

            // Departamentos
            List<String> groups = jwt.getClaim("groups");
            if (groups != null) {
                List<String> departments = groups.stream()
                        .filter(group -> group.startsWith("DEPT_"))
                        .map(group -> group.replace("DEPT_", ""))
                        .collect(Collectors.toList());
                userInfo.put("departments", departments);

                String currentDepartment = groups.stream()
                        .filter(group -> group.startsWith("DEPT_"))
                        .findFirst()
                        .map(group -> group.replace("DEPT_", ""))
                        .orElse(null);
                userInfo.put("currentDepartment", currentDepartment);
            }

            userInfo.put("isAuthenticated", authentication.isAuthenticated());

            logService.registrar("Consulta", username, "/auth/userinfo", "Informações do usuário recuperadas");
        }

        return userInfo;
    }

    /**
     * Verifica se o usuário está autenticado
     */
    public boolean isAuthenticated(Authentication authentication) {
        boolean autenticado = authentication != null && authentication.isAuthenticated();
        String username = "desconhecido";
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            username = jwt.getClaim("preferred_username");
        }

        logService.registrar("Autenticação", username, "/auth/check", "Usuário autenticado: " + autenticado);
        return autenticado;
    }
}
