package app.controller;

import app.dto.CreateUserDto;
import app.dto.UpdateUserDto;
import app.dto.UserDto;
import app.service.UserService;
import app.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private AuditService auditService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Cria um novo usuário")
    @PostMapping
    public ResponseEntity<UserDto> save(@Valid @RequestBody CreateUserDto dto, HttpServletRequest request) {
        try {
            UserDto createdUser = userService.save(dto);

            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            throw e;
        }
    }

    @Operation(summary = "Busca um usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable String id, HttpServletRequest request) {
        UserDto user = userService.findById(id);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Lista todos os usuários")
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> findAll(HttpServletRequest request) {
        List<UserDto> users = userService.findAll();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Lista usuários paginados")
    @GetMapping("/paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<UserDto>> findAllPaginated(Pageable pageable, HttpServletRequest request) {
        Page<UserDto> users = userService.findAllPaginated(pageable);

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Deleta um usuário por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id, HttpServletRequest request) {
        try {
            UserDto user = userService.findById(id);
            String username = user.getUsername();

            userService.deleteById(id);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw e;
        }
    }

    @Operation(summary = "Atualiza um usuário por ID")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateById(@PathVariable String id, @Valid @RequestBody UpdateUserDto updatedUser, HttpServletRequest request) { // ADICIONAR HttpServletRequest
        try {
            UserDto oldUser = userService.findById(id);
            String oldUsername = oldUser.getUsername();

            UserDto updated = userService.updateById(id, updatedUser);

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw e;
        }
    }

    private String getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                return "authenticated_user"; // Por enquanto
            } catch (Exception e) {
                return "token_error";
            }
        }
        return "anonymous";
    }
}