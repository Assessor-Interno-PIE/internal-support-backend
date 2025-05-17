package app.exception.handler;

public class KeycloakException extends RuntimeException {
    public KeycloakException(String message) {
        super(message);
    }
}