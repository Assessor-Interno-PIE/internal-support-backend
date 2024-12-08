package app.controller;

import app.entity.User;
import app.repository.UserRepository;
import app.config.JwtServiceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtServiceGenerator jwtServiceGenerator;

    // Endpoint para gerar e retornar os tokens para os usuários padrão e admin
    @GetMapping("/generate")
    public ResponseEntity<Object> generateDefaultTokens() {
        // Buscar os usuários padrão e admin do banco de dados (assumindo que já existam)
        User adminUser = userRepository.findByUsername("admin");
        User defaultUser = userRepository.findByUsername("user");

        if (adminUser == null || defaultUser == null) {
            return ResponseEntity.badRequest().body("Usuários padrão ou admin não encontrados.");
        }

        // Gerar tokens para ambos os usuários
        String adminToken = jwtServiceGenerator.generateToken(adminUser);
        String userToken = jwtServiceGenerator.generateToken(defaultUser);

        // Retornar ambos os tokens
        return ResponseEntity.ok().body(
                new TokenResponse(adminToken, userToken)
        );
    }

    // Classe para encapsular os tokens
    public static class TokenResponse {
        private String adminToken;
        private String userToken;

        public TokenResponse(String adminToken, String userToken) {
            this.adminToken = adminToken;
            this.userToken = userToken;
        }

        public String getAdminToken() {
            return adminToken;
        }

        public void setAdminToken(String adminToken) {
            this.adminToken = adminToken;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }
    }
}
