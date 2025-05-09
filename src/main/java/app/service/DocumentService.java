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

    // Salvar novo documento
    public Document save(MultipartFile pdfFile, String departmentName, String title, String description, String addedBy) throws IOException {
        Document document = new Document();
        document.setDepartmentName(departmentName);
        document.setTitle(title);
        document.setDescription(description);
        document.setFilePath(pdfFile.getBytes()); // Store raw bytes
        document.setAddedBy(addedBy);

        return documentRepository.save(document);
    }

    // Fazer download do PDF como Resource
    public Resource downloadFile(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado"));

        byte[] fileBytes = document.getFilePath();
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IOException("Arquivo não disponível no documento");
        }

        return new ByteArrayResource(fileBytes);
    }

    // Buscar por ID
    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com id: " + id));
    }

    // Listar todos os documentos
    public List<Document> findAll() {
        List<Document> documents = documentRepository.findAll();
        if (documents.isEmpty()) {
            throw new RuntimeException("Não há documentos registrados!");
        }
        return documents;
    }

    // Paginação
    public Page<Document> findAllPaginated(Pageable pageable) {
        Page<Document> documents = documentRepository.findAll(pageable);
        if (documents.isEmpty()) {
            throw new RuntimeException("Não há documentos registrados!");
        }
        return documents;
    }

    // Buscar por departamento
    public List<Document> findDocumentsByDepartment(String departmentName) {
        return documentRepository.findByDepartmentName(departmentName);
    }

    // Buscar por título (palavra-chave)
    public List<Document> findDocumentsByTitleContaining(String keyword) {
        return documentRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Deletar por ID
    public void deleteDocumentById(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new IllegalArgumentException("Documento com o ID fornecido não foi encontrado.");
        }
        documentRepository.deleteById(id);
    }

    // Atualizar documento existente
    public Document updateDocument(Long id, MultipartFile file, String title, String description, String departmentName) throws IOException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento com o ID fornecido não foi encontrado."));

        document.setTitle(title);
        document.setDescription(description);
        document.setDepartmentName(departmentName);

        if (file != null && !file.isEmpty()) {
            document.setFilePath(file.getBytes()); // Store raw bytes
        }

        return documentRepository.save(document);
    }
}
