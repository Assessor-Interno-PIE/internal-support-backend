package app.controller;

import app.entity.Department;
import app.entity.Document;
import app.entity.User;
import app.service.DepartmentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
    }

    @Test
    public void testSaveDepartment() throws Exception {
        Department department = new Department();
        department.setId(1L);
        department.setName("Finance");

        when(departmentService.save(any(Department.class))).thenReturn("Departamento salvo com sucesso!");

        mockMvc.perform(post("/api/departments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Finance\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Departamento salvo com sucesso!"));

        verify(departmentService, times(1)).save(any(Department.class));
    }

    @Test
    public void testFindById() throws Exception {
        Department department = new Department();
        department.setId(1L);
        department.setName("Finance");

        when(departmentService.findById(1L)).thenReturn(department);

        mockMvc.perform(get("/api/departments/find-by-id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Finance\"}"));

        verify(departmentService, times(1)).findById(1L);
    }

    @Test
    public void testFindAllDepartments() throws Exception {
        Department department1 = new Department();
        department1.setId(1L);
        department1.setName("Finance");

        Department department2 = new Department();
        department2.setId(2L);
        department2.setName("HR");

        when(departmentService.findAll()).thenReturn(List.of(department1, department2));

        mockMvc.perform(get("/api/departments/find-all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Finance\"},{\"id\":2,\"name\":\"HR\"}]"));

        verify(departmentService, times(1)).findAll();
    }

    @Test
    public void testDeleteDepartmentById() throws Exception {
        when(departmentService.deleteById(1L)).thenReturn("Departamento deletado com sucesso!");

        mockMvc.perform(delete("/api/departments/delete-by-id/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Departamento deletado com sucesso!"));

        verify(departmentService, times(1)).deleteById(1L);
    }

    @Test
    public void testSaveDepartmentFailure() throws Exception {
        mockMvc.perform(post("/api/departments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(departmentService, never()).save(any(Department.class));
    }


    @Test
    public void testDepartmentStatsByIdSuccess() throws Exception {
        Long id = 1L;
        Department department = new Department(); // Preencha com dados de teste
        department.setUsers(new ArrayList<>(Arrays.asList(new User(), new User()))); // 2 usuários
        department.setDocuments(new ArrayList<>(Arrays.asList(new Document()))); // 1 documento

        when(departmentService.findById(id)).thenReturn(department);

        mockMvc.perform(get("/api/departments/department-stats/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfUsers").value(2))
                .andExpect(jsonPath("$.numberOfDocuments").value(1));

        verify(departmentService, times(1)).findById(id);
    }

    @Test
    public void testGetDepartmentsByNameContainingFound() throws Exception {
        String keyword = "Finance";
        List<Document> documents = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<Department> departments = new ArrayList<>();

        departments.add(new Department(1L, "Finance Department", documents, users));
        departments.add(new Department(2L, "Financial Services", documents, users));

        when(departmentService.findDepartmentsByNameContaining(keyword)).thenReturn(departments);

        mockMvc.perform(get("/api/departments/search/name-contains?keyword={keyword}", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))  // Verifica se a lista tem 2 elementos
                .andExpect(jsonPath("$[0].name").value("Finance Department"))
                .andExpect(jsonPath("$[1].name").value("Financial Services"));

        verify(departmentService, times(1)).findDepartmentsByNameContaining(keyword);
    }

    @Test
    public void testUpdateByIdSuccess() throws Exception {
        Long id = 1L;
        Department updatedDepartment = new Department(id, "Updated Department", new ArrayList<>(), new ArrayList<>());

        // Simula o comportamento do serviço
        when(departmentService.updateById(eq(id), any(Department.class))).thenReturn(updatedDepartment);

        mockMvc.perform(put("/api/departments/update-by-id/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Department\"}")) // Corpo da requisição JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Department"));

        verify(departmentService, times(1)).updateById(eq(id), any(Department.class));
    }

}

