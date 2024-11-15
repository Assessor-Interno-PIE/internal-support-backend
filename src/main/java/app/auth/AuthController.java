package app.auth;

import app.config.JwtServiceGenerator;
import app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtServiceGenerator jwtService;


    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@RequestBody Login login) {
        try {
            return ResponseEntity.ok(authService.userLogin(login));
        }catch(AuthenticationException ex) {
            System.out.println(ex.getMessage());
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping("/register")
//    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User input) {
//        return ResponseEntity.ok("HI");
//    }

}
