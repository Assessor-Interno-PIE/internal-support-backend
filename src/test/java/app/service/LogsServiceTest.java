package app.service;

import app.entity.Document;
import app.entity.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogsServiceTest {

    @Mock
    private LogsRepository logsRepository;

    @InjectMocks
    private LogsService logsService;

    @Test
    void testSaveLog(){
        Logs log = new Logs();

        //log falso pro teste
        log.setEndpoint("/api/test");
        log.setDocument(new Document());
        log.setMethod("GET");
        log.setStatus("sucess");
        log.setUser(new User());

        String result = logsService.save(log);

        assertEquals("Log salvo com sucesso.", result);

        //verifica se usou o metodo save conforme esperado
        verify(logsRepository, times(1)).save(log);
    }

    @Test
    void findLogWithExistingID(){
        Logs log = new Logs();
        log.setId(1L);
        log.setEndpoint("api/test");

        when(logsRepository.findById(1L)).thenReturn(Optional.of(log));

        Logs result = logsService.findById(1L);

        assertEquals(log.getEndpoint(), result.getEndpoint());

        //verifica se usou o metodo findbyid
        verify(logsRepository, times(1)).findById(1L);

    }

    @Test
    void findLogWithInvalidID() {
        when(logsRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            logsService.findById(1L);
        });

        assertEquals("Log não encontrado com id: 1", exception.getMessage());
        //verifica se usou o metodo findbyid
        verify(logsRepository, times(1)).findById(1L);
    }

    @Test
    void findAllLogs() {
        Logs log1 = new Logs();
        log1.setId(1L);
        Logs log2 = new Logs();
        log2.setId(2L);
        List<Logs> logsList = Arrays.asList(log1, log2);

        when(logsRepository.findAll()).thenReturn(logsList);

        List<Logs> result = logsService.findAll();

        assertEquals(2, result.size());
        //verifica se usou o metodo findall
        verify(logsRepository, times(1)).findAll();
    }

    @Test
    void findAllLogsEmpty() {
        when(logsRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            logsService.findAll();
        });

        assertEquals("Não há logs registrados!", exception.getMessage());
        //verifica se usou o metodo findall
        verify(logsRepository, times(1)).findAll();
    }

    @Test
    void deleteLogWithValidID() {
        Logs log = new Logs();
        log.setId(1L);

        when(logsRepository.findById(1L)).thenReturn(Optional.of(log));

        String result = logsService.deleteById(1L);

        assertEquals("Log deletado com sucesso.", result);
        verify(logsRepository, times(1)).findById(1L);
        verify(logsRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteLogWithInvalidID() {
        when(logsRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            logsService.deleteById(1L);
        });

        assertEquals("Log não encontrado com id: 1", exception.getMessage());
        verify(logsRepository, times(1)).findById(1L);
    }

    @Test
    void updateLogWithValidID() {
        Logs existingLog = new Logs();
        existingLog.setId(1L);
        existingLog.setEndpoint("/api/velha");

        Logs updatedLog = new Logs();
        updatedLog.setEndpoint("/api/nova");

        when(logsRepository.findById(1L)).thenReturn(Optional.of(existingLog));
        when(logsRepository.save(existingLog)).thenReturn(existingLog);

        Logs result = logsService.updateById(1L, updatedLog);

        assertEquals("/api/nova", result.getEndpoint());
        verify(logsRepository, times(1)).save(existingLog);
    }

    @Test
    void updateLogWithInvalidID() {
        Logs updatedLog = new Logs();

        when(logsRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            logsService.updateById(1L, updatedLog);
        });

        assertEquals("Log não encontrado com id: 1", exception.getMessage());
    }


}
