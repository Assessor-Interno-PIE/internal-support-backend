package app.service;

import app.entity.Department;
import app.entity.Document;
import app.entity.User;
import app.repository.DepartmentRepository;
import app.repository.DocumentRepository;
import app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void testSaveDocument() {
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Título teste");
        document.setContent("documento feliz");

        // criar e definir IDs para as entidades
        Department department = new Department();
        department.setId(1L);
        document.setDepartment(department);

        Category category = new Category();
        category.setId(1L);
        document.setCategory(category);

        User user = new User();
        user.setId(1L);
        document.setUser(user);

        // Mock nos repositórios
        when(departmentRepository.existsById(department.getId())).thenReturn(true);
        when(categoryRepository.existsById(category.getId())).thenReturn(true);
        when(userRepository.existsById(user.getId())).thenReturn(true);

        String result = documentService.save(document);

        assertEquals("Documento salvo com sucesso", result);

        verify(documentRepository, times(1)).save(document);
    }

    @Test
    void testSaveDocument_DepartmentNotFound() {
        Document document = new Document();

        // cria o departamento sem id
        Department department = new Department();
        document.setDepartment(department);

        //mockar a resposta para o repositório de departamento
        when(departmentRepository.existsById(department.getId())).thenReturn(false);

        // espera que a IllegalArgumentException seja lançada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.save(document);
        });

        assertEquals("Departamento não encontrado.", exception.getMessage());
    }

    @Test
    void testSaveDocument_CategoryNotFound() {
        Document document = new Document();

        // Criar o departamento e categoria
        Department department = new Department();
        department.setId(1L);
        document.setDepartment(department);

        Category category = new Category();
        document.setCategory(category);

        // mockar a resposta para o repositório de categoria
        when(departmentRepository.existsById(department.getId())).thenReturn(true);
        when(categoryRepository.existsById(category.getId())).thenReturn(false);

        // esperar que a IllegalArgumentException seja lançada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.save(document);
        });

        assertEquals("Categoria não encontrada.", exception.getMessage());
    }

    @Test
    void testSaveDocument_UserNotFound() {
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Título teste");
        document.setContent("documento feliz");

        // Criar o departamento, categoria e usuário
        Department department = new Department();
        department.setId(1L);
        document.setDepartment(department);

        Category category = new Category();
        category.setId(1L);
        document.setCategory(category);

        User user = new User();
        user.setId(1L);
        document.setUser(user);

        // mocka a resposta para o repositório de usuário
        when(departmentRepository.existsById(department.getId())).thenReturn(true);
        when(categoryRepository.existsById(category.getId())).thenReturn(true);
        when(userRepository.existsById(user.getId())).thenReturn(false);

        // espera que a IllegalArgumentException seja lançada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            documentService.save(document);
        });

        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    void findDocumentWithValidID() {
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Título do Documento");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        Document result = documentService.findById(1L);

        assertEquals(document.getTitle(), result.getTitle());
        verify(documentRepository, times(1)).findById(1L);
    }

    @Test
    void findDocumentWithInvalidID() {
        when(documentRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            documentService.findById(1L);
        });

        assertEquals("Documento não encontrado com id: 1", exception.getMessage());
        verify(documentRepository, times(1)).findById(1L);
    }

    @Test
    void findAllDocuments() {
        Document doc1 = new Document();
        doc1.setId(1L);
        Document doc2 = new Document();
        doc2.setId(2L);
        List<Document> documentsList = Arrays.asList(doc1, doc2);

        when(documentRepository.findAll()).thenReturn(documentsList);

        List<Document> result = documentService.findAll();

        assertEquals(2, result.size());
        verify(documentRepository, times(1)).findAll();
    }

    @Test
    void findAllDocumentsEmpty() {
        when(documentRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            documentService.findAll();
        });

        assertEquals("Não há documentos registrados!", exception.getMessage());
        verify(documentRepository, times(1)).findAll();
    }

    @Test
    void deleteDocumentWithValidID() {
        Document document = new Document();
        document.setId(1L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        String result = documentService.deleteById(1L);

        assertEquals("Documento deletado com sucesso.", result);
        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteDocumentWithInvalidID() {
        when(documentRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            documentService.deleteById(1L);
        });

        assertEquals("Documento não encontrado com id: 1", exception.getMessage());
        verify(documentRepository, times(1)).findById(1L);
    }

    @Test
    void updateDocumentWithValidID() {
        Document existingDocument = new Document();
        existingDocument.setId(1L);
        existingDocument.setTitle("Título Antigo");

        Document updatedDocument = new Document();
        updatedDocument.setTitle("Título Novo");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(documentRepository.save(existingDocument)).thenReturn(existingDocument);

        Document result = documentService.updateById(1L, updatedDocument);

        assertEquals("Título Novo", result.getTitle());
        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, times(1)).save(existingDocument);
    }

    @Test
    void updateDocumentWithInvalidID() {
        Document updatedDocument = new Document();

        when(documentRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            documentService.updateById(1L, updatedDocument);
        });

        assertEquals("Documento não encontrado com id: 1", exception.getMessage());
        verify(documentRepository, times(1)).findById(1L);
    }


}
