package app.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class AuthResponse {
    // Getters e Setters
    private String access_token;
    private String refresh_token;
    private String token_type;
    private Integer expires_in;
    private String scope;

    public AuthResponse(Map<String, Object> tokenResponse) {
        this.access_token = (String) tokenResponse.get("access_token");
        this.refresh_token = (String) tokenResponse.get("refresh_token");
        this.token_type = (String) tokenResponse.get("token_type");
        this.expires_in = tokenResponse.get("expires_in") instanceof Integer ?
                (Integer) tokenResponse.get("expires_in") :
                Integer.parseInt(tokenResponse.get("expires_in").toString());
        this.scope = (String) tokenResponse.get("scope");
    }

}