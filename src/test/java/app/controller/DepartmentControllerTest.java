package app.controller;

import app.entity.Department;
import app.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import com.sun.jdi.InternalException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    @Test
    void saveDepartmentWithoutName() throws Exception {
        Department invalidDepartment = new Department();

        mockMvc.perform(post("/api/departments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDepartment)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro: O nome não pode estar vazio"));
    }

    @Test
    void saveDepartmentSuccess() throws Exception {
        Department validDepartment = new Department();
        validDepartment.setName("Departamento Exemplar");

        when(departmentService.save(any(Department.class))).thenReturn("Departamento criado com sucesso");

        mockMvc.perform(post("/api/departments/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDepartment)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Departamento criado com sucesso"));
    }

    @Test
    void findDepartmentByIdSuccess() throws Exception {
        Department department = new Department();
        department.setId(1L);
        department.setName("TI");

        when(departmentService.findById(1L)).thenReturn(department);

        mockMvc.perform(get("/api/departments/find-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("TI"));
    }

    @Test
    void findDepartmentByIdNotFound() throws Exception {
        when(departmentService.findById(anyLong())).thenThrow(new EntityNotFoundException("Departamento não encontrado com id: 1"));

        mockMvc.perform(get("/api/departments/find-by-id/1"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Departamento não encontrado com id: 1"));
    }

    @Test
    void findAllDepartmentsSuccess() throws Exception {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department());

        when(departmentService.findAll()).thenReturn(departments);
        mockMvc.perform(get("/api/departments/find-all"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    @Test
    void findAllDepartmentsWithoutRegistered() throws Exception {
        when(departmentService.findAll()).thenThrow(new InternalException("Não há departamentos registrados!"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/departments/find-all"))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Não há departamentos registrados!"));
    }

    @Test
    void updateDepartmentSuccess() throws Exception {
        Department updatedDepartment = new Department();
        updatedDepartment.setId(1L);
        updatedDepartment.setName("Departamento Atualizado");

        when(departmentService.updateById(anyLong(), any(Department.class))).thenReturn(updatedDepartment);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/departments/update-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDepartment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Departamento Atualizado"));
    }

    @Test
    void updateDepartmentNotFound() throws Exception {
        Department updatedDepartment = new Department();
        updatedDepartment.setName("Departamento Atualizado");

        when(departmentService.updateById(anyLong(), any(Department.class))).thenThrow(new EntityNotFoundException("Departamento não encontrado com id: 1"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/departments/update-by-id/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDepartment)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Departamento não encontrado com id: 1"));
    }

    @Test
    void deleteDepartmentByIdSuccess() throws Exception {
        when(departmentService.deleteById(1L)).thenReturn("Departamento excluído com sucesso");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/departments/delete-by-id/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Departamento excluído com sucesso"));
    }

    @Test
    void deleteDepartmentByIdFailure() throws Exception {
        when(departmentService.deleteById(1L)).thenThrow(new EntityNotFoundException("Departamento não encontrado com id: 1"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/departments/delete-by-id/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Departamento não encontrado com id: 1"));
    }
}