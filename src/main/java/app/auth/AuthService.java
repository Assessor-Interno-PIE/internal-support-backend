//AuthenticationService.java
package app.auth;

import app.entity.User;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import app.config.JwtServiceGenerator;

@Service
public class AuthService {
	
	@Autowired
	private AuthRepository repository;
	@Autowired
	private JwtServiceGenerator jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;


	public String userLogin(Login login) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						login.getUsername(),
						login.getPassword()
						)
				);
		User user = repository.findByUsername(login.getUsername()).get();

        return jwtService.generateToken(user);
	}

	public String userRegister(Registration registration) {

		User user = new User();
		user.setName(registration.getName());
		user.setUsername(registration.getUsername());
		user.setPassword(passwordEncoder.encode(registration.getPassword()));
		user.setDepartment(registration.getDepartment());
		user.setIsAdmin(0);

		userRepository.save(user);
		String token = jwtService.generateToken(user);
		System.out.println("Token gerado: " + token);  // Adicione este log para depuração

		return token;
	}

	public User updateById(Long id, UpdateUserRequest updateUserRequest) {
		return userRepository.findById(id)
				.map(user -> {
					if (updateUserRequest.getName() != null) {
						user.setName(updateUserRequest.getName());
					}
					if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
						user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
					}
					if (updateUserRequest.getDepartment() != null) {
						user.setDepartment(updateUserRequest.getDepartment());
					}
					if (updateUserRequest.getIsAdmin() != null) {
						user.setIsAdmin(updateUserRequest.getIsAdmin());
					}
					return userRepository.save(user);
				})
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
	}

}
