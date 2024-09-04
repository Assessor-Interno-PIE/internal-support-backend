package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo 'nome' não pode estar em branco.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "O campo 'nome' deve conter apenas letras.")
    private String name;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Document> documents;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<User> users;

}
