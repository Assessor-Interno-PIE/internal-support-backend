package app.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            userInfo.put("id", jwt.getSubject());
            userInfo.put("username", jwt.getClaim("preferred_username"));
            userInfo.put("name", jwt.getClaim("name"));
            userInfo.put("email", jwt.getClaim("email"));
            userInfo.put("roles", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            // Obter grupos/departamentos
            List<String> groups = jwt.getClaim("groups");
            if (groups != null) {
                userInfo.put("departments", groups.stream()
                        .filter(group -> group.startsWith("DEPT_"))
                        .collect(Collectors.toList()));
            }
        }

        return ResponseEntity.ok(userInfo);
    }

    // Se precisar de um endpoint de logout manual
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok().build();
    }
}
