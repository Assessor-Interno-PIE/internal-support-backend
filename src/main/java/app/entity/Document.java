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
@Table(name = "documents")
@EntityListeners(app.component.AuditListener.class)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título não pode estar vazio")
    @Column(nullable = false)
    private String title;

    @Lob
    @Column(columnDefinition = "LONGBLOB", nullable = false)
    private byte[] filePath;

    @Column(nullable = false)
    private String groupId;

    @NotBlank(message = "Descrição nao pode estar vazia")
    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String addedBy;

}
