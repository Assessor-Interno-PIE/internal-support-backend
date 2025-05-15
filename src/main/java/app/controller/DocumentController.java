package app.controller;

import app.entity.Document;
import app.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documentos", description = "Endpoints para gerenciamento de documentos")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Operation(summary = "Salva um novo documento", description = "Faz upload de um documento com metadados associados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao salvar documento")
    })
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveDocument(
            @Parameter(description = "Arquivo a ser salvo", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Nome do departamento (grupo Keycloak)", required = true)
            @RequestParam("departmentName") @NotBlank(message = "Nome do departamento é obrigatório") String departmentName,
            @Parameter(description = "Título do documento", required = true)
            @RequestParam("title") @NotBlank(message = "Título é obrigatório") String title,
            @Parameter(description = "Descrição do documento", required = true)
            @RequestParam("description") @NotBlank(message = "Descrição é obrigatória") String description,
            @Parameter(description = "Usuário que adicionou o documento", required = true)
            @RequestParam("addedBy") @NotBlank(message = "Adicionado por é obrigatório") String addedBy
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Arquivo não pode estar vazio");
            }
            Document document = documentService.save(file, departmentName, title, description, addedBy);
            return ResponseEntity.ok("Documento salvo com sucesso! ID do documento: " + document.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar o documento: " + e.getMessage());
        }
    }

    @Operation(summary = "Visualiza um documento", description = "Retorna um documento para visualização no navegador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento retornado com sucesso",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao visualizar documento")
    })
    @GetMapping("/view/{documentId}")
    public ResponseEntity<Resource> viewDocument(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long documentId
    ) {
        try {
            Resource resource = documentService.downloadFile(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document-" + documentId + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Operation(summary = "Faz download de um documento", description = "Retorna um documento para download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento retornado com sucesso",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao fazer download do documento")
    })
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long documentId
    ) {
        try {
            Resource resource = documentService.downloadFile(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document-" + documentId + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Operation(summary = "Busca um documento por ID", description = "Retorna os metadados de um documento específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento encontrado",
                    content = @Content(schema = @Schema(implementation = Document.class))),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado")
    })
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<Document> findById(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long id
    ) {
        try {
            Document document = documentService.findById(id);
            return ResponseEntity.ok(document);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @Operation(summary = "Lista todos os documentos", description = "Retorna uma lista de todos os documentos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de documentos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Document.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum documento encontrado")
    })
    @GetMapping("/find-all")
    public ResponseEntity<List<Document>> findAll() {
        try {
            List<Document> documents = documentService.findAll();
            return ResponseEntity.ok(documents);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @Operation(summary = "Lista documentos com paginação", description = "Retorna uma lista paginada de documentos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum documento encontrado")
    })
    @GetMapping("/find-all/paginated")
    public ResponseEntity<Page<Document>> findAllPaginated(
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "5")
            @RequestParam(defaultValue = "5") int size
    ) {
        try {
            Page<Document> documents = documentService.findAllPaginated(PageRequest.of(page, size));
            return ResponseEntity.ok(documents);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @Operation(summary = "Busca documentos por departamento", description = "Retorna documentos associados a um departamento (grupo Keycloak)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documentos encontrados",
                    content = @Content(schema = @Schema(implementation = Document.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum documento encontrado para o departamento")
    })
    @GetMapping("/by-department/{departmentName}")
    public ResponseEntity<List<Document>> getDocumentsByDepartment(
            @Parameter(description = "Nome do departamento (grupo Keycloak)", required = true)
            @PathVariable @NotBlank(message = "Nome do departamento é obrigatório") String departmentName
    ) {
        try {
            List<Document> documents = documentService.findDocumentsByDepartment(departmentName);
            if (documents.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
            return ResponseEntity.ok(documents);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @Operation(summary = "Busca documentos por título", description = "Retorna documentos cujo título contém a palavra-chave")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documentos encontrados",
                    content = @Content(schema = @Schema(implementation = Document.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum documento encontrado com a palavra-chave")
    })
    @GetMapping("/search/title-contains")
    public ResponseEntity<List<Document>> getDocumentsByTitleContaining(
            @Parameter(description = "Palavra-chave para busca no título", required = true)
            @RequestParam @NotBlank(message = "Palavra-chave é obrigatória") String keyword
    ) {
        try {
            List<Document> documents = documentService.findDocumentsByTitleContaining(keyword);
            if (documents.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
            return ResponseEntity.ok(documents);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @Operation(summary = "Deleta um documento por ID", description = "Remove um documento pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long id
    ) {
        try {
            documentService.deleteDocumentById(id);
            return ResponseEntity.ok("Documento deletado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documento não encontrado: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualiza um documento", description = "Atualiza os metadados e/ou arquivo de um documento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao atualizar documento")
    })
    @PutMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateDocument(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novo arquivo (opcional)")
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "Título do documento", required = true)
            @RequestParam("title") @NotBlank(message = "Título é obrigatório") String title,
            @Parameter(description = "Descrição do documento", required = true)
            @RequestParam("description") @NotBlank(message = "Descrição é obrigatória") String description,
            @Parameter(description = "Nome do departamento (grupo Keycloak)", required = true)
            @RequestParam("departmentName") @NotBlank(message = "Nome do departamento é obrigatório") String departmentName
    ) {
        try {
            Document updatedDocument = documentService.updateDocument(id, file, title, description, departmentName);
            return ResponseEntity.ok("Documento atualizado com sucesso! ID do documento: " + updatedDocument.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documento não encontrado: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o documento: " + e.getMessage());
        }
    }
}
