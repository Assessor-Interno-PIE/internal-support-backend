package app.config;

import app.entity.Document;
import app.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existem documentos no banco
        if (documentRepository.count() == 0) {
            // Cria alguns documentos de exemplo
            Document doc1 = new Document();
            doc1.setTitle("Manual do Usuário");
            doc1.setDescription("Manual básico de utilização do sistema");
            doc1.setDepartmentName("TI");
            doc1.setAddedBy("admin");
            doc1.setFilePath(new byte[0]); // Arquivo vazio inicial

            Document doc2 = new Document();
            doc2.setTitle("Política de Segurança");
            doc2.setDescription("Políticas de segurança da empresa");
            doc2.setDepartmentName("RH");
            doc2.setAddedBy("admin");
            doc2.setFilePath(new byte[0]); // Arquivo vazio inicial

            // Salva os documentos
            documentRepository.save(doc1);
            documentRepository.save(doc2);
        }
    }
} 