package app.controller;

import app.entity.Department;
import app.entity.Document;
import app.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    public void testSaveDocument() throws Exception {
        Department department = new Department();
        department.setId(1L);

        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Document");
        document.setDepartment(department);
        document.setDescription("Test Description");
        document.setFilePath("test/path/to/file");

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());

        when(documentService.save(any(MultipartFile.class), eq(1L), any(String.class), any(String.class))).thenReturn(document);

        mockMvc.perform(multipart("/api/documents/save")
                        .file(file)
                        .param("departmentId", "1")
                        .param("title", "Test Document")
                        .param("description", "Test Description"))
                .andExpect(status().isOk())
                .andExpect(content().string("Arquivo salvo com sucesso! ID do documento: 1"));

        verify(documentService, times(1)).save(any(MultipartFile.class), eq(1L), any(String.class), any(String.class));
    }

    @Test
    public void testFindById() throws Exception {
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Document");

        when(documentService.findById(1L)).thenReturn(document);

        mockMvc.perform(get("/api/documents/find-by-id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"title\":\"Test Document\"}"));

        verify(documentService, times(1)).findById(1L);
    }

    @Test
    public void testFindAll() throws Exception {
        Document document1 = new Document();
        document1.setId(1L);
        document1.setTitle("Document One");

        Document document2 = new Document();
        document2.setId(2L);
        document2.setTitle("Document Two");

        when(documentService.findAll()).thenReturn(List.of(document1, document2));

        mockMvc.perform(get("/api/documents/find-all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"title\":\"Document One\"},{\"id\":2,\"title\":\"Document Two\"}]"));

        verify(documentService, times(1)).findAll();
    }

    @Test
    public void testGetDocumentsByDepartment() throws Exception {
        Department department = new Department();
        department.setId(1L);

        Document document = new Document();
        document.setId(1L);
        document.setTitle("Department Document");

        when(documentService.findDocumentsByDepartment(1L)).thenReturn(List.of(document));

        mockMvc.perform(get("/api/documents/by-department/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"title\":\"Department Document\"}]"));

        verify(documentService, times(1)).findDocumentsByDepartment(1L);
    }

    @Test
    public void testDeleteDocument() throws Exception {
        Long documentId = 1L;
        doNothing().when(documentService).deleteDocumentById(documentId);

        mockMvc.perform(delete("/api/documents/{id}", documentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Documento deletado com sucesso!"));

        verify(documentService, times(1)).deleteDocumentById(documentId);
    }

    @Test
    public void testUpdateDocument() throws Exception {
        Document updatedDocument = new Document();
        updatedDocument.setId(1L);
        updatedDocument.setTitle("Updated Document");

        MockMultipartFile file = new MockMultipartFile("file", "updated.pdf", MediaType.APPLICATION_PDF_VALUE, "updated content".getBytes());

        when(documentService.updateDocument(eq(1L), any(MultipartFile.class), any(String.class), any(String.class), any(Long.class))).thenReturn(updatedDocument);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/documents/edit/1")
                        .file(file)
                        .param("title", "Updated Document")
                        .param("description", "Updated Description")
                        .param("departmentId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Documento atualizado com sucesso! ID do documento: 1"));

        verify(documentService, times(1)).updateDocument(eq(1L), any(MultipartFile.class), any(String.class), any(String.class), any(Long.class));
    }


    @Test
    public void testGetDocumentsByTitleContaining() throws Exception {
        Document document = new Document();
        document.setId(1L);
        document.setTitle("Test Document");

        when(documentService.findDocumentsByTitleContaining("Test")).thenReturn(List.of(document));

        mockMvc.perform(get("/api/documents/search/title-contains")
                        .param("keyword", "Test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"title\":\"Test Document\"}]"));

        verify(documentService, times(1)).findDocumentsByTitleContaining("Test");
    }

    @Test
    public void testSaveDocumentFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());

        when(documentService.save(any(MultipartFile.class), eq(1L), any(String.class), any(String.class)))
                .thenThrow(new IOException("Erro ao salvar"));

        mockMvc.perform(multipart("/api/documents/save")
                        .file(file)
                        .param("departmentId", "1")
                        .param("title", "Test Document")
                        .param("description", "Test Description"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro ao salvar o arquivo: Erro ao salvar"));

        verify(documentService, times(1)).save(any(MultipartFile.class), eq(1L), any(String.class), any(String.class));
    }

    @Test
    public void testFindByIdNotFound() throws Exception {
        when(documentService.findById(1L)).thenThrow(new RuntimeException("Documento não encontrado"));

        mockMvc.perform(get("/api/documents/find-by-id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(documentService, times(1)).findById(1L);
    }

    @Test
    public void testDownloadDocumentNotFound() throws Exception {
        when(documentService.downloadFile(1L)).thenThrow(new IOException("Documento não encontrado"));

        mockMvc.perform(get("/api/documents/download/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(documentService, times(1)).downloadFile(1L);
    }


    @Test
    public void testDeleteDocumentFailure() throws Exception {
        Long documentId = 1L;
        doThrow(new IllegalArgumentException("ID invalido")).when(documentService).deleteDocumentById(documentId);

        mockMvc.perform(delete("/api/documents/{id}", documentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ID invalido"));

        verify(documentService, times(1)).deleteDocumentById(documentId);
    }

    @Test
    public void testUpdateDocumentFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "updated.pdf", MediaType.APPLICATION_PDF_VALUE, "updated content".getBytes());

        when(documentService.updateDocument(eq(1L), any(MultipartFile.class), any(String.class), any(String.class), any(Long.class)))
                .thenThrow(new RuntimeException("Erro ao atualizar"));

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/documents/edit/1")
                        .file(file)
                        .param("title", "Updated Document")
                        .param("description", "Updated Description")
                        .param("departmentId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro ao atualizar o documento: Erro ao atualizar"));

        verify(documentService, times(1)).updateDocument(eq(1L), any(MultipartFile.class), any(String.class), any(String.class), any(Long.class));
    }

    @Test
    public void testViewDocumentFound() throws Exception {
        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn("document.pdf");

        when(documentService.downloadFile(1L)).thenReturn(mockResource);

        mockMvc.perform(get("/api/documents/view/1")
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"document.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        verify(documentService, times(1)).downloadFile(1L);
    }


    @Test
    public void testViewDocumentNotFound() throws Exception {
        when(documentService.downloadFile(1L)).thenThrow(new IOException("Documento não encontrado"));

        mockMvc.perform(get("/api/documents/view/1")
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(documentService, times(1)).downloadFile(1L);
    }

    @Test
    public void testDownloadDocumentSuccess() throws Exception {
        Resource mockResource = mock(Resource.class);
        when(mockResource.getFilename()).thenReturn("document.pdf");

        when(documentService.downloadFile(1L)).thenReturn(mockResource);

        mockMvc.perform(get("/api/documents/download/1")
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        verify(documentService, times(1)).downloadFile(1L);
    }


}
