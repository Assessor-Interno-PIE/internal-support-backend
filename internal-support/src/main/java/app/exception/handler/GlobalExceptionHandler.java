package app.exception.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0); // Pega o primeiro erro
        String errorMessage = "Erro: " + fieldError.getDefaultMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("Erro de validação");
        return new ResponseEntity<>("Erro: " + errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<String> handleTransactionSystemException(TransactionSystemException ex) {
        if (ex.getRootCause() instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex.getRootCause();
            String errorMessage = constraintViolationException.getConstraintViolations().stream()
                    .map(violation -> violation.getMessage())
                    .findFirst()
                    .orElse("Erro de validação");
            return new ResponseEntity<>("Erro: " + errorMessage, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Erro interno do sistema", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("Erro interno: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
