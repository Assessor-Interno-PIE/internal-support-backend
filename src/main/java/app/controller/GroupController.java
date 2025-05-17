package app.controller;

import app.dto.CreateGroupDto;
import app.dto.GroupDto;
import app.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/keycloak")
@Tag(name = "Grupos Keycloak", description = "Endpoints para gerenciamento de grupos no Keycloak")
@SecurityRequirement(name = "OAuth2")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Operation(summary = "Cria um novo grupo", description = "Cria um novo grupo no Keycloak com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grupo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao criar grupo")
    })
    @PostMapping("/groups")
    public ResponseEntity<String> save(@Valid @RequestBody CreateGroupDto createGroupDto) {
        try {
            groupService.save(createGroupDto);
            return ResponseEntity.ok("Grupo criado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao criar grupo: " + e.getMessage());
        }
    }

    @Operation(summary = "Busca um grupo por ID", description = "Retorna os detalhes de um grupo específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar grupo")
    })
    @GetMapping("/groups/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        try {
            GroupDto group = groupService.findById(id);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Grupo não encontrado: " + e.getMessage());
        }
    }

    @Operation(summary = "Lista todos os grupos", description = "Retorna uma lista de todos os grupos no Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de grupos retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum grupo encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao listar grupos")
    })
    @GetMapping("/groups")
    public ResponseEntity<?> findAll() {
        try {
            List<GroupDto> groups = groupService.findAll();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Nenhum grupo encontrado: " + e.getMessage());
        }
    }

    @Operation(summary = "Deleta um grupo por ID", description = "Remove um grupo do Keycloak pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grupo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao deletar grupo")
    })
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        try {
            String message = groupService.deleteById(id);
            return ResponseEntity.ok("Grupo deletado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao deletar grupo: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualiza um grupo por ID", description = "Atualiza os dados de um grupo existente no Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grupo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao atualizar grupo")
    })
    @PutMapping("/groups/{id}")
    public ResponseEntity<?> updateById(@PathVariable String id, @Valid @RequestBody GroupDto updatedGroup) {
        try {
            GroupDto group = groupService.updateById(id, updatedGroup);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao atualizar grupo: " + e.getMessage());
        }
    }
}
