package app.service;

import app.entity.Department;
import app.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    @Test
    void testSaveDepartment() {
        Department department = new Department();
        String result = departmentService.save(department);

        verify(departmentRepository, times(1)).save(department);
        assertEquals("Departamento salvo com sucesso", result);
    }

    @Test
    void testFindByIdFound() {
        Long id = 1L;
        Department department = new Department();
        department.setId(id);

        when(departmentRepository.findById(id)).thenReturn(java.util.Optional.of(department));

        Department result = departmentService.findById(id);

        verify(departmentRepository, times(1)).findById(id);
        assertEquals(department, result);
    }

    @Test
    void testFindByIdNotFound() {
        Long id = 1L;

        when(departmentRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> departmentService.findById(id));
        verify(departmentRepository, times(1)).findById(id);
    }

    @Test
    void testFindAllWithDepartments() {
        Department department1 = new Department();
        Department department2 = new Department();
        List<Department> departmentsList = List.of(department1, department2);

        when(departmentRepository.findAll()).thenReturn(departmentsList);

        List<Department> result = departmentService.findAll();

        assertEquals(departmentsList, result);
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void testFindAllEmpty() {
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.findAll();
        });

        assertEquals("Não há departamentos registrados!", exception.getMessage());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void testDeleteByIdSuccess() {
        long id = 1L;
        Department department = new Department();
        department.setId(id);

        when(departmentRepository.findById(id)).thenReturn(java.util.Optional.of(department));

        String result = departmentService.deleteById(id);

        verify(departmentRepository, times(1)).deleteById(id);
        assertEquals("Departamento deletado com sucesso.", result);
    }

    @Test
    void testDeleteByIdNotFound() {
        Long id = 1L;

        when(departmentRepository.findById(id)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> departmentService.deleteById(id));
        assertEquals("Departamento não encontrado com id: " + id, exception.getMessage());
        verify(departmentRepository, never()).deleteById(id);
    }

    @Test
    void testUpdateByIdSuccess() {
        Long id = 1L;
        Department existingDepartment = new Department();
        existingDepartment.setId(id);
        existingDepartment.setName("Old Name");

        Department updatedDepartment = new Department();
        updatedDepartment.setName("New Name");

        when(departmentRepository.findById(id)).thenReturn(java.util.Optional.of(existingDepartment));
        when(departmentRepository.save(existingDepartment)).thenReturn(existingDepartment);

        Department result = departmentService.updateById(id, updatedDepartment);

        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, times(1)).save(existingDepartment);
        assertEquals("New Name", result.getName());
    }

    @Test
    void testUpdateByIdNotFound() {
        Long id = 1L;
        Department updatedDepartment = new Department();
        updatedDepartment.setName("New Name");

        when(departmentRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> departmentService.updateById(id, updatedDepartment));
        verify(departmentRepository, times(1)).findById(id);
    }
}
