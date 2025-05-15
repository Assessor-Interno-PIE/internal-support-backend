package app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupDTO {
    // Getters and Setters
    private String id;

    @NotBlank(message = "Nome obrigatório")
    private String name;

    // Constructors
    public GroupDTO() {}

    public GroupDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

}