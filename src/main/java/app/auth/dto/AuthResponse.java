package app.auth.dto;

import java.util.Map;

/**
 * DTO usado para encapsular a resposta de autenticação (tokens e afins).
 */
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public AuthResponse(Map<String, Object> tokenData) {
        this.accessToken = (String) tokenData.get("access_token");
        this.refreshToken = (String) tokenData.get("refresh_token");
        Object expires = tokenData.get("expires_in");
        this.expiresIn = expires instanceof Number ? ((Number) expires).longValue() : 0;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
