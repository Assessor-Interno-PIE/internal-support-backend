package app.controller;

import app.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/keycloak")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping("/groups")
    public ResponseEntity<Object> getGroups() {
        try {
            Object groups = groupService.listGroups();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar grupos: " + e.getMessage());
        }
    }
}
