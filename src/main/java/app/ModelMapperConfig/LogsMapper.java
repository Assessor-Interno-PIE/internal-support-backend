package app.ModelMapperConfig;

import app.DTO.*;
import app.entity.*;
import app.repository.DocumentRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogsMapper {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentRepository documentRepository;

    public LogsDTO toLogsDTO(Logs logs) {

        LogsDTO logsDTO = new LogsDTO();
        logsDTO.setId(logs.getId());
        logsDTO.setEndpoint(logs.getEndpoint());
        logsDTO.setMethod(logs.getMethod());
        logsDTO.setStatus(logs.getStatus());

        // Mapping the user
        if (logs.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(logs.getUser().getId());
            userDTO.setName(logs.getUser().getName());
            userDTO.setEmail(logs.getUser().getEmail());
            userDTO.setPassword(logs.getUser().getPassword());

            logsDTO.setUserDTO(userDTO);
        }

        // Mapping the document
        if (logs.getDocument() != null) {
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setId(logs.getDocument().getId());
            documentDTO.setTitle(logs.getDocument().getTitle());
            documentDTO.setContent(logs.getDocument().getContent());
            documentDTO.setUpdatedAt(logs.getDocument().getUpdatedAt());
            documentDTO.setCreatedAt(logs.getDocument().getCreatedAt());

            logsDTO.setDocumentDTO(documentDTO);
        }

        return logsDTO;
    }

    public Logs toLogs(LogsDTO logsDTO) {
        Logs logs = new Logs();
        logs.setId(logsDTO.getId());
        logs.setEndpoint(logsDTO.getEndpoint());
        logs.setMethod(logsDTO.getMethod());
        logs.setStatus(logsDTO.getStatus());

        // Mapping the User
        if (logsDTO.getUserDTO() != null) {
            User user = userRepository.findById(logsDTO.getUserDTO().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            logs.setUser(user);
        }

        // Mapping the Document
        if (logsDTO.getDocumentDTO() != null) {
            Document document = documentRepository.findById(logsDTO.getDocumentDTO().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Document not found"));
            logs.setDocument(document);
        }
        return logs;
    }
}
