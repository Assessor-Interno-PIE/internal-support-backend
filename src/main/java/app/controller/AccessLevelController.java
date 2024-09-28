package app.controller;

import app.entity.AccessLevel;
import app.service.AccessLevelService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access-levels")
public class AccessLevelController {

    @Autowired
    private AccessLevelService accessLevelService;

    @Operation(description = "Salva um nível de acesso")
    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody AccessLevel accessLevel) {
        String message = accessLevelService.save(accessLevel);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<AccessLevel> findById(@PathVariable Long id) {
        AccessLevel accessLevel = accessLevelService.findById(id);
        return new ResponseEntity<>(accessLevel, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<AccessLevel>> findAll() {
        List<AccessLevel> accessLevels = accessLevelService.findAll();
        return new ResponseEntity<>(accessLevels, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = accessLevelService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<AccessLevel> updateById(@Valid @PathVariable Long id, @RequestBody AccessLevel updatedAccessLevel) {
        AccessLevel accessLevel = accessLevelService.updateById(id, updatedAccessLevel);
        return new ResponseEntity<>(accessLevel, HttpStatus.OK);
    }

}
