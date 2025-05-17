package app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título obrigatório")
    private String title;

    @NotBlank(message = "Descrição não pode ser vazia")
    private String description;

    /**
     * Arquivo binário armazenado no banco
     */
    @Lob
    @Column(name = "filePath", columnDefinition = "LONGBLOB")
    private byte[] filePath;

    private String departmentName;

    /**
     * Nome de usuário que adicionou o documento (para fins de rastreamento)
     */
    private String addedBy;
}
