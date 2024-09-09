package app.ModelMapperConfig;

import app.DTO.AccessLevelDTO;
import app.DTO.DepartmentDTO;
import app.DTO.UserDTO;
import app.entity.AccessLevel;
import app.entity.Department;
import app.entity.Document;
import app.entity.User;

import app.repository.AccessLevelRepository;
import app.repository.DepartmentRepository;
import app.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AccessLevelRepository accessLevelRepository;

    @Autowired
    private DocumentRepository documentRepository;

    public UserDTO toUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());

        // Mapping the department
        if (user.getDepartment() != null) {
            DepartmentDTO departmentDTO = new DepartmentDTO();
            departmentDTO.setId(user.getDepartment().getId());
            departmentDTO.setName(user.getDepartment().getName());
            userDTO.setDepartmentDTO(departmentDTO);
        }

        // Mapping the AccessLevel
        if (user.getAccessLevel() != null) {
            AccessLevelDTO accessLevelDTO = new AccessLevelDTO();
            accessLevelDTO.setId(user.getAccessLevel().getId());
            accessLevelDTO.setName(user.getAccessLevel().getName());
            userDTO.setAccessLevelDTO(accessLevelDTO);
        }

        // Mapping document IDs
        List<Long> viewedDocumentIds = Optional.ofNullable(user.getViewed())
                .orElse(Collections.emptyList()).stream()
                .map(Document::getId)
                .collect(Collectors.toList());
        userDTO.setViewedDocumentIds(viewedDocumentIds);

        return userDTO;
    }

    public User toUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());


        // Mapping the department
        if (userDTO.getDepartmentDTO() != null) {
            Department department = departmentRepository.findById(userDTO.getDepartmentDTO().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Departament not found"));
            user.setDepartment(department);
        }

        // Mapping the AccessLevel
        if (userDTO.getAccessLevelDTO() != null) {
            AccessLevel accessLevel = accessLevelRepository.findById(userDTO.getAccessLevelDTO().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Access Level not found"));
            user.setAccessLevel(accessLevel);
        }

        // Mapping documents
        List<Document> documents = Optional.ofNullable(userDTO.getViewedDocumentIds())
                .orElse(Collections.emptyList()).stream()
                .map(id -> documentRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id)))
                .collect(Collectors.toList());
        user.setViewed(documents);

        return user;
    }
}
