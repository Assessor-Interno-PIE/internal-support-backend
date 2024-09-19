package app.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    private final Validator validator;

    public DocumentTest(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidDocument() {
        Document document = new Document();
        document.setTitle("Título Exemplo");
        document.setContent("Conteúdo Exemplo");
        document.setDepartment(new Department());
        document.setCategory(new Category());
        document.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUser(new User());

        Set<ConstraintViolation<Document>> violations = validator.validate(document);
        assertTrue(violations.isEmpty(), "O document deve ser válido");

    }

    @Test
    public void testInvalidDocumentWithoutTitle() {
        Document document = new Document();
        document.setContent("Conteúdo Exemplo");
        document.setDepartment(new Department());
        document.setCategory(new Category());
        document.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUser(new User());

        Set<ConstraintViolation<Document>> violations = validator.validate(document);
        assertFalse(violations.isEmpty(), "O document deve ser inválido sem título");
        assertEquals(1, violations.size());
        assertEquals("O título não pode estar vazio", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidDocumentWithoutContent() {
        Document document = new Document();
        document.setTitle("Título Exemplo");
        document.setDepartment(new Department());
        document.setCategory(new Category());
        document.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUser(new User());

        Set<ConstraintViolation<Document>> violations = validator.validate(document);
        assertFalse(violations.isEmpty(), "O document deve ser inválido sem conteúdo");
        assertEquals(1, violations.size());
        assertEquals("O conteúdo não pode estar vazio", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidDocumentWithoutDepartment() {
        Document document = new Document();
        document.setTitle("Título Exemplo");
        document.setContent("Conteúdo Exemplo");
        document.setCategory(new Category());
        document.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUser(new User());

        Set<ConstraintViolation<Document>> violations = validator.validate(document);
        assertFalse(violations.isEmpty(), "O document deve ser inválido sem departamento");
        assertEquals(1, violations.size());
        assertEquals("O departamento não pode estar vazio", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidDocumentWithoutCategory() {
        Document document = new Document();
        document.setTitle("Título Exemplo");
        document.setContent("Conteúdo Exemplo");
        document.setDepartment(new Department());
        document.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUser(new User());

        Set<ConstraintViolation<Document>> violations = validator.validate(document);
        assertFalse(violations.isEmpty(), "O document deve ser inválido sem categoria");
        assertEquals(1, violations.size());
        assertEquals("A categoria não deve estar vazia", violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidDocumentWithoutUser() {
        Document document = new Document();
        document.setTitle("Título Exemplo");
        document.setContent("Conteúdo Exemplo");
        document.setDepartment(new Department());
        document.setCategory(new Category());
        document.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        document.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        Set<ConstraintViolation<Document>> violations = validator.validate(document);
        assertFalse(violations.isEmpty(), "O document deve ser inválido sem usuário");
        assertEquals(1, violations.size());
        assertEquals("O usuário não deve estar vazio", violations.iterator().next().getMessage());
    }

}