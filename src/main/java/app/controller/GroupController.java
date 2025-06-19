package app.controller;

import app.dto.CreateGroupDto;
import app.dto.GroupDto;
import app.service.GroupService;
import app.service.AuditService;
import app.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/keycloak")
@Tag(name = "Grupos Keycloak", description = "Endpoints para gerenciamento de grupos no Keycloak")
@SecurityRequirement(name = "OAuth2")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    private AuditService auditService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "Cria um novo grupo")
    @PostMapping("/groups")
    public ResponseEntity<MessageResponse> save(@Valid @RequestBody CreateGroupDto dto, HttpServletRequest request) { // ADICIONAR HttpServletRequest
        try {
            groupService.save(dto);
            return ResponseEntity.ok(new MessageResponse("Grupo criado com sucesso"));
        } catch (Exception e) {
            throw e;
        }
    }

    @Operation(summary = "Busca um grupo por ID")
    @GetMapping("/groups/{id}")
    public ResponseEntity<GroupDto> findById(@PathVariable String id, HttpServletRequest request) { // ADICIONAR HttpServletRequest
        GroupDto group = groupService.findById(id);
        return ResponseEntity.ok(group);
    }

    @Operation(summary = "Lista todos os grupos")
    @GetMapping("/groups")
    public ResponseEntity<List<GroupDto>> findAll(HttpServletRequest request) { // ADICIONAR HttpServletRequest
        List<GroupDto> groups = groupService.findAll();
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Deleta um grupo por ID")
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<MessageResponse> deleteById(@PathVariable String id, HttpServletRequest request) { // ADICIONAR HttpServletRequest
        try {
            GroupDto group = groupService.findById(id);
            if (group != null) {
                groupService.deleteById(id);
            }
            return ResponseEntity.ok(new MessageResponse("Grupo deletado com sucesso"));
        } catch (Exception e) {
            throw e;
        }
    }

    @Operation(summary = "Lista grupos paginados")
    @GetMapping("/find-all/paginated")
    public ResponseEntity<Page<GroupDto>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupDto> paginated = groupService.findAllPaginated(pageable);
        return ResponseEntity.ok(paginated);
    }

    @Operation(summary = "Atualiza um grupo por ID")
    @PutMapping("/groups/{id}")
    public ResponseEntity<MessageResponse> updateById(@PathVariable String id, @Valid @RequestBody CreateGroupDto updatedGroup, HttpServletRequest request) { // ADICIONAR HttpServletRequest
        try {
            GroupDto oldGroup = groupService.findById(id);
            String oldName = oldGroup.getName();
            groupService.updateById(id, updatedGroup);

            return ResponseEntity.ok(new MessageResponse("Grupo atualizado com sucesso"));
        } catch (Exception e) {
            throw e;
        }
    }

    // MÉTODO HELPER PARA PEGAR USER ID
    private String getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Se você tem um serviço JWT ou Spring Security configurado:
                /*
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth instanceof JwtAuthenticationToken) {
                    JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) auth;
                    return jwtToken.getToken().getClaimAsString("sub"); // ou "preferred_username"
                }
                */
                return "authenticated_user"; // Por enquanto
            } catch (Exception e) {
                return "token_error";
            }
        }
        return "anonymous";
    }
}