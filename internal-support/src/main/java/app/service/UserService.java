package app.service;

import app.entity.User;
import app.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String save (@Valid User user){
        userRepository.save(user);
        return "Usuario salvo com sucesso";
    }

    public User findById(@Valid long id){
        if(userRepository.existsById(id)){
            Optional<User> user = userRepository.findById(id);
            return user.get();
        }else{
            throw new RuntimeException("Usuario nao encontrado com id: "+id);
        }
    }

    public List<User> findAll(){
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new RuntimeException("Não há usuarios registrados!");
        }else{
            return users;
        }
    }

    public void deleteById(@Valid long id){
        // verifica se o usuario existe
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado com id: " + id));

        // agora pode deletar o usuario
        userRepository.deleteById(id);
    }

    public User updateById(@Valid long id, User updatedUser){

        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setEmail(updatedUser.getEmail());
                    user.setPassword(updatedUser.getPassword());
                    user.setDepartment(updatedUser.getDepartment());
                    user.setViewed(updatedUser.getViewed());
                    user.setAccessLevel(updatedUser.getAccessLevel());

                    return userRepository.save(user);
                })
                .orElseThrow(()-> new RuntimeException("Usuario não encontrado com id: " + id));
    }

}
