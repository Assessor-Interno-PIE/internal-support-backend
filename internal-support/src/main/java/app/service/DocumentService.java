package app.service;


import app.entity.Document;
import app.repository.DocumentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public String save (@Valid Document document){
        documentRepository.save(document);
        return "Documento salvo com sucesso";
    }

    public Document findById(@Valid long id){
        if(documentRepository.existsById(id)){
            Optional<Document> document = documentRepository.findById(id);
            return document.get();
        }else{
            throw new RuntimeException("Documento nao encontrado com id: "+id);
        }
    }

    public List<Document> findAll(){
        List<Document> documents = documentRepository.findAll();
        if(documents.isEmpty()){
            throw new RuntimeException("Não há documentos registrados!");
        }else{
            return documents;
        }
    }

    public void deleteById(@Valid long id){
        // verifica se o documento existe
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado com id: " + id));

        // agora pode deletar o documento
        documentRepository.deleteById(id);
    }

    public Document updateById(@Valid long id, Document updatedDocument){

        return documentRepository.findById(id)
                .map(document -> {
                    document.setTitle(updatedDocument.getTitle());
                    document.setContent(updatedDocument.getContent());
                    document.setDepartment(updatedDocument.getDepartment());
                    document.setCategory(updatedDocument.getCategory());
                    document.setUser(updatedDocument.getUser());

                    return documentRepository.save(document);
                })
                .orElseThrow(()-> new RuntimeException("Documento não encontrado com id: " + id));
    }

}
