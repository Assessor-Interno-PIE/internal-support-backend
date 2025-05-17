package app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserDto {
    @NotBlank(message = "Username é obrigatório")
    private String username;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Nome é obrigatório")
    private String firstName;
    
    @NotBlank(message = "Sobrenome é obrigatório")
    private String lastName;
    
    @NotBlank(message = "Senha é obrigatória")
    private String password;
    
    private boolean enabled = true;
} 