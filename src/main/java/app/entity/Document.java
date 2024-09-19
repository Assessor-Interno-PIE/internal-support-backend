package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título não pode estar vazio")
    private String title;

    @NotBlank(message = "O conteúdo não pode estar vazio")
    private String content;

    @NotNull(message = "O departamento não pode estar vazio")
    @ManyToOne
    @JsonIgnoreProperties("documents")
    private Department department;

    @NotNull(message = "A categoria não deve estar vazia")
    @ManyToOne
    @JsonIgnoreProperties({"documents"})
    private Category category;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    @NotNull(message = "O usuário não deve estar vazio")
    @ManyToOne
    @JsonIgnoreProperties("documents")
    private User user;

}
