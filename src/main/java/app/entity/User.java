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
    @Pattern(regexp = "^(\\S+\\s+\\S+)(\\s+\\S+)?$", message = "O nome deve ter pelo menos duas palavras e um espaço.")
    private String name;

    @Email(message = "Formato de e-mail incorreto")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco.")
    // pattern
    private String password;

    @NotNull(message = "O departamento não pode estar vazio")
    @ManyToOne
    @JsonIgnoreProperties("users")
    private Department department;

    @ManyToMany
    @JsonIgnoreProperties("viewed")
    private List<Document> viewed;

    @NotNull(message = "O nível de acesso não deve estar vazio")
    @ManyToOne
    @JsonIgnoreProperties("users")
    private AccessLevel accessLevel;

}
