package app.auth.controller;

import app.auth.dto.AuthResponse;
import app.auth.dto.LoginRequest;
import app.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller para operações de autenticação
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para login de usuário
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> tokenResponse = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(tokenResponse));
    }

    /**
     * Endpoint para obter informações do usuário autenticado
     */
    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, Object>> getUserInfo(Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = authService.getUserInfo(authentication);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * Endpoint para verificar se o usuário está autenticado
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAuthenticated", authService.isAuthenticated(authentication));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/debug/authorities")
    public Map<String, Object> getAuthorities(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        if (authentication != null) {
            result.put("authorities", authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));
            result.put("principal", authentication.getPrincipal());
        }
        return result;
    }
}