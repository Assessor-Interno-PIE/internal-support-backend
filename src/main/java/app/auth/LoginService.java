//AuthenticationService.java
package app.auth;

import app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import app.config.JwtServiceGenerator;

@Service
public class LoginService {
	
	@Autowired
	private LoginRepository repository;
	@Autowired
	private JwtServiceGenerator jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;


	public String userLogin(Login login) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						login.getUsername(),
						login.getPassword()
						)
				);
		User user = repository.findByUsername(login.getUsername()).get();
		String jwtToken = jwtService.generateToken(user);
		
		return jwtToken;
	}

}
