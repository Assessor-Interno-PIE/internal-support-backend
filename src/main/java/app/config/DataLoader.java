package app.config;

import app.entity.Department;
import app.entity.User;
import app.repository.DepartmentRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se o departamento já existe, caso contrário, cria um novo
        Department defaultDepartment = departmentRepository.findById(1L).orElseGet(() -> {
            Department dept = new Department();
            dept.setId(1L);
            dept.setName("Default Department");
            return departmentRepository.save(dept);  // Salva o departamento
        });

        // Verificar se o usuário admin já existe, caso contrário, cria um novo
        if (userRepository.findByUsername("admin") == null) {
            User user = new User();
            user.setName("Admin User");
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setIsAdmin(1);
            user.setDepartment(defaultDepartment);  // Associa o departamento
            userRepository.save(user);  // Salva o usuário
        }
    }
}
