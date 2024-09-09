package app.ModelMapperConfig;

import app.DTO.DepartmentDTO;
import app.entity.Department;
import app.entity.Document;
import app.entity.User;
import app.repository.DocumentRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DepartmentMapper {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    public DepartmentDTO toDepartmentDTO(Department department) {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setId(department.getId());
        departmentDTO.setName(department.getName());

        // Mapping document IDs
        List<Long> documentIds = Optional.ofNullable(department.getDocuments())
                .orElse(Collections.emptyList()).stream()
                .map(Document::getId)
                .collect(Collectors.toList());
        departmentDTO.setDocumentIds(documentIds);

        // Mapping user IDs
        List<Long> userIds = Optional.ofNullable(department.getUsers())
                .orElse(Collections.emptyList()).stream()
                .map(User::getId)
                .collect(Collectors.toList());
        departmentDTO.setUserIds(userIds);

        return departmentDTO;
    }

    public Department toDepartment(DepartmentDTO departmentDTO) {
        Department department = new Department();
        department.setId(departmentDTO.getId());
        department.setName(departmentDTO.getName());

        // Mapping documents
        List<Document> documents = Optional.ofNullable(departmentDTO.getDocumentIds())
                .orElse(Collections.emptyList()).stream()
                .map(id -> documentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id)))
                .collect(Collectors.toList());
        department.setDocuments(documents);

        // Mapping users
        List<User> users = Optional.ofNullable(departmentDTO.getUserIds())
                .orElse(Collections.emptyList()).stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id)))
                .collect(Collectors.toList());
        department.setUsers(users);

        return department;
    }
}
