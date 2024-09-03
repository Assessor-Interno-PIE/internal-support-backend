package app.service;

import app.entity.Logs;
import app.repository.LogsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogsService {

    @Autowired
    private LogsRepository logsRepository;

    public String save(@Valid Logs logs) {
        logsRepository.save(logs);
        return "Log salvo com sucesso.";
    }

    public Logs findById(@Valid Long id) {
        return logsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log não encontrado com id: " + id));
    }

    public List<Logs> findAll() {
        List<Logs> logs = logsRepository.findAll();
        if (logs.isEmpty()) {
            throw new RuntimeException("Não há logs registrados!");
        } else {
            return logs;
        }
    }

    public String deleteById(@Valid Long id) {
        // Verifica se o log existe
        Logs log = logsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log não encontrado com id: " + id));
        // Agora pode deletar o log
        logsRepository.deleteById(id);
        return "Log deletado com sucesso.";
    }

    public Logs updateById(@Valid Long id, Logs updatedLogs) {
        return logsRepository.findById(id)
                .map(log -> {
                    log.setEndpoint(updatedLogs.getEndpoint());
                    log.setDocument(updatedLogs.getDocument());
                    log.setMethod(updatedLogs.getMethod());
                    log.setStatus(updatedLogs.getStatus());
                    log.setUser(updatedLogs.getUser());
                    return logsRepository.save(log);
                })
                .orElseThrow(() -> new RuntimeException("Log não encontrado com id: " + id));
    }
}
