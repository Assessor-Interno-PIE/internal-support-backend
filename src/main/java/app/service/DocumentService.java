package app.service;

import app.entity.Document;
import app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private static final String DOCUMENTS_FOLDER = "documents";  // Diretório onde os PDFs serão armazenados

    public String save(Document document, MultipartFile pdfFile) throws IOException {
        // Verifica se o departamento existe
        if (!departmentRepository.existsById(document.getDepartment().getId())) {
            throw new IllegalArgumentException("Departamento não encontrado.");
        }

        // Cria o diretório "documents" se não existir
        File folder = new File(DOCUMENTS_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Cria um nome único para o arquivo PDF
        String fileName = System.currentTimeMillis() + "_" + pdfFile.getOriginalFilename();
        Path filePath = Paths.get(DOCUMENTS_FOLDER, fileName);

        // Salva o arquivo PDF no sistema de arquivos
        Files.write(filePath, pdfFile.getBytes());

        // Define o caminho do arquivo PDF no documento
        document.setPdfContent(filePath.toString());  // Guarda o caminho do arquivo no banco de dados

        // Salva o documento no banco de dados
        documentRepository.save(document);
        return "Documento salvo com sucesso em: " + filePath;
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

    public String deleteById(Long id) {
        // Verifica se o documento existe
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com id: " + id));

        // Aqui, você pode deletar o arquivo do sistema de arquivos se necessário
        String filePath = document.getPdfContent();
        if (filePath != null) {
            new File(filePath).delete();
        }

        // Deleta o documento do banco de dados
        documentRepository.deleteById(id);
        return "Documento deletado com sucesso.";
    }

    public Document updateById(Long id, Document updatedDocument, MultipartFile pdfFile) throws IOException {
        return documentRepository.findById(id)
                .map(document -> {
                    document.setTitle(updatedDocument.getTitle());
                    document.setDepartment(updatedDocument.getDepartment());

                    // Se um novo arquivo PDF for enviado, substitui o anterior
                    if (pdfFile != null && !pdfFile.isEmpty()) {
                        // Cria o diretório "documents" se não existir
                        File folder = new File(DOCUMENTS_FOLDER);
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }

                        // Cria um nome único para o novo arquivo PDF
                        String fileName = System.currentTimeMillis() + "_" + pdfFile.getOriginalFilename();
                        Path filePath = Paths.get(DOCUMENTS_FOLDER, fileName);

                        // Salva o novo arquivo PDF
                        try {
                            Files.write(filePath, pdfFile.getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        document.setPdfContent(filePath.toString());  // Atualiza o caminho do arquivo
                    }

                    return documentRepository.save(document);
                })
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com id: " + id));
    }
}
