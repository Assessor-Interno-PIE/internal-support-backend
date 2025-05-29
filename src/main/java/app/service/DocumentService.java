package app.service;

import app.entity.Document;
import app.repository.jpa.DocumentRepository;
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

    @Autowired
    private LogService logService;

    // Salvar novo documento
    public Document save(MultipartFile file, String groupId, String title, String description, String addedBy) throws IOException {
        Document document = new Document();
        document.setGroupId(groupId);
        document.setTitle(title);
        document.setDescription(description);
        document.setFilePath(file.getBytes());
        document.setAddedBy(addedBy);

        logService.registrar("Salvar", addedBy, "/api/documents/save", "Novo documento salvo");

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

        String endpoint = "/view/" + documentId;

        logService.registrar("Download", "", endpoint, "Documento baixado");
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

        logService.registrar("Busca", "", "/api/documents", "Busca de todos os documentos");
        return documents;
    }

    // Paginação
    public Page<Document> findAllPaginated(Pageable pageable) {
        Page<Document> documents = documentRepository.findAll(pageable);
        if (documents.isEmpty()) {
            throw new RuntimeException("Não há documentos registrados!");
        }

        logService.registrar("Busca", "", "/api/documents/paginated", "Busca de documentos paginados");
        return documents;
    }

    // Buscar por grupo
    public List<Document> findByGroupId(String groupId) {
        String endpoint = "/by-group/" + groupId;

        logService.registrar("Busca", "", endpoint, "Busca por grupo");
        return documentRepository.findByGroupId(groupId);

    }

    // Deletar por ID
    public void deleteById(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new IllegalArgumentException("Documento com o ID fornecido não foi encontrado.");
        }

        String endpoint = "/api/documents/" + id;
        logService.registrar("Delete", "", endpoint, "Documento deletado");
        documentRepository.deleteById(id);
    }

    // Atualizar documento existente
    public Document updateById(Long id, MultipartFile file, String title, String description, String groupId) throws IOException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento com o ID fornecido não foi encontrado."));

        document.setTitle(title);
        document.setDescription(description);
        document.setGroupId(groupId);

        if (file != null && !file.isEmpty()) {
            document.setFilePath(file.getBytes());
        }

        String endpoint = "/api/documents/" + id;
        logService.registrar("Atualização", "", endpoint, "Atualização de Documento");
        return documentRepository.save(document);
    }
}
