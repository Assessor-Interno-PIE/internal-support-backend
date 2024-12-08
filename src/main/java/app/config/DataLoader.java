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
        Department defaultDepartment = departmentRepository.findById(1L).orElseGet(() -> {
            Department dept = new Department();
            dept.setId(1L);
            dept.setName("Default Department");
            return departmentRepository.save(dept);
        });

        if (userRepository.findByUsername("admin") == null) {
            User user = new User();
            user.setName("Admin User");
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setIsAdmin(1);
            user.setDepartment(defaultDepartment);
            userRepository.save(user);
        }
        if (userRepository.findByUsername("user") == null) {
            User user = new User();
            user.setName("Common User");
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setIsAdmin(0);
            user.setDepartment(defaultDepartment);
            userRepository.save(user);
        }
    }
}
