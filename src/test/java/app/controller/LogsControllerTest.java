package app.controller;

import app.entity.Document;
import app.entity.Logs;
import app.entity.User;
import app.service.LogsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogsControllerTest {

    @InjectMocks
    private LogsController logsController;

    @Mock
    private LogsService logsService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(logsController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSaveLog() throws Exception {
        User user = new User();
        user.setId(1L);

        Document document = new Document();
        document.setId(1L);

        Logs log = new Logs();
        log.setEndpoint("/api/example");
        log.setMethod("POST");
        log.setStatus("SUCCESS");
        log.setUser(user);
        log.setDocument(document);

        when(logsService.save(any(Logs.class))).thenReturn("Log salvo com sucesso.");

        String logJson = objectMapper.writeValueAsString(log);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/logs/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Log salvo com sucesso."));

        verify(logsService).save(any(Logs.class));
    }

    @Test
    public void testFindById() throws Exception {
        User user = new User();
        user.setId(1L);

        Document document = new Document();
        document.setId(1L);

        Logs log = new Logs();
        log.setId(1L);
        log.setEndpoint("/api/example");
        log.setMethod("POST");
        log.setStatus("SUCCESS");
        log.setUser(user);
        log.setDocument(document);

        when(logsService.findById(anyLong())).thenReturn(log);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/logs/find-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(log)));

        verify(logsService).findById(1L);
    }

    @Test
    public void testFindAll() throws Exception {
        User user = new User();
        user.setId(1L);

        Document document = new Document();
        document.setId(1L);

        Logs log1 = new Logs();
        log1.setId(1L);
        log1.setEndpoint("/api/example1");
        log1.setMethod("POST");
        log1.setStatus("SUCCESS");
        log1.setUser(user);
        log1.setDocument(document);

        Logs log2 = new Logs();
        log2.setId(2L);
        log2.setEndpoint("/api/example2");
        log2.setMethod("GET");
        log2.setStatus("SUCCESS");
        log2.setUser(user);
        log2.setDocument(document);

        List<Logs> logs = Arrays.asList(log1, log2);

        when(logsService.findAll()).thenReturn(logs);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/logs/find-all"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(logs)));

        verify(logsService).findAll();
    }

    @Test
    public void testUpdateById() throws Exception {
        User user = new User();
        user.setId(1L);

        Document document = new Document();
        document.setId(1L);

        Logs updatedLog = new Logs();
        updatedLog.setId(1L);
        updatedLog.setEndpoint("/api/updated");
        updatedLog.setMethod("PUT");
        updatedLog.setStatus("UPDATED");
        updatedLog.setUser(user);
        updatedLog.setDocument(document);

        when(logsService.updateById(anyLong(), any(Logs.class))).thenReturn(updatedLog);

        String logJson = objectMapper.writeValueAsString(updatedLog);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/logs/update-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logJson))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedLog)));

        verify(logsService).updateById(eq(1L), argThat(log ->
                log.getEndpoint().equals(updatedLog.getEndpoint()) &&
                        log.getMethod().equals(updatedLog.getMethod()) &&
                        log.getStatus().equals(updatedLog.getStatus()) &&
                        log.getUser().getId().equals(updatedLog.getUser().getId()) &&
                        log.getDocument().getId().equals(updatedLog.getDocument().getId())
        ));
    }

    @Test
    public void testDeleteById() throws Exception {
        when(logsService.deleteById(anyLong())).thenReturn("Log deletado com sucesso.");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/logs/delete-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Log deletado com sucesso."));

        verify(logsService).deleteById(1L);
    }
}