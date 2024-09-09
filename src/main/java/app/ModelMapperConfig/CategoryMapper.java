package app.ModelMapperConfig;

import app.entity.Category;
import app.DTO.CategoryDTO;
import app.entity.Document;
import app.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    @Autowired
    private DocumentRepository documentRepository;

    public CategoryDTO toCategoryDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());

        // Mapping document IDs
        List<Long> documentIds = Optional.ofNullable(category.getDocuments())
                .orElse(Collections.emptyList()).stream()
                .map(Document::getId)
                .collect(Collectors.toList());
        categoryDTO.setDocumentIds(documentIds);

        return categoryDTO;
    }

    public Category toCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());

        // Mapping documents
        List<Document> documents = Optional.ofNullable(categoryDTO.getDocumentIds())
                .orElse(Collections.emptyList()).stream()
                .map(id -> documentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id)))
                .collect(Collectors.toList());
        category.setDocuments(documents);

        return category;
    }
}
