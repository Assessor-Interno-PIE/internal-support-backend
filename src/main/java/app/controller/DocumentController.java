package app.controller;

import app.entity.Document;
import app.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestPart("document") Document document,
                                       @RequestPart("pdf") MultipartFile pdfFile) {
        try {
            String message = documentService.save(document, pdfFile);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao salvar o documento: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        try {
            String responseMessage = documentService.deleteById(id);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erro ao deletar o documento: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<Document> updateById(@PathVariable Long id,
                                               @RequestPart("document") Document updatedDocument,
                                               @RequestPart(value = "pdf", required = false) MultipartFile pdfFile) {
        try {
            Document document = documentService.updateById(id, updatedDocument, pdfFile);
            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
