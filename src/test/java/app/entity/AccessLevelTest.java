package app.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccessLevelTest {

    private final Validator validator;

    public AccessLevelTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidAccessLevel() {
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setName("Nível de Acesso Válido");
        accessLevel.setUsers(List.of());

        Set<ConstraintViolation<AccessLevel>> violations = validator.validate(accessLevel);
        assertTrue(violations.isEmpty(), "O nível de acesso deve ser válido");
    }

    @Test
    public void testInvalidAccessLevelWithEmptyName() {
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setName(""); // Empty name
        accessLevel.setUsers(List.of());

        Set<ConstraintViolation<AccessLevel>> violations = validator.validate(accessLevel);
        assertFalse(violations.isEmpty(), "O nível de acesso deve ser inválido com nome vazio");
        assertEquals(2, violations.size()); // 2 violations because of @NotBlank and @Pattern
    }

    @Test
    public void testInvalidAccessLevelWithInvalidName() {
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setName("Acesso123"); // Name with numbers
        accessLevel.setUsers(List.of());

        Set<ConstraintViolation<AccessLevel>> violations = validator.validate(accessLevel);
        assertFalse(violations.isEmpty(), "O nível de acesso deve ser inválido com nome contendo números");
        assertEquals(1, violations.size());
        assertEquals("O nome deve conter apenas letras", violations.iterator().next().getMessage());
    }
}
