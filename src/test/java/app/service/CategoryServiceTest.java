package app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void testSaveCategory() {
        Category category = new Category();
        String result = categoryService.save(category);

        verify(categoryRepository, times(1)).save(category);
        assertEquals("Categoria salva com sucesso", result);
    }

    @Test
    void testFindByIdFound() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);

        when(categoryRepository.findById(id)).thenReturn(java.util.Optional.of(category));

        Category result = categoryService.findById(id);

        verify(categoryRepository, times(1)).findById(id);
        assertEquals(category, result);
    }

    @Test
    void testFindByIdNotFound() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.findById(id));
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void testFindAllWithCategories() {
        Category category1 = new Category();
        Category category2 = new Category();
        List<Category> categoriesList = List.of(category1, category2);

        when(categoryRepository.findAll()).thenReturn(categoriesList);

        List<Category> result = categoryService.findAll();

        assertEquals(categoriesList, result);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testFindAllEmpty() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.findAll();
        });

        assertEquals("Não há categorias registradas!", exception.getMessage());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testDeleteByIdSuccess() {
        long id = 1L;
        Category category = new Category();
        category.setId(id);

        when(categoryRepository.findById(id)).thenReturn(java.util.Optional.of(category));

        String result = categoryService.deleteById(id);

        verify(categoryRepository, times(1)).deleteById(id);
        assertEquals("Categoria deletada com sucesso", result);
    }

    @Test
    void testDeleteByIdNotFound() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> categoryService.deleteById(id));
        assertEquals("Categoria não encontrada com id: " + id, exception.getMessage());
        verify(categoryRepository, never()).deleteById(id);
    }

    @Test
    void testUpdateByIdSuccess() {
        Long id = 1L;
        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("Old Name");

        Category updatedCategory = new Category();
        updatedCategory.setName("New Name");

        when(categoryRepository.findById(id)).thenReturn(java.util.Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        Category result = categoryService.updateById(id, updatedCategory);

        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(existingCategory);
        assertEquals("New Name", result.getName());
    }

    @Test
    void testUpdateByIdNotFound() {
        Long id = 1L;
        Category updatedCategory = new Category();
        updatedCategory.setName("New Name");

        when(categoryRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.updateById(id, updatedCategory));
        verify(categoryRepository, times(1)).findById(id);
    }
}
