package app.controller;

import app.entity.Document;
import app.service.DocumentService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("departmentName") @NotBlank String department,
            @RequestParam("title") @NotBlank String title,
            @RequestParam("description") @NotBlank String description,
            @RequestParam("addedBy") @NotBlank String addedBy
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("O arquivo enviado está vazio.");
        }

        try {
            Document saved = documentService.save(file, department, title, description, addedBy);
            return ResponseEntity.ok("Documento armazenado com sucesso. ID: " + saved.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar documento: " + e.getMessage());
        }
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> view(@PathVariable Long id) {
        try {
            Resource file = documentService.downloadFile(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"doc-" + id + "\"")
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        try {
            Resource file = documentService.downloadFile(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"doc-" + id + "\"")
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getById(@PathVariable Long id) {
        try {
            Document document = documentService.findById(id);
            return ResponseEntity.ok(document);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Document>> listAll() {
        try {
            return ResponseEntity.ok(documentService.findAll());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Document>> paginatedList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        try {
            Page<Document> docs = documentService.findAllPaginated(PageRequest.of(page, size));
            return ResponseEntity.ok(docs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/department/{name}")
    public ResponseEntity<List<Document>> listByDepartment(@PathVariable String name) {
        List<Document> docs = documentService.findDocumentsByDepartment(name);
        if (docs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(docs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Document>> searchByTitle(@RequestParam("keyword") String keyword) {
        List<Document> results = documentService.findDocumentsByTitleContaining(keyword);
        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            documentService.deleteDocumentById(id);
            return ResponseEntity.ok("Documento excluído.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento não encontrado.");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") @NotBlank String title,
            @RequestParam("description") @NotBlank String description,
            @RequestParam("departmentName") @NotBlank String department
    ) {
        try {
            Document updated = documentService.updateDocument(id, file, title, description, department);
            return ResponseEntity.ok("Documento atualizado. ID: " + updated.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento não encontrado.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Falha ao atualizar documento.");
        }
    }
}
