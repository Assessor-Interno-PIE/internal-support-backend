package app.service;

import app.entity.AccessLevel;
import app.repository.AccessLevelRepository;
import com.sun.jdi.InternalException;
import jakarta.persistence.EntityNotFoundException;
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

    public AccessLevel findById(Long id) {
        return accessLevelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nível de acesso não encontrado com id: " + id));
    }

    public List<AccessLevel> findAll() {
        List<AccessLevel> accessLevels = accessLevelRepository.findAll();
        if (accessLevels.isEmpty()) {
            throw new InternalException("Não há niveis de acesso registrados!");
        } else {
            return accessLevels;
        }
    }

    public String deleteById(@Valid long id) {
        // Verifica se o nível existe
        AccessLevel accessLevel = accessLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nível de acesso não encontrado com id: " + id));
        // Agora pode deletar o nível
        accessLevelRepository.deleteById(id);
        return "Nível de acesso deletado com sucesso";
    }

    public AccessLevel updateById(@Valid Long id, AccessLevel updatedAccessLevel) {
        return accessLevelRepository.findById(id)
                .map(accessLevel -> {
                    accessLevel.setName(updatedAccessLevel.getName());
                    accessLevel.setUsers(updatedAccessLevel.getUsers());
                    return accessLevelRepository.save(accessLevel);
                })
                .orElseThrow(() -> new RuntimeException("Nivel de acesso não encontrado com id: " + id));
    }
}
