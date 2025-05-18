package app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserDto {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "Nome é obrigatório")
    private String firstName;
    
    @NotBlank(message = "Sobrenome é obrigatório")
    private String lastName;
    
    private String password; // Senha opcional para atualização
    
    private boolean enabled = true;
} 