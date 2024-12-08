package app.service;

import app.entity.Department;
import app.entity.Document;
import app.entity.User;
import app.repository.DocumentRepository;
import app.repository.DepartmentRepository;
import app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DocumentServiceTest {

    @MockBean
    private DocumentRepository documentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DepartmentRepository departmentRepository;

    @Autowired
    private DocumentService documentService;

    @Mock
    private MultipartFile file;

    private Department department;
    private Document document;

    @BeforeEach
    void setUp() {

        department = new Department();
        department.setId(1L);
        department.setName("Finance");

        document = new Document();
        document.setId(1L);
        document.setDepartment(department);
        document.setTitle("Document 1");
        document.setDescription("Description of document");
        document.setFilePath(Paths.get("upload", "document1.pdf").toString());

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setDepartment(department);

        userRepository.save(user);
    }

    @Test
    void testSaveDocument() throws IOException {

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(documentRepository.save(any(Document.class))).thenReturn(document);
        when(file.getOriginalFilename()).thenReturn("document1.pdf");

        Document savedDocument = documentService.save(file, 1L, "Document 1", "Description of document");

        assertNotNull(savedDocument);
        assertEquals("Document 1", savedDocument.getTitle());
        assertEquals("Description of document", savedDocument.getDescription());
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void testDownloadFile_NotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> documentService.downloadFile(1L));
    }

    @Test
    void testFindById_Success() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        Document foundDocument = documentService.findById(1L);

        assertNotNull(foundDocument);
        assertEquals(1L, foundDocument.getId());
        verify(documentRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {documentService.findById(1L);
        });
    }

    @Test
    void testDeleteDocumentById_Success() {
        when(documentRepository.existsById(1L)).thenReturn(true);

        documentService.deleteDocumentById(1L);

        verify(documentRepository).deleteById(1L);
    }

    @Test
    void testDeleteDocumentById_NotFound() {
        // Simulando o comportamento do repositório
        when(documentRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            documentService.deleteDocumentById(1L);
        });
    }

    @Test
    void testUpdateDocument_Success() throws IOException {
        // Simulando o comportamento do repositório de departamentos
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        // Simulando o comportamento do repositório de documentos
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(file.getOriginalFilename()).thenReturn("updated_document.pdf");
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document updatedDocument = documentService.updateDocument(1L, file, "Updated Document", "Updated description", 1L);

        assertNotNull(updatedDocument);
        assertEquals("Updated Document", updatedDocument.getTitle());
        assertEquals("Updated description", updatedDocument.getDescription());
        verify(documentRepository).save(any(Document.class));
    }

}
