package app.controller;

import app.entity.Category;
import app.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.InternalException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void saveCategoryWithoutName() throws Exception {
        Category invalidCategory = new Category();

        mockMvc.perform(post("/api/categories/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro: O nome não pode estar em branco")); // Verifica a mensagem de erro
    }

    @Test
    void saveCategorySuccess() throws Exception {
        Category validCategory = new Category();
        validCategory.setName("Categoria Exmeplar");

        when(categoryService.save(any(Category.class))).thenReturn("Categoria criada com sucesso");

        mockMvc.perform(post("/api/categories/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCategory)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Categoria criada com sucesso"));
    }

    @Test
    void findCategoryByIdSuccess() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Books");

        when(categoryService.findById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/categories/find-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Books"));
    }

    @Test
    void findCategoryByIdNotFound() throws Exception {
        when(categoryService.findById(anyLong())).thenThrow(new EntityNotFoundException("Categoria não encontrada com id: 1"));

        mockMvc.perform(get("/api/categories/find-by-id/1"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Categoria não encontrada com id: 1"));
    }

    @Test
    void findAllSuccess() throws Exception {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category());

        when(categoryService.findAll()).thenReturn(categories);
        mockMvc.perform(get("/api/categories/find-all"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    @Test
    void findAllWithoutRegistredCategories() throws Exception {
        when(categoryService.findAll()).thenThrow(new InternalException("Não há níveis de acesso registrados!"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/find-all"))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Não há níveis de acesso registrados!"));
    }

    @Test
    void updateCategoriesSuccess() throws Exception {
        Category uptcategory = new Category();
        uptcategory.setId(1L);
        uptcategory.setName("Categoria Exemplar Atualizada");

        when(categoryService.updateById(anyLong(), any(Category.class))).thenReturn(uptcategory);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/categories/update-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uptcategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Categoria Exemplar Atualizada"));
    }

    @Test
    void updateCategoryNotFound() throws Exception {
        Category updatedCategory = new Category();
        updatedCategory.setName("Categoria Exemplar Atualizada");

        when(categoryService.updateById(anyLong(), any(Category.class))).thenThrow(new EntityNotFoundException("Categoria não encontrada com id: 1"));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/categories/update-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Categoria não encontrada com id: 1"));
    }

    @Test
    void deleteCategoryByIdSuccess() throws Exception {
        when(categoryService.deleteById(1L)).thenReturn("Categoria deletada com sucesso");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categories/delete-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Categoria deletada com sucesso"));
    }
    @Test
    void deleteCategoryByIdFailure() throws Exception {
        when(categoryService.deleteById(1L)).thenThrow(new EntityNotFoundException("Categoria não encontrada com id: 1"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categories/delete-by-id/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Categoria não encontrada com id: 1"));
    }
}