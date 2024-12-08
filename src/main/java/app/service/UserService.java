package app.service;

import app.entity.Department;
import app.entity.Document;
import app.entity.User;
import app.repository.DepartmentRepository;
import app.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    public String save(User user) {
        if (!departmentRepository.existsById(user.getDepartment().getId())) {
            throw new IllegalArgumentException("Departamento não encontrado.");
        }
        userRepository.save(user);
        return "Usuário salvo com sucesso.";
    }

    public User findById(Long id) {
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

    public Page<User> findAllPaginated(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new RuntimeException("Não há usuários registrados!");
        }
        return users;
    }

    public String deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
        userRepository.deleteById(id);
        return "Usuario deletado com sucesso.";
    }

    public User updateById(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setPassword(updatedUser.getPassword());
                    user.setDepartment(updatedUser.getDepartment());
                    user.setIsAdmin(updatedUser.getIsAdmin());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    public List<User> findUsersByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Departamento não encontrado"));
        return userRepository.findByDepartment(department);
    }

    public List<User> findUsersByNameContaining(String keyword) {
        return userRepository.findByNameContaining(keyword);
    }

}
