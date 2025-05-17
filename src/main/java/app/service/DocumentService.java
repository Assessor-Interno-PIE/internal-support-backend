package app.service;

import app.entity.Document;
import app.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    /**
     * Armazena um novo documento no sistema
     */
    public Document save(MultipartFile pdfFile, String departmentName, String title, String description, String addedBy) throws IOException {
        Document document = new Document();
        document.setTitle(title);
        document.setDescription(description);
        document.setDepartmentName(departmentName);
        document.setAddedBy(addedBy);
        document.setFilePath(pdfFile.getBytes());

        return documentRepository.save(document);
    }

    /**
     * Recupera o arquivo PDF de um documento específico
     */
    public Resource downloadFile(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado"));

        byte[] bytes = document.getFilePath();
        if (bytes == null || bytes.length == 0) {
            throw new IOException("Arquivo não disponível no documento");
        }

        return new ByteArrayResource(bytes);
    }

    /**
     * Retorna um documento específico pelo ID
     */
    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com ID: " + id));
    }

    /**
     * Retorna todos os documentos cadastrados
     */
    public List<Document> findAll() {
        List<Document> documents = documentRepository.findAll();
        if (documents.isEmpty()) {
            throw new RuntimeException("Nenhum documento foi encontrado.");
        }
        return documents;
    }

    /**
     * Retorna todos os documentos com paginação
     */
    public Page<Document> findAllPaginated(Pageable pageable) {
        Page<Document> documents = documentRepository.findAll(pageable);
        if (documents.isEmpty()) {
            throw new RuntimeException("Nenhum documento foi encontrado.");
        }
        return documents;
    }

    /**
     * Busca documentos por nome do departamento
     */
    public List<Document> findDocumentsByDepartment(String departmentName) {
        return documentRepository.findByDepartment(departmentName);
    }

    /**
     * Busca documentos por palavras-chave no título
     */
    public List<Document> findDocumentsByTitleContaining(String keyword) {
        return documentRepository.findByTitleContainingIgnoreCase(keyword);
    }

    /**
     * Remove um documento pelo ID
     */
    public void deleteDocumentById(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new IllegalArgumentException("Documento com o ID informado não existe.");
        }
        documentRepository.deleteById(id);
    }

    /**
     * Atualiza os dados de um documento existente
     */
    public Document updateDocument(Long id, MultipartFile file, String title, String description, String departmentName) throws IOException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado com ID: " + id));

        document.setTitle(title);
        document.setDescription(description);
        document.setDepartmentName(departmentName);

        if (file != null && !file.isEmpty()) {
            document.setFilePath(file.getBytes());
        }

        return documentRepository.save(document);
    }
}
