package app.controller;

import app.DTO.CategoryDTO;
import app.DTO.UserDTO;
import app.ModelMapperConfig.CategoryMapper;
import app.entity.Category;
import app.entity.User;
import app.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody CategoryDTO categoryDTO) {
        // Mapping entity User to UserDTO
        Category category = categoryMapper.toCategory(categoryDTO);

        String message = categoryService.save(category);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        Category category = categoryService.findById(id);

        // Mapping entity User to UserDTO
        CategoryDTO categoryDTO = categoryMapper.toCategoryDTO(category);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List<Category> categories = categoryService.findAll();

        List<CategoryDTO> categoriesDTOS = categories.stream()
                .map(categoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(categoriesDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = categoryService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<CategoryDTO> updateById(@Valid @PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.toCategory(categoryDTO);
        Category updatedCategory = categoryService.updateById(id, category);
        CategoryDTO updatedCategoryDTO = categoryMapper.toCategoryDTO(updatedCategory);
        return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
    }
}
