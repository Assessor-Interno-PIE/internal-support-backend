package app.ModelMapperConfig;

import app.DTO.CategoryDTO;
import app.DTO.DepartmentDTO;
import app.DTO.DocumentDTO;
import app.DTO.UserDTO;
import app.entity.Category;
import app.entity.Department;
import app.entity.Document;
import app.entity.User;
import app.repository.CategoryRepository;
import app.repository.DepartmentRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private  UserRepository userRepository;

    public DocumentDTO toDocumentDTO(Document document){

        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setId(document.getId());
        documentDTO.setTitle(document.getTitle());
        documentDTO.setContent(document.getContent());
        documentDTO.setCreatedAt(document.getCreatedAt());
        documentDTO.setUpdatedAt(document.getUpdatedAt());

        // Mapping the Department
        if (document.getDepartment() != null) {
            DepartmentDTO departmentDTO = new DepartmentDTO();
            departmentDTO.setId(document.getDepartment().getId());
            departmentDTO.setName(document.getDepartment().getName());
            documentDTO.setDepartmentDTO(departmentDTO);
        }

        // Mapping the Department
        if (document.getCategory() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(document.getCategory().getId());
            categoryDTO.setName(document.getCategory().getName());
            documentDTO.setCategoryDTO(categoryDTO);
        }

        // Mapping the User
        if (document.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(document.getUser().getId());
            userDTO.setName(document.getUser().getName());
            userDTO.setEmail(document.getUser().getEmail());
            userDTO.setPassword(document.getUser().getPassword());
            documentDTO.setUserDTO(userDTO);
        }

        return documentDTO;
    }

    public Document toDocument(DocumentDTO documentDTO){

        Document document = new Document();
        document.setId(documentDTO.getId());
        document.setTitle(documentDTO.getTitle());
        document.setContent(documentDTO.getContent());
        document.setCreatedAt(documentDTO.getCreatedAt());
        document.setUpdatedAt(documentDTO.getUpdatedAt());

        if (documentDTO.getDepartmentDTO() != null) {
            Department department = departmentRepository.findById(documentDTO.getDepartmentDTO().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));
            document.setDepartment(department);
        }

        if (documentDTO.getCategoryDTO() != null) {
            Category category = categoryRepository.findById(documentDTO.getCategoryDTO().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            document.setCategory(category);
        }

        if (documentDTO.getUserDTO() != null) {
            User user = userRepository.findById(documentDTO.getUserDTO().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            document.setUser(user);
        }

        return document;
    }
}
