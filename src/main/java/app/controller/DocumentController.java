package app.controller;

import app.entity.Document;
import app.service.DocumentService;
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
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/save")
    public ResponseEntity<String> saveDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("departmentName") String departmentName,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("addedBy") String addedBy
    ) {
        try {
            Document document = documentService.save(file, departmentName, title, description, addedBy);
            return ResponseEntity.ok("Arquivo salvo com sucesso! ID do documento: " + document.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    @GetMapping("/view/{documentId}")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long documentId) {
        try {
            Resource resource = documentService.downloadFile(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Resource resource = documentService.downloadFile(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<Document> findById(@PathVariable Long id) {
        try {
            Document document = documentService.findById(id);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Document>> findAll() {
        try {
            List<Document> documents = documentService.findAll();
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/find-all/paginated")
    public ResponseEntity<Page<Document>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            Page<Document> documents = documentService.findAllPaginated(PageRequest.of(page, size));
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/by-department/{departmentName}")
    public ResponseEntity<List<Document>> getDocumentsByDepartment(@PathVariable String departmentName) {
        try {
            List<Document> documents = documentService.findDocumentsByDepartment(departmentName);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/search/title-contains")
    public ResponseEntity<List<Document>> getDocumentsByTitleContaining(@RequestParam String keyword) {
        try {
            List<Document> documents = documentService.findDocumentsByTitleContaining(keyword);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocumentById(id);
            return ResponseEntity.ok("Documento deletado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping(value = "edit/{id}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateDocument(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("departmentName") String departmentName) {
        try {
            Document updatedDocument = documentService.updateDocument(id, file, title, description, departmentName);
            return ResponseEntity.ok("Documento atualizado com sucesso! ID do documento: " + updatedDocument.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o documento: " + e.getMessage());
        }
    }
}
