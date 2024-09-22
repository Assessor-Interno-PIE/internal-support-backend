package app.controller;

import app.entity.AccessLevel;
import app.entity.User;
import app.service.AccessLevelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.InternalException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessLevelController.class)
class AccessLevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessLevelService accessLevelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveAccessLevelSuccess() throws Exception {
        AccessLevel accessLevel = new AccessLevel();
        List<User> users = new ArrayList<>();
        accessLevel.setName("Exemplo de nível de acesso");
        accessLevel.setUsers(users);

        AccessLevel savedAccessLevel = new AccessLevel();
        savedAccessLevel.setId(1L); //expected
        savedAccessLevel.setName("Exemplo de nível de acesso");
        savedAccessLevel.setUsers(users);

        String successMessage = "Nivel de acesso salvo com sucesso";

        when(accessLevelService.save(any(AccessLevel.class))).thenReturn("Nivel de acesso salvo com sucesso");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/access-levels/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accessLevel)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Nivel de acesso salvo com sucesso"));
    }

    @Test
    void saveAccessLevelWithNoName() throws Exception {
        AccessLevel accessLevel = new AccessLevel();
        List<User> users = new ArrayList<>();
        accessLevel.setUsers(users);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/access-levels/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accessLevel)))
                .andExpect((status().isBadRequest()))
                .andExpect(MockMvcResultMatchers.content().string("Erro: O nome não pode estar em branco"));
    }

    @Test
    void saveAccessLevelWithInvalidName() throws Exception {
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setName("12345"); // Invalid name
        accessLevel.setUsers(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/access-levels/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accessLevel)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Erro: O nome deve conter apenas letras"));
    }

    @Test
    void findAccesLevelSuccess() throws Exception {
        AccessLevel accessLevel = new AccessLevel();
        accessLevel.setId(1L);
        accessLevel.setName("Nível de Acesso Exemplar");

        when(accessLevelService.findById(anyLong())).thenReturn(accessLevel);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/access-levels/find-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    void findAccesLevelFailure() throws Exception {
        when(accessLevelService.findById(anyLong())).thenThrow(new EntityNotFoundException("Nível de acesso não encontrado com id: 1"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/access-levels/find-by-id/1"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Nível de acesso não encontrado com id: 1"));
    }

    @Test
    void findAllAccessSuccess() throws Exception {
        List<AccessLevel> accessLevels = new ArrayList<>();
        accessLevels.add(new AccessLevel());

        when(accessLevelService.findAll()).thenReturn(accessLevels);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/access-levels/find-all"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    @Test
    void findAllAccessWithoutRegistredAccessLevels() throws Exception {
        when(accessLevelService.findAll()).thenThrow(new InternalException("Não há níveis de acesso registrados!"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/access-levels/find-all"))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Não há níveis de acesso registrados!"));
    }
}