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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("documents")
    private Department department;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("documents")
    private Category category;

    @NotNull
    private Timestamp createdAt;

    @NotNull
    private Timestamp updatedAt;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("documents")
    private User user;

}
