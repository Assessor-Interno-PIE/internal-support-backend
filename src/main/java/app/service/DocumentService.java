package app.service;

import app.entity.Department;
import app.entity.Document;
import app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public Document save(MultipartFile pdfFile, Long departmentId, String title, String description) throws IOException {
        String originalFilename = pdfFile.getOriginalFilename();
        String filename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = Paths.get(uploadPath, filename);

        pdfFile.transferTo(filePath);

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Departamento não encontrado"));

        Document document = new Document();
        document.setDepartment(department);
        document.setTitle(title);
        document.setDescription(description);
        document.setFilePath(filePath.toString());

        return documentRepository.save(document);
    }

    public Resource downloadFile(Long documentId) throws IOException {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado"));

        Path filePath = Paths.get(document.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("Arquivo não pode ser lido ou não existe");
        }
    }


    public Document findById(Long id) {
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

    public List<Document> findDocumentsByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Departamento não encontrado"));
        return documentRepository.findByDepartment(department);
    }

    public List<Document> findDocumentsByTitleContaining(String keyword) {
        return documentRepository.findByTitleContaining(keyword);
    }

    public void deleteDocumentById(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Documento com o ID fornecido não foi encontrado.");
        }
    }

    public Document updateDocument(Long id, MultipartFile file, String title, String description, Long departmentId) throws IOException {
        return documentRepository.findById(id)
                .map(document -> {
                    try {
                        document.setTitle(title);
                        document.setDescription(description);

                        if (departmentId != null) {
                            Department department = departmentRepository.findById(departmentId)
                                    .orElseThrow(() -> new IllegalArgumentException("Departamento não encontrado"));
                            document.setDepartment(department);
                        }

                        if (file != null && !file.isEmpty()) {
                            String originalFilename = file.getOriginalFilename();
                            String filename = UUID.randomUUID() + "_" + originalFilename;
                            Path filePath = Paths.get(uploadPath, filename);
                            file.transferTo(filePath);
                            document.setFilePath(filePath.toString());
                        }

                        return documentRepository.save(document);
                    } catch (IOException e) {
                        throw new RuntimeException("Erro ao atualizar o arquivo", e);
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Documento com o ID fornecido não foi encontrado."));
    }

}
