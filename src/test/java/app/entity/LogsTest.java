package app.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LogsTest {

    private final Validator validator;

    public LogsTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidLogs() {
        Logs logs = new Logs();
        logs.setEndpoint("/api/example");
        logs.setMethod("GET");
        logs.setStatus("SUCCESS");
        logs.setDocument(new Document());
        logs.setUser(new User());

        Set<ConstraintViolation<Logs>> violations = validator.validate(logs);
        assertTrue(violations.isEmpty(), "Os logs devem ser válidos");
    }

    @Test
    public void testInvalidLogsWithEmptyEndpoint() {
        Logs logs = new Logs();
        logs.setEndpoint("");
        logs.setMethod("GET");
        logs.setStatus("SUCCESS");
        logs.setDocument(new Document());
        logs.setUser(new User());

        Set<ConstraintViolation<Logs>> violations = validator.validate(logs);
        assertFalse(violations.isEmpty(), "Os logs devem ser inválidos com endpoint vazio");
        assertEquals(1, violations.size());
        assertEquals("endpoint não deve estar vazio", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidLogsWithNullDocument() {
        Logs logs = new Logs();
        logs.setEndpoint("/api/example");
        logs.setMethod("GET");
        logs.setStatus("SUCCESS");
        logs.setDocument(null);
        logs.setUser(new User());

        Set<ConstraintViolation<Logs>> violations = validator.validate(logs);
        assertFalse(violations.isEmpty(), "Os logs devem ser inválidos com documento nulo");
        assertEquals(1, violations.size());
    }

    @Test
    public void testInvalidLogsWithNullUser() {
        Logs logs = new Logs();
        logs.setEndpoint("/api/example");
        logs.setMethod("GET");
        logs.setStatus("SUCCESS");
        logs.setDocument(new Document());
        logs.setUser(null);

        Set<ConstraintViolation<Logs>> violations = validator.validate(logs);
        assertFalse(violations.isEmpty(), "Os logs devem ser inválidos com usuário nulo");
        assertEquals(1, violations.size());
    }
}
