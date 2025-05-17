package app.controller;

import app.dto.CreateGroupDto;
import app.dto.GroupDto;
import app.service.GroupService;
import app.dto.MessageResponse;
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
@RequestMapping("/api/keycloak")
@Tag(name = "Grupos Keycloak", description = "Endpoints para gerenciamento de grupos no Keycloak")
@SecurityRequirement(name = "OAuth2")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "Cria um novo grupo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @PostMapping("/groups")
    public ResponseEntity<MessageResponse> save(@Valid @RequestBody CreateGroupDto dto) {
        groupService.save(dto);
        return ResponseEntity.ok(new MessageResponse("Grupo criado com sucesso"));
    }

    @Operation(summary = "Busca um grupo por ID")
    @GetMapping("/groups/{id}")
    public ResponseEntity<GroupDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(groupService.findById(id));
    }

    @Operation(summary = "Lista todos os grupos")
    @GetMapping("/groups")
    public ResponseEntity<List<GroupDto>> findAll() {
        return ResponseEntity.ok(groupService.findAll());
    }

    @Operation(summary = "Deleta um grupo por ID")
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<MessageResponse> deleteById(@PathVariable String id) {
        groupService.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Grupo deletado com sucesso"));
    }

    @Operation(summary = "Atualiza um grupo por ID")
    @PutMapping("/groups/{id}")
    public ResponseEntity<MessageResponse> updateById(@PathVariable String id, @Valid @RequestBody CreateGroupDto updatedGroup) {
        groupService.updateById(id, updatedGroup);
        return ResponseEntity.ok(new MessageResponse("Grupo atualizado com sucesso"));
    }
}
