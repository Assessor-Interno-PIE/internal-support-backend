package app.controller;

import app.entity.Department;
import app.entity.Document;
import app.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
            @RequestParam("departmentId") Long departmentId,
            @RequestParam("title") String title,
            @RequestParam("description") String description
    ) {
        try {
            Document document = documentService.save(file, departmentId, title, description);
            return ResponseEntity.ok("Arquivo salvo com sucesso! ID do documento: " + document.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    @GetMapping("/view/{documentId}")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long documentId) {
        try {
            Resource resource = documentService.downloadFile(documentId);

            // Configurar o cabeçalho para exibir o PDF no navegador
            String contentDisposition = "inline; filename=\"" + resource.getFilename() + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(MediaType.APPLICATION_PDF) // Definir o tipo de conteúdo como PDF
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        try {
            Resource resource = documentService.downloadFile(documentId);

            String contentDisposition = "attachment; filename=\"" + resource.getFilename() + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<Document> findById(@PathVariable Long id) {
        try {
            Document document = documentService.findById(id);
            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Document>> findAll() {
        List<Document> documents = documentService.findAll();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/by-department/{id}")
    public ResponseEntity<List<Document>> getDocumentsByDepartment(@PathVariable Long id) {
        List<Document> documents = documentService.findDocumentsByDepartment(id);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/search/title-contains")
    public ResponseEntity<List<Document>> getDocumentsByTitleContaining(@RequestParam String keyword) {
        List<Document> documents = documentService.findDocumentsByTitleContaining(keyword);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocumentById(id);
            return new ResponseEntity<>("Documento deletado com sucesso!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
