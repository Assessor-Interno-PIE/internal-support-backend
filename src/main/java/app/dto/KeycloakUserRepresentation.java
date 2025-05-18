package app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class KeycloakUserRepresentation {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled = true;
    private List<UserCredential> credentials = new ArrayList<>();
    private Map<String, List<String>> attributes = new HashMap<>();

    @Data
    public static class UserCredential {
        private String type = "password";
        private String value;
        private boolean temporary = false;

        @JsonProperty("type")
        public String getType() {
            return type;
        }

        @JsonProperty("value")
        public String getValue() {
            return value;
        }

        @JsonProperty("temporary")
        public boolean isTemporary() {
            return temporary;
        }
    }

    public static KeycloakUserRepresentation buildFromCreateUser(CreateUserDto userDto) {
        KeycloakUserRepresentation user = new KeycloakUserRepresentation();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(userDto.isEnabled());

        UserCredential credential = new UserCredential();
        credential.setValue(userDto.getPassword());
        credential.setTemporary(false);
        user.getCredentials().add(credential);

        return user;
    }

    public static KeycloakUserRepresentation buildForUpdate(CreateUserDto userDto) {
        KeycloakUserRepresentation user = new KeycloakUserRepresentation();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(userDto.isEnabled());

        String password = userDto.getPassword();
        if (password != null && !password.trim().isEmpty()) {
            UserCredential credential = new UserCredential();
            credential.setValue(password);
            credential.setTemporary(false);
            user.getCredentials().add(credential);
        }

        return user;
    }
}