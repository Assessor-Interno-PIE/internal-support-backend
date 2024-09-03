package app.service;

import app.entity.AccessLevel;
import app.repository.AccessLevelRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccessLevelService {

    @Autowired
    private AccessLevelRepository accessLevelRepository;

    public String save(@Valid AccessLevel accessLevel) {
        accessLevelRepository.save(accessLevel);
        return "Nivel de acesso salvo com sucesso";
    }

    public AccessLevel findById(@Valid Long id) {
        if (accessLevelRepository.existsById(id)) {
            Optional<AccessLevel> accessLevel = accessLevelRepository.findById(id);
            return accessLevel.get();
        } else {
            throw new RuntimeException("Nivel de acesso nao encontrada com id: " + id);
        }
    }

    public List<AccessLevel> findAll() {
        List<AccessLevel> accessLevels = accessLevelRepository.findAll();
        if (accessLevels.isEmpty()) {
            throw new RuntimeException("Não há niveis de acesso registrados!");
        } else {
            return accessLevels;
        }
    }

    public void deleteById(@Valid Long id) {
        // verifica se o nivel existe
        AccessLevel accessLevel = accessLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nivel de acesso não encontrada com id: " + id));
        // agora pode deletar o nivel
        accessLevelRepository.deleteById(id);
    }

    public AccessLevel updateById(@Valid Long id, AccessLevel updatedAccessLevel) {
        return accessLevelRepository.findById(id)
                .map(accessLevel -> {
                    accessLevel.setName(updatedAccessLevel.getName());
                    accessLevel.setUsers(updatedAccessLevel.getUsers());
                    return accessLevelRepository.save(accessLevel);
                })
                .orElseThrow(() -> new RuntimeException("Nivel de acesso não encontrada com id: " + id));
    }
}
