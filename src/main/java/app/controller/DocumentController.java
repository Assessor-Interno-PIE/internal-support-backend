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

    @Operation(summary = "Salva um novo documento")
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> save(
            @Parameter(description = "Arquivo a ser salvo", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "ID do grupo Keycloak", required = true)
            @RequestParam("groupId") @NotBlank(message = "ID do grupo é obrigatório") String groupId,
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
            Document document = documentService.save(file, groupId, title, description, addedBy);
            return ResponseEntity.ok("Documento salvo com sucesso! ID do documento: " + document.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar o documento: " + e.getMessage());
        }
    }

    @Operation(summary = "Visualiza um documento")
    @GetMapping("/view/{documentId}")
    public ResponseEntity<Resource> viewDocument(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long documentId
    ) {
        try {
            Resource resource = documentService.downloadFile(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document-" + documentId + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Operation(summary = "Faz download de um documento")
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long documentId
    ) {
        try {
            Resource resource = documentService.downloadFile(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document-" + documentId + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Operation(summary = "Busca um documento por ID")
    @GetMapping("/{id}")
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

    @Operation(summary = "Lista todos os documentos")
    @GetMapping
    public ResponseEntity<List<Document>> findAll() {
        try {
            List<Document> documents = documentService.findAll();
            return ResponseEntity.ok(documents);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @Operation(summary = "Lista documentos com paginação")
    @GetMapping("/paginated")
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

    @Operation(summary = "Busca documentos por grupo")
    @GetMapping("/by-group/{groupId}")
    public ResponseEntity<List<Document>> getDocumentsByGroup(
            @Parameter(description = "ID do grupo Keycloak", required = true)
            @PathVariable @NotBlank(message = "ID do grupo é obrigatório") String groupId
    ) {
        try {
            List<Document> documents = documentService.findByGroupId(groupId);
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

    @Operation(summary = "Deleta um documento por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long id
    ) {
        try {
            documentService.deleteById(id);
            return ResponseEntity.ok("Documento deletado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documento não encontrado: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualiza um documento")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateById(
            @Parameter(description = "ID do documento", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novo arquivo (opcional)")
            @RequestParam(value = "file", required = false) MultipartFile file,
            @Parameter(description = "Título do documento", required = true)
            @RequestParam("title") @NotBlank(message = "Título é obrigatório") String title,
            @Parameter(description = "Descrição do documento", required = true)
            @RequestParam("description") @NotBlank(message = "Descrição é obrigatória") String description,
            @Parameter(description = "ID do grupo Keycloak", required = true)
            @RequestParam("groupId") @NotBlank(message = "ID do grupo é obrigatório") String groupId
    ) {
        try {
            Document updatedDocument = documentService.updateById(id, file, title, description, groupId);
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
