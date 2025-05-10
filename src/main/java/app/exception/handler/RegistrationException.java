package app.exception.handler;

public class RegistrationException extends RuntimeException {
  public RegistrationException(String message) {
    super(message);
  }
}