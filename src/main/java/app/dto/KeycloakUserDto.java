package app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeycloakUserDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled = true;
    private List<CredentialDto> credentials = new ArrayList<>();
    private Map<String, List<String>> attributes = new HashMap<>();

    @Data
    public static class CredentialDto {
        private String type = "password";
        private String value;
        private boolean temporary = false;
    }

    public static KeycloakUserDto fromCreateUserDto(CreateUserDto dto) {
        KeycloakUserDto keycloakUser = new KeycloakUserDto();
        keycloakUser.setUsername(dto.getUsername());
        keycloakUser.setEmail(dto.getEmail());
        keycloakUser.setFirstName(dto.getFirstName());
        keycloakUser.setLastName(dto.getLastName());
        keycloakUser.setEnabled(dto.isEnabled());

        CredentialDto credential = new CredentialDto();
        credential.setValue(dto.getPassword());
        keycloakUser.getCredentials().add(credential);

        return keycloakUser;
    }

    public static KeycloakUserDto fromCreateUserDtoForUpdate(CreateUserDto dto) {
        KeycloakUserDto keycloakUser = new KeycloakUserDto();
        // Não inclui o username na atualização
        keycloakUser.setEmail(dto.getEmail());
        keycloakUser.setFirstName(dto.getFirstName());
        keycloakUser.setLastName(dto.getLastName());
        keycloakUser.setEnabled(dto.isEnabled());

        // Só inclui as credenciais se uma nova senha foi fornecida
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            CredentialDto credential = new CredentialDto();
            credential.setValue(dto.getPassword());
            keycloakUser.getCredentials().add(credential);
        }

        return keycloakUser;
    }
} 