package app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] filePath;

    private String departmentName;

    @NotBlank(message = "Descrição nao pode estar vazia")
    private String description;

    private String addedBy;


}
