package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo 'nome' não pode estar em branco.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "O campo 'nome' deve conter apenas letras.")
    private String name;

    @OneToMany(mappedBy = "category")
    @JsonIgnoreProperties({"category"})
    private List<Document> documents;

}
