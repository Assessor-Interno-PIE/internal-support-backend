package app.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

        private Long id;
        private String name;
        private String email;
        private String password;
        private DepartmentDTO departmentDTO;
        private AccessLevelDTO accessLevelDTO;
        private List<Long> viewedDocumentIds;

}
