package app.auth;

import java.util.Optional;

import app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LoginRepository extends JpaRepository<User, Long>{
	public Optional<User> findByUsername(String login);
}
