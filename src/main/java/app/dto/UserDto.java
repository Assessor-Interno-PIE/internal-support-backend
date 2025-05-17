package app.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Map<String, List<String>> attributes;
    private List<String> groups;
    private List<String> roles;
} 