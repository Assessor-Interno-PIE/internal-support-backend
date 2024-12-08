package app.service;

import app.entity.Department;
import app.entity.User;
import app.repository.DepartmentRepository;
import app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Finance");

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setPassword("password");
        user.setDepartment(department);
        user.setIsAdmin(1);
    }

    @Test
    void testSaveUser_Success() {
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = userService.save(user);

        assertEquals("Usuário salvo com sucesso.", result);
        verify(userRepository).save(user);
    }

    @Test
    void testSaveUser_DepartmentNotFound() {
        when(departmentRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.save(user));

        assertEquals("Departamento não encontrado.", exception.getMessage());
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User foundUser = userService.findById(1L);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("John Doe", foundUser.getName());
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.findById(1L));

        assertEquals("Usuário não encontrado com id: 1", exception.getMessage());
    }

    @Test
    void testDeleteById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        String result = userService.deleteById(1L);

        assertEquals("Usuario deletado com sucesso.", result);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteById(1L));

        assertEquals("Usuário não encontrado com id: 1", exception.getMessage());
    }

    @Test
    void testUpdateById_Success() {
        // Dados de entrada
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setPassword("newpassword");
        updatedUser.setDepartment(department);
        updatedUser.setIsAdmin(0);

        // Mocking do comportamento do repositório
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User updated = userService.updateById(1L, updatedUser);

        assertEquals("Updated Name", updated.getName());
        assertEquals("newpassword", updated.getPassword());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("Updated Name", capturedUser.getName());
        assertEquals("newpassword", capturedUser.getPassword());
        assertEquals(0, capturedUser.getIsAdmin());
        assertEquals(department, capturedUser.getDepartment());
    }

    @Test
    void testUpdateById_NotFound() {
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setPassword("newpassword");
        updatedUser.setDepartment(department);
        updatedUser.setIsAdmin(0);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateById(1L, updatedUser));

        assertEquals("Usuário não encontrado com id: 1", exception.getMessage());
    }

    @Test
    void testFindUsersByDepartment_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(userRepository.findByDepartment(department)).thenReturn(List.of(user));

        List<User> users = userService.findUsersByDepartment(1L);

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals("John Doe", users.get(0).getName());
    }

    @Test
    void testFindUsersByDepartment_DepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.findUsersByDepartment(1L));

        assertEquals("Departamento não encontrado", exception.getMessage());
    }


}
