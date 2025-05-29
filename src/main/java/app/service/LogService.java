package app.service;

import app.entity.Log;
import app.repository.elasticsearch.ElasticLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {
    @Autowired
    ElasticLogRepository logRepository;

    public void registrar(String acao, String usuario, String endpoint, String descricao) {
        Log log = new Log();
        log.setAcao(acao);
        log.setUsuario(usuario);
        log.setDataHora(LocalDateTime.now().toString());
        log.setEndpoint(endpoint);
        log.setDescricao(descricao);

        logRepository.save(log);
    }
}
