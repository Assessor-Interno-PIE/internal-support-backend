package app.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

            // Departamentos do usuário (removendo o prefixo DEPT_)
            List<String> groups = jwt.getClaim("groups");
            if (groups != null) {
                List<String> departments = groups.stream()
                        .filter(group -> group.startsWith("DEPT_"))
                        .map(group -> group.replace("DEPT_", ""))
                        .collect(Collectors.toList());
                userInfo.put("departments", departments);
            }

            // Departamento atual do usuário
            String currentDepartment = groups != null ? 
                groups.stream()
                    .filter(group -> group.startsWith("DEPT_"))
                    .findFirst()
                    .map(group -> group.replace("DEPT_", ""))
                    .orElse(null) : null;
            userInfo.put("currentDepartment", currentDepartment);

            // Status da autenticação
            userInfo.put("isAuthenticated", authentication.isAuthenticated());
        }

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAuthenticated", authentication != null && authentication.isAuthenticated());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        
        Map<String, String> logoutResponse = new HashMap<>();
        logoutResponse.put("message", "Logout realizado com sucesso");
        return ResponseEntity.ok(logoutResponse);
    }
}
