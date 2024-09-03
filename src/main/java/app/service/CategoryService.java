package app.service;

import app.entity.Category;
import app.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public String save(@Valid Category category) {
        categoryRepository.save(category);
        return "Categoria salva com sucesso";
    }

    public Category findById(@Valid Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));
    }

    public List<Category> findAll() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new RuntimeException("Não há categorias registradas!");
        } else {
            return categories;
        }
    }

    public String deleteById(@Valid Long id) {
        // Verifica se a categoria existe
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));
        // Agora pode deletar a categoria
        categoryRepository.deleteById(id);
        return "Categoria deletada com sucesso.";
    }

    public Category updateById(@Valid Long id, Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(updatedCategory.getName());
                    category.setDocuments(updatedCategory.getDocuments());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));
    }
}
