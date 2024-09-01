package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Logs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String endpoint;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("logs")
    private Document document;

    @NotBlank
    private String method;

    @NotBlank
    private String status;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("logs")
    private User user;

}
