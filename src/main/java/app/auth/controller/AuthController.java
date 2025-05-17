package app.auth.controller;

import app.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Collections;

/**
 * Controlador responsável por endpoints relacionados à sessão do usuário autenticado.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService service;

    @Autowired
    public AuthController(AuthService service) {
        this.service = service;
    }

    /**
     * Retorna os dados básicos do usuário logado com base no token JWT.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> fetchAuthenticatedUser(Authentication auth) {
        return ResponseEntity.ok(service.extractUserDetails(auth));
    }

    /**
     * Endpoint de verificação de autenticação.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> isUserAuthenticated(Authentication auth) {
        boolean authenticated = auth != null && auth.isAuthenticated();
        return ResponseEntity.ok(Collections.singletonMap("authenticated", authenticated));
    }
}
