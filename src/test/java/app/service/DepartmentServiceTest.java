package app.service;

import app.entity.Department;
import app.entity.User;
import app.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DepartmentServiceTest {

    @InjectMocks
    DepartmentService departmentService;

    @InjectMocks
    UserService userService;

    @Mock
    DepartmentRepository departmentRepository;

    private User adminUser;
    private Department department;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        department = new Department();
        department.setId(1L);
        department.setName("Finance");
        department.setDocuments(List.of());
        department.setUsers(List.of());

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setName("Admin User");
        adminUser.setUsername("admin");
        adminUser.setPassword("password123");
        adminUser.setIsAdmin(1);
        adminUser.setDepartment(department);

        department.setUsers(List.of(adminUser));
    }

    @Test
    void testSave() {
        Department department = new Department();
        department.setName("HR");

        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        String result = departmentService.save(department);

        assertEquals("Departamento salvo com sucesso", result);
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void findByIdTest() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Department foundDepartment = departmentService.findById(1L);

        assertNotNull(foundDepartment);
        assertEquals(department.getName(), foundDepartment.getName());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void findByIdNotFoundTest() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.findById(1L);
        });

        assertEquals("Departamento não encontrado com id: 1", exception.getMessage());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAllSuccess() {
        Department department2 = new Department();
        department2.setName("HR");

        List<Department> departments = Arrays.asList(department, department2);

        when(departmentRepository.findAll()).thenReturn(departments);

        List<Department> result = departmentService.findAll();

        assertEquals(2, result.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void testFindAllEmpty() {
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> departmentService.findAll());
    }

    @Test
    void testFindAllPaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Department department2 = new Department();
        department2.setName("HR");

        List<Department> departments = Arrays.asList(department, department2);
        Page<Department> page = new PageImpl<>(departments, pageable, departments.size());

        when(departmentRepository.findAll(pageable)).thenReturn(page);

        Page<Department> result = departmentService.findAllPaginated(pageable);

        assertEquals(2, result.getContent().size());
        verify(departmentRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindAllPaginatedEmpty() {
        Pageable pageable = PageRequest.of(0, 10);

        when(departmentRepository.findAll(pageable)).thenReturn(Page.empty());

        assertThrows(RuntimeException.class, () -> departmentService.findAllPaginated(pageable));
    }

    @Test
    void deleteByIdTest() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        String result = departmentService.deleteById(1L);

        assertEquals("Departamento deletado com sucesso.", result);
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteByIdNotFoundTest() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.deleteById(1L);
        });

        assertEquals("Departamento não encontrado com id: 1", exception.getMessage());
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, never()).deleteById(1L);
    }

    @Test
    void testUpdateByIdSuccess() {
        Department updatedDepartment = new Department();
        updatedDepartment.setName("IT");

        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDepartment);

        Department result = departmentService.updateById(department.getId(), updatedDepartment);

        assertEquals("IT", result.getName());
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void testUpdateByIdNotFound() {
        Department updatedDepartment = new Department();
        updatedDepartment.setName("IT");

        when(departmentRepository.findById(department.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> departmentService.updateById(department.getId(), updatedDepartment));
    }

    @Test
    void testFindDepartmentsByNameContaining() {
        String keyword = "Finance";
        Department department2 = new Department();
        department2.setName("HR");

        List<Department> departments = Arrays.asList(department);

        when(departmentRepository.findByNameContaining(keyword)).thenReturn(departments);

        List<Department> result = departmentService.findDepartmentsByNameContaining(keyword);

        assertEquals(1, result.size());
        assertEquals("Finance", result.get(0).getName());
        verify(departmentRepository, times(1)).findByNameContaining(keyword);
    }


}
