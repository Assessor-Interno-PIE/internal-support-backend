package app.controller;

import app.entity.Department;
import app.entity.Document;
import app.entity.User;
import app.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.InternalException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DocumentService documentService;

    @Test
    void saveDocumentSuccess() throws Exception {
        Document validDocument = new Document();
        validDocument.setTitle("Document Exemplar");
        validDocument.setContent("Content Exemplar");

        User user = new User();
        user.setId(1L);
        validDocument.setUser(user);

        Department department = new Department();
        department.setId(1L);
        validDocument.setDepartment(department);

        Category category = new Category();
        category.setId(1L);
        validDocument.setCategory(category);

        when(documentService.save(any(Document.class))).thenReturn("Documento salvo com sucesso");
        mockMvc.perform(post("/api/documents/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDocument)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Documento salvo com sucesso"));
    }

    @Test
    void saveDocumentWithoutUser() throws Exception {
        Document invalidDocument = new Document();
        invalidDocument.setTitle("Document Exemplar");
        invalidDocument.setContent("Content Exemplar");


        Department department = new Department();
        department.setId(1L);
        invalidDocument.setDepartment(department);

        Category category = new Category();
        category.setId(1L);
        invalidDocument.setCategory(category);

        mockMvc.perform(post("/api/documents/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDocument)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(("Erro: O usuário não deve estar vazio")));
    }

    @Test
    void saveDocumentWithoutDepartment() throws Exception {
        Document invalidDocument = new Document();
        invalidDocument.setTitle("Document Exemplar");
        invalidDocument.setContent("Content Exemplar");

        User user = new User();
        user.setId(1L);
        invalidDocument.setUser(user);

        Category category = new Category();
        category.setId(1L);
        invalidDocument.setCategory(category);

        mockMvc.perform(post("/api/documents/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDocument)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(("Erro: O departamento não pode estar vazio")));
    }

    @Test
    void saveDocumentWithoutCategory() throws Exception {
        Document invalidDocument = new Document();
        invalidDocument.setTitle("Document Exemplar");
        invalidDocument.setContent("Content Exemplar");

        User user = new User();
        user.setId(1L);
        invalidDocument.setUser(user);

        Department department = new Department();
        department.setId(1L);
        invalidDocument.setDepartment(department);

        mockMvc.perform(post("/api/documents/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDocument)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(("Erro: A categoria não deve estar vazia")));
    }


    @Test
    void findDocumentByIdSuccess() throws Exception {
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Document Example");

        when(documentService.findById(1L)).thenReturn(document);
        mockMvc.perform(get("/api/documents/find-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Document Example"));
    }

    @Test
    void findDocumentByIdNotFound() throws Exception {
        when(documentService.findById(anyLong())).thenThrow(new EntityNotFoundException("Documento não encontrado com id: 1"));

        mockMvc.perform(get("/api/documents/find-by-id/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Documento não encontrado com id: 1"));
    }

    @Test
    void findAllDocumentsSuccess() throws Exception {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document());

        when(documentService.findAll()).thenReturn(documents);
        mockMvc.perform(get("/api/documents/find-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void findAllDocumentsWithoutRecords() throws Exception {
        when(documentService.findAll()).thenThrow(new InternalException("Não há documentos registrados!"));

        mockMvc.perform(get("/api/documents/find-all"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Não há documentos registrados!"));
    }

    @Test
    void updateDocumentSuccess() throws Exception {
        Document updatedDocument = new Document();
        updatedDocument.setId(1L);
        updatedDocument.setTitle("Document Atualizado");

        when(documentService.updateById(anyLong(), any(Document.class))).thenReturn(updatedDocument);

        mockMvc.perform(put("/api/documents/update-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDocument)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Document Atualizado"));
    }

    @Test
    void updateDocumentNotFound() throws Exception {
        Document updatedDocument = new Document();
        updatedDocument.setTitle("Document Atualizado");

        when(documentService.updateById(anyLong(), any(Document.class))).thenThrow(new EntityNotFoundException("Documento não encontrado com id: 1"));

        mockMvc.perform(put("/api/documents/update-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDocument)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Documento não encontrado com id: 1"));
    }

    @Test
    void deleteDocumentByIdSuccess() throws Exception {
        when(documentService.deleteById(1L)).thenReturn("Documento excluído com sucesso");

        mockMvc.perform(delete("/api/documents/delete-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Documento excluído com sucesso"));
    }

    @Test
    void deleteDocumentByIdNotFound() throws Exception {
        when(documentService.deleteById(1L)).thenThrow(new EntityNotFoundException("Documento não encontrado com id: 1"));

        mockMvc.perform(delete("/api/documents/delete-by-id/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Documento não encontrado com id: 1"));
    }
}