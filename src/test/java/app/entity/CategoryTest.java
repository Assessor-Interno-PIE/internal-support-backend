package app.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private final Validator validator;

    public CategoryTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidCategory() {
        Category category = new Category();
        category.setName("Categoria Exemplo");
        category.setDocuments(List.of());

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertTrue(violations.isEmpty(), "A categoria deve ser válida");
    }

    @Test
    public void testInvalidCategoryWithEmptyName() {
        Category category = new Category();
        category.setName(""); // Empty name
        category.setDocuments(List.of());

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertFalse(violations.isEmpty(), "A categoria deve ser inválida com nome vazio");
        assertEquals(2, violations.size()); // 2 violations because of @NotBlank and @Pattern
    }

    @Test
    public void testInvalidCategoryWithInvalidName() {
        Category category = new Category();
        category.setName("Categoria123"); // Name with numbers
        category.setDocuments(List.of());

        Set<ConstraintViolation<Category>> violations = validator.validate(category);
        assertFalse(violations.isEmpty(), "A categoria deve ser inválida com nome contendo números");
        assertEquals(1, violations.size());
        assertEquals("O nome deve conter apenas letras", violations.iterator().next().getMessage());
    }
}
