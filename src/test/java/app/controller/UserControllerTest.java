package app.controller;

import app.entity.Department;
import app.entity.User;
import app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        User user = new User();
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("123");
        user.setDepartment(department);
        user.setIsAdmin(0);

        when(userService.save(any(User.class))).thenReturn("Usuário salvo com sucesso.");

        String userJson = "{"
                + "\"name\":\"Test User\","
                + "\"username\":\"testUsername\","
                + "\"password\":\"123\","
                + "\"department\":{\"id\":1},"
                + "\"isAdmin\":0"
                + "}";

        mockMvc.perform(post("/api/users/save")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
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
        user.setUsername("testUsername");

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/find-by-id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Test User\",\"username\":\"testUsername\"}"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    public void testFindAll() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User One");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");

        when(userService.findAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users/find-all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"User One\"},{\"id\":2,\"name\":\"User Two\"}]"));

        verify(userService, times(1)).findAll();
    }

    @Test
    public void testUpdateById() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated User");

        when(userService.updateById(eq(1L), any(User.class))).thenReturn(updatedUser);

        String userJson = "{"
                + "\"name\":\"Updated User\","
                + "\"username\":\"updatedUsername\""
                + "}";

        mockMvc.perform(put("/api/users/update-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Updated User\"}"));

        verify(userService, times(1)).updateById(eq(1L), any(User.class));
    }


    @Test
    public void testGetUsersByDepartment() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Department User");

        when(userService.findUsersByDepartment(1L)).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/by-department/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Department User\"}]"));

        verify(userService, times(1)).findUsersByDepartment(1L);
    }

    @Test
    public void testGetUsersByNameContaining() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");

        when(userService.findUsersByNameContaining("Test")).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/search/name-contains")
                        .param("keyword", "Test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Test User\"}]"));

        verify(userService, times(1)).findUsersByNameContaining("Test");
    }


    @Test
    public void testDeleteById() throws Exception {
        Long userId = 1L;
        when(userService.deleteById(userId)).thenReturn("Usuario deletado com sucesso.");

        mockMvc.perform(delete("/api/users/delete-by-id/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario deletado com sucesso."));

        verify(userService, times(1)).deleteById(userId);
    }
}
