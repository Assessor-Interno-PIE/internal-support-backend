package app.repository;

import app.entity.Department;
import app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDepartment(Department department);
    List<User> findByNameContaining(String keyword);
    User findByUsername(String username);
}
