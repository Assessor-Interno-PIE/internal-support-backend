package app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotEmpty(message = "O email não pode ser vazio")
    @Email(message = "O email fornecido não é válido")
    private String email;

    @NotEmpty(message = "O nome é obrigatório")
    private String firstName;

    @NotEmpty(message = "O sobrenome é obrigatório")
    private String lastName;

    private String password; // Senha opcional para atualização

    private boolean enabled = true;
}