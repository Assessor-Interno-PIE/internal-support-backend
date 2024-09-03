package app.service;

import app.entity.User;
import app.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String save(@Valid User user) {
        userRepository.save(user);
        return "Usuário salvo com sucesso.";
    }

    public User findById(@Valid Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new RuntimeException("Não há usuários registrados!");
        } else {
            return users;
        }
    }

    public String deleteById(@Valid Long id) {
        // Verifica se o usuário existe
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        // Agora pode deletar o usuário
        userRepository.deleteById(id);
        return "Usuário deletado com sucesso.";
    }

    public User updateById(@Valid Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setEmail(updatedUser.getEmail());
                    user.setPassword(updatedUser.getPassword());
                    user.setDepartment(updatedUser.getDepartment());
                    user.setViewed(updatedUser.getViewed());
                    user.setAccessLevel(updatedUser.getAccessLevel());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }
}
