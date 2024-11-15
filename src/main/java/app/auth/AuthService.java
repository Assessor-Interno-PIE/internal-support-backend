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

}
