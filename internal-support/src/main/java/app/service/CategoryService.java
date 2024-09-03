package app.service;

import app.entity.Category;
import app.entity.Department;
import app.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public String save (@Valid Category category){
        categoryRepository.save(category);
        return "Categoria salva com sucesso";
    }

    public Category findById(@Valid long id){
        if(categoryRepository.existsById(id)){
            Optional<Category> category = categoryRepository.findById(id);
            return category.get();
        }else{
            throw new RuntimeException("Categoria nao encontrada com id: "+id);
        }
    }

    public List<Category> findAll(){
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new RuntimeException("Não há categorias registradas!");
        }else{
            return categories;
        }
    }

    public void deleteById(@Valid long id){
        // verifica se o categoria existe
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com id: " + id));

        // agora pode deletar a categoria
        categoryRepository.deleteById(id);
    }

    public Category updateById(@Valid long id, Category updatedCategory){

        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(updatedCategory.getName());
                    category.setDocuments(updatedCategory.getDocuments());

                    return categoryRepository.save(category);
                })
                .orElseThrow(()-> new RuntimeException("Categoria não encontrada com id: " + id));
    }


}
