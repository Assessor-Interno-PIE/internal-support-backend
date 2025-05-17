package app.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO simplificado para resposta de token do Keycloak.
 */
@Setter
@Getter
public class TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

}
