package app.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogsDTO {

    private Long id;
    private String endpoint;
    private String method;
    private String status;
    private UserDTO userDTO;
    private DocumentDTO documentDTO;

}
