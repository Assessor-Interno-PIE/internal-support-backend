package app.service;

import app.entity.AccessLevel;
import app.entity.Department;
import app.entity.User;
import app.repository.AccessLevelRepository;
import app.repository.DepartmentRepository;
import app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AccessLevelRepository accessLevelRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testSaveUser(){
        User user = new User();
        Department department = new Department();

        department.setId(2L);
        user.setDepartment(department);

        AccessLevel accesslevel = new AccessLevel();
        accesslevel.setId(2L);
        user.setAccessLevel(accesslevel);

        //mocka o departamento e nivel de acesso
        when(departmentRepository.existsById(2L)).thenReturn(true);
        when(accessLevelRepository.existsById(2L)).thenReturn(true);

        when(userRepository.save(any(User.class))).thenReturn(user);

        //testa se salvou com sucesso
        String result = userService.save(user);
        assertEquals("Usuário salvo com sucesso.", result);

        //verifica se teve a chamada correta dos métodos
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void testUserWithNoDepartamentSave() {
        User user = new User();
        Department department = new Department();
        department.setId(2L);
        user.setDepartment(department);

        user.setAccessLevel(new AccessLevel());

        //mocka que o departamento nao existe
        when(departmentRepository.existsById(anyLong())).thenReturn(false);

        // testa se a exceção foi lançada ao salvar sem departamento
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.save(user);
        });

        assertEquals("Departamento não encontrado.", exception.getMessage());

        // verifica que o método save não foi chamado
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUserWithNoAccessLevelSave() {
        User user = new User();
        Department department = new Department();
        department.setId(1L);
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setId(1L);

        user.setDepartment(department);

        user.setAccessLevel(accessLevel);

        //mocka que o departamento existe
        when(departmentRepository.existsById(anyLong())).thenReturn(true);

        //mocka que o accesslevel nao existe
        when(accessLevelRepository.existsById(anyLong())).thenReturn(false);

        // testa se a exceção foi lançada ao salvar sem nivel de acesso
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.save(user);
        });

        assertEquals("Nível de acesso não encontrado.", exception.getMessage());

        // verifica que o método save não foi chamado
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testfindUserWithValidID(){
        //cria um user só para ter id
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);//da um id "valido"
        assertEquals(user, result);

        //verifica se foi chamado o findbyid do repositorio
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(1L);
        });
        assertEquals("Usuário não encontrado com id: 1", exception.getMessage());

        //verifica se foi chamado o findbyid do repositorio
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllUsersExist(){
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();
        assertEquals(users.size(), result.size());

        //verifica se foi chamado o findall do repositorio
        verify(userRepository, times(1)).findAll();
    }

    void testFindAllUsersNotFound(){
        when(userRepository.findAll()).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->{
            userService.findAll();
        });
        assertEquals("Não há usuários registrados!", exception.getMessage());

        //verifica se foi chamado o findall do repositorio
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testDeleteByIdUserExists(){
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = userService.deleteById(1L);
        assertEquals("Usuário deletado com sucesso.", result);

        //verifica se foi chamado o findbyid do repositorio
        verify(userRepository, times(1)).findById(1L);
        //verifica se foi chamado o deletebyid do repositorio
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByIdUserNotFound(){

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->{
            userService.deleteById(1L);
        });
        assertEquals("Usuário não encontrado com id: 1", exception.getMessage());

        //verifica se foi chamado o findbyid do repositorio
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateByIdUserExists(){
        User existingUser = new User();
        existingUser.setName("velho nome");
        existingUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        User updatedUser = new User();
        updatedUser.setName("novo nome");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateById(1L, updatedUser);
        assertEquals(existingUser, result);

        //verifica se foi chamado o findbyid do repositorio
        verify(userRepository, times(1)).findById(1L);
        //verifica se foi chamado o save do repositorio conforme o usuario q deveria ser atualizado
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateByIdUserNotFound(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, ()->{
            userService.updateById(1L, new User());
        });

        assertEquals("Usuário não encontrado com id: 1", exception.getMessage());
        //verifica se foi chamado o findbyid do repositorio
        verify(userRepository, times(1)).findById(1L);
    }





}
