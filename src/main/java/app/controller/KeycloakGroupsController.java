package app.controller;

import app.service.KeycloakGroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/keycloak")
public class KeycloakGroupsController {

    @Autowired
    private KeycloakGroupsService keycloakGroupsService;

    @GetMapping("/groups")
    public ResponseEntity<Object> getGroups() {
        try {
            Object groups = keycloakGroupsService.listGroups();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar grupos: " + e.getMessage());
        }
    }
}
