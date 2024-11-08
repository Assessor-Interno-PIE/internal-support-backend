package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome não deve estar vazio")
    private String name;

    @NotBlank(message = "O nome de usuário não pode ser vazio")
    private String username;

    @NotBlank(message = "A senha não pode estar em branco.")
    private String password;

    @NotNull(message = "O departamento não pode estar vazio")
    @ManyToOne
    @JsonIgnoreProperties("users")
    private Department department;

    @NotNull(message = "isAdmin é inválido")
    private Number isAdmin;
}