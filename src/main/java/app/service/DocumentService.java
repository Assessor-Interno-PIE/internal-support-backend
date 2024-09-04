package app.service;

import app.entity.Document;
import app.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public String save(@Valid Document document) {

        // Verifica se o departamento existe
        if (!departmentRepository.existsById(document.getDepartment().getId())) {
            throw new IllegalArgumentException("Departamento não encontrado.");
        }

        // Verifica se a categoria existe
        if (!categoryRepository.existsById(document. getCategory().getId())){
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        // Verifica se o usuario existe
        if (!userRepository.existsById(document.getUser().getId())){
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        // Define timestamps de criação e atualização antes de salvar
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        document.setCreatedAt(currentTimestamp);
        document.setUpdatedAt(currentTimestamp);
        documentRepository.save(document);
        return "Documento salvo com sucesso.";
    }

    public Document findById(@Valid Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com id: " + id));
    }

    public List<Document> findAll() {
        List<Document> documents = documentRepository.findAll();
        if (documents.isEmpty()) {
            throw new RuntimeException("Não há documentos registrados!");
        } else {
            return documents;
        }
    }

    public String deleteById(@Valid Long id) {
        // Verifica se o documento existe
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com id: " + id));
        // Agora pode deletar o documento
        documentRepository.deleteById(id);
        return "Documento deletado com sucesso.";
    }

    public Document updateById(@Valid Long id, Document updatedDocument) {
        return documentRepository.findById(id)
                .map(document -> {
                    document.setTitle(updatedDocument.getTitle());
                    document.setContent(updatedDocument.getContent());
                    document.setDepartment(updatedDocument.getDepartment());
                    document.setCategory(updatedDocument.getCategory());
                    document.setUser(updatedDocument.getUser());
                    document.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // Atualiza o timestamp de modificação
                    return documentRepository.save(document);
                })
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com id: " + id));
    }
}
