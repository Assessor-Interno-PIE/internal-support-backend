package app.auth;

import app.entity.Department;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registration {
    private String name;
    private String username;
    private String password;
    private Department department;
}