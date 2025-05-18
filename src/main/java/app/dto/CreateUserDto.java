package app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateUserDto {

    @NotEmpty(message = "O username não pode ser vazio")
    private String username;

    @NotEmpty(message = "O email é obrigatório")
    @Email(message = "O email fornecido não é válido")
    private String email;

    @NotEmpty(message = "O nome é obrigatório")
    private String firstName;

    @NotEmpty(message = "O sobrenome é obrigatório")
    private String lastName;

    @NotEmpty(message = "A senha deve ser fornecida")
    private String password;

    private boolean enabled = true;
}