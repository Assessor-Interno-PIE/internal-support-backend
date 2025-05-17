package app.auth;

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