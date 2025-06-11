package app.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    // Getters e Setters
    private String username;
    private String password;

}