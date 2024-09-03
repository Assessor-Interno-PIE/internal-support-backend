package app.service;

import app.entity.Logs;
import app.repository.LogsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LogsService {

    @Autowired
    private LogsRepository logsRepository;

    public String save(@Valid Logs logs){
        logsRepository.save(logs);
        return "Log salva com sucesso";
    }

    public Logs findById(@Valid Long id){
        if(logsRepository.existsById(id)){
            Optional<Logs> logs = logsRepository.findById(id);
            return logs.get();
        }else{
            throw new RuntimeException("Log nao encontrada com id: "+id);
        }
    }

    public List<Logs> findAll(){
        List<Logs> logs = logsRepository.findAll();
        if(logs.isEmpty()){
            throw new RuntimeException("Não há logs registradas!");
        }else{
            return logs;
        }
    }

    public void deleteById(@Valid Long id){
        // verifica se a log existe
        Logs logs = logsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log não encontrada com id: " + id));
        // agora pode deletar o log
        logsRepository.deleteById(id);
    }

    public Logs updateById(@Valid Long id, Logs updatedLogs){
        return logsRepository.findById(id)
                .map(logs -> { // tipo um for
                    logs.setEndpoint(updatedLogs.getEndpoint());
                    logs.setDocument(updatedLogs.getDocument());
                    logs.setMethod(updatedLogs.getMethod());
                    logs.setStatus(updatedLogs.getStatus());
                    logs.setUser(updatedLogs.getUser());
                    return logsRepository.save(logs);
                })
                .orElseThrow(()-> new RuntimeException("Log não encontrado com id: " + id));
    }
}
