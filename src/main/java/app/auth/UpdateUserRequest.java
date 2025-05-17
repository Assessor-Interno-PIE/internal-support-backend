package app.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
        private String name;
        private String password;
        private Department department;
        private Integer isAdmin;
}
