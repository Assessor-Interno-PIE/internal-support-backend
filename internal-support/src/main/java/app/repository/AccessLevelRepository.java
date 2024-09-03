package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import app.entity.AccessLevel;

public interface AccessLevelRepository extends JpaRepository<AccessLevel, Long> {

}
