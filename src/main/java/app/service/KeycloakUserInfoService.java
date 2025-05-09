package app.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KeycloakUserInfoService {

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject(); // O ID do usuário no Keycloak
        }
        return null;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public String getCurrentUserDepartment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            Map<String, Object> claims = jwt.getClaims();

            // Verificar em grupos
            List<String> groups = (List<String>) claims.get("groups");
            if (groups != null) {
                return groups.stream()
                        .filter(group -> group.startsWith("DEPT_"))
                        .findFirst()
                        .orElse(null);
            }

            // Ou em atributos personalizados
            Map<String, Object> attributes = (Map<String, Object>) claims.get("attributes");
            if (attributes != null && attributes.containsKey("department")) {
                return (String) attributes.get("department");
            }
        }
        return null;
    }

    public boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_admin"));
    }
}
