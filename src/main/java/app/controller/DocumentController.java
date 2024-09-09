package app.controller;

import app.DTO.DocumentDTO;
import app.DTO.UserDTO;
import app.ModelMapperConfig.DocumentMapper;
import app.entity.Document;
import app.entity.User;
import app.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentMapper documentMapper;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody DocumentDTO documentDTO) {
        // Mapping entity User to UserDTO
        Document document = documentMapper.toDocument(documentDTO);

        String message = documentService.save(document);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<DocumentDTO> findById(@PathVariable Long id) {
        Document document = documentService.findById(id);

        // Mapping entity User to UserDTO
        DocumentDTO documentDTO = documentMapper.toDocumentDTO(document);
        return new ResponseEntity<>(documentDTO, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<DocumentDTO>> findAll() {
        List<Document> documents = documentService.findAll();

        List<DocumentDTO> documentDTOS = documents.stream()
                .map(documentMapper::toDocumentDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(documentDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = documentService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<DocumentDTO> updateById(@Valid @PathVariable Long id, @RequestBody DocumentDTO documentDTO) {
        Document document = documentMapper.toDocument(documentDTO);
        Document updatedDocument = documentService.updateById(id, document);
        DocumentDTO updatedDocumentDTO = documentMapper.toDocumentDTO(updatedDocument);
        return new ResponseEntity<>(updatedDocumentDTO, HttpStatus.OK);
    }
}
