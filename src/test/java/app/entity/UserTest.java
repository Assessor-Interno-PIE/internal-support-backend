package app.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final Validator validator;

    public UserTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidUser() {
        User user = new User();
        user.setName("Gilmar da Silva");
        user.setEmail("exemplo@exemplo");
        user.setPassword("senha123");
        user.setDepartment(new Department());
        user.setAccessLevel(new AccessLevel());

        //Error Validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Usuarío deve ser válido");
    }

    @Test
    public void testInvalidUserWithoutName() {
        User user = new User();
        user.setEmail("exemplo@exemplo");
        user.setPassword("senha123");
        user.setDepartment(new Department());
        user.setAccessLevel(new AccessLevel());

        //Error Validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "O usuário deve ser inválido sem nome");
        assertEquals(1, violations.size());
        assertEquals("O nome não deve estar vazio", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidUserWithoutPassword() {
        User user = new User();
        user.setName("Gilmar da Silva");
        user.setEmail("exemplo@exemplo");
        user.setPassword("");
        user.setDepartment(new Department());
        user.setAccessLevel(new AccessLevel());

        //Error Validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "O usuário deve ser inválido com senha em branco");
        assertEquals(1, violations.size());
        assertEquals("A senha não pode estar em branco.", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidUserWithInvalidEmail() {
        User user = new User();
        user.setName("Gilmar da Silva");
        user.setEmail("exemplo");
        user.setPassword("senha123");
        user.setDepartment(new Department());
        user.setAccessLevel(new AccessLevel());

        //Error Validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "O usuário deve ser inválido com email inválido");
        assertEquals(1, violations.size());
        assertEquals("Formato de e-mail incorreto", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidUserWithoutDepartment() {
        User user = new User();
        user.setName("Gilmar da Silva");
        user.setEmail("exemplo@exemplo");
        user.setPassword("senha123");
        user.setAccessLevel(new AccessLevel());

        //Error Validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "O usuário deve ser inválido sem departamento");
        assertEquals(1, violations.size());
        assertEquals("O departamento não pode estar vazio", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidUserWithoutAccessLevel() {
        User user = new User();
        user.setName("Gilmar da Silva");
        user.setEmail("exemplo@exemplo");
        user.setPassword("senha123");
        user.setDepartment(new Department());

        //Error Validation
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "O usuário deve ser inválido sem nível de acesso");
        assertEquals(1, violations.size());
        assertEquals("O nível de acesso não deve estar vazio", violations.iterator().next().getMessage());
    }

}