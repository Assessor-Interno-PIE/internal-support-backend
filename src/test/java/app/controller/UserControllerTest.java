package app.controller;

import app.entity.Department;
import app.entity.User;
import app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testSaveUser() throws Exception {
        Department department = new Department();
        department.setId(1L);

        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setId(1L);

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setDepartment(department);
        user.setAccessLevel(accessLevel);

        when(userService.save(any(User.class))).thenReturn("Usuário salvo com sucesso.");

        String userJson = "{"
                + "\"name\":\"Test User\","
                + "\"email\":\"test@example.com\","
                + "\"password\":\"password\","
                + "\"department\":{\"id\":1},"
                + "\"accessLevel\":{\"id\":1}"
                + "}";

        mockMvc.perform(post("/api/users/save")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuário salvo com sucesso."));

        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    public void testFindById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userService.findById(anyLong())).thenReturn(user);

        mockMvc.perform(get("/api/users/find-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    public void testFindAll() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User());

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users/find-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(userService, times(1)).findAll();
    }

    @Test
    public void testDeleteById() throws Exception {
        when(userService.deleteById(anyLong())).thenReturn("Usuário deletado com sucesso.");

        mockMvc.perform(delete("/api/users/delete-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário deletado com sucesso."));

        verify(userService, times(1)).deleteById(1L);
    }

    @Test
    public void testUpdateById() throws Exception {
        User updatedUser = new User();
        updatedUser.setName("Updated User");

        when(userService.updateById(anyLong(), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/update-by-id/1")
                        .contentType("application/json")
                        .content("{\"name\":\"Updated User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated User"));

        verify(userService, times(1)).updateById(anyLong(), any(User.class));
    }
}