package app.controller;

import app.dto.CreateUserDto;
import app.dto.UpdateUserDto;
import app.dto.UserDto;
import app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/keycloak/users")
@Tag(name = "Usuários Keycloak", description = "Endpoints para gerenciamento de usuários no Keycloak")
@SecurityRequirement(name = "OAuth2")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Cria um novo usuário")
    @PostMapping
    public ResponseEntity<UserDto> save(@Valid @RequestBody CreateUserDto dto) {
        return ResponseEntity.ok(userService.save(dto));
    }

    @Operation(summary = "Busca um usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Lista todos os usuários")
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Deleta um usuário por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Atualiza um usuário por ID")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateById(@PathVariable String id, @Valid @RequestBody UpdateUserDto updatedUser) {
        return ResponseEntity.ok(userService.updateById(id, updatedUser));
    }
} 