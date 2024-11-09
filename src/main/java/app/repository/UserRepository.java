package app.repository;

import app.entity.Department;
import app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDepartment(Department department);
}
