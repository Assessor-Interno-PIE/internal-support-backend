package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("users")
    private Department department;

    @ManyToMany
    @JsonIgnoreProperties("viewed")
    private List<Document> viewed;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("users")
    private AccessLevel accessLevel;

}
