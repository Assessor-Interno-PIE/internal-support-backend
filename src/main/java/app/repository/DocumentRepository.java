package app.repository;

import app.entity.Department;
import app.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByDepartment(Department department);
    List<Document> findByTitleContaining(String keyword);
}
