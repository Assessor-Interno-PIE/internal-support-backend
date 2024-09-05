package app.controller;

import app.entity.Document;
import app.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody Document document) {
        String message = documentService.save(document);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<Document> findById(@PathVariable Long id) {
        Document document = documentService.findById(id);
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Document>> findAll() {
        List<Document> documents = documentService.findAll();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = documentService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<Document> updateById(@Valid @PathVariable Long id, @RequestBody Document updatedDocument) {
        Document document = documentService.updateById(id, updatedDocument);
        return new ResponseEntity<>(document, HttpStatus.OK);
    }
}
