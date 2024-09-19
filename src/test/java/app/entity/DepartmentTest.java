package app.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {

    private final Validator validator;

    public DepartmentTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidDepartment() {
        Department department = new Department();
        department.setName("Recursos Humanos");
        department.setDocuments(List.of()); // Inicializa como lista vazia
        department.setUsers(List.of()); // Inicializa como lista vazia

        Set<ConstraintViolation<Department>> violations = validator.validate(department);
        assertTrue(violations.isEmpty(), "O departamento deve ser válido");
    }

    @Test
    public void testInvalidDepartmentWithoutName() {
        Department department = new Department();
        department.setDocuments(List.of());
        department.setUsers(List.of());

        Set<ConstraintViolation<Department>> violations = validator.validate(department);
        assertFalse(violations.isEmpty(), "O departamento deve ser inválido sem nome");
        assertEquals(1, violations.size());
        assertEquals("O nome não pode estar vazio", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidDepartmentWithNumericName() {
        Department department = new Department();
        department.setName("Recursos 123"); // Nome inválido com números
        department.setDocuments(List.of());
        department.setUsers(List.of());

        Set<ConstraintViolation<Department>> violations = validator.validate(department);
        assertFalse(violations.isEmpty(), "O departamento deve ser inválido com nome numérico");
        assertEquals(1, violations.size());
        assertEquals("O nome deve conter apenas letras", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidDepartmentWithEmptyName() {
        Department department = new Department();
        department.setName(""); // Empty Name | 2 violations because of @NotBlank and @Pattern
        department.setDocuments(List.of());
        department.setUsers(List.of());

        Set<ConstraintViolation<Department>> violations = validator.validate(department);
        assertFalse(violations.isEmpty(), "O departamento deve ser inválido com nome vazio");
        assertEquals(2, violations.size());
    }

    @Test
    public void testInvalidDepartmentWithSpecialCharacters() {
        Department department = new Department();
        department.setName("Recursos & Humanos"); // Special Character
        department.setDocuments(List.of());
        department.setUsers(List.of());

        Set<ConstraintViolation<Department>> violations = validator.validate(department);
        assertFalse(violations.isEmpty(), "O departamento deve ser inválido com caracteres especiais no nome");
        assertEquals(1, violations.size());
        assertEquals("O nome deve conter apenas letras", violations.iterator().next().getMessage());
    }

}