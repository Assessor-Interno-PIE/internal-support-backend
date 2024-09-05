package app.service;

import app.entity.Department;
import app.repository.DepartmentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public String save(@Valid Department department) {
        departmentRepository.save(department);
        return "Departamento salvo com sucesso";
    }

    public Department findById(@Valid Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado com id: " + id));
    }

    public List<Department> findAll() {
        List<Department> departments = departmentRepository.findAll();
        if (departments.isEmpty()) {
            throw new RuntimeException("Não há departamentos registrados!");
        } else {
            return departments;
        }
    }

    public String deleteById(@Valid Long id) {
        // Verifica se o departamento existe
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado com id: " + id));
        // Agora pode deletar o departamento
        departmentRepository.deleteById(id);
        return "Departamento deletado com sucesso.";
    }

    public Department updateById(@Valid Long id, Department updatedDepartment) {
        return departmentRepository.findById(id)
                .map(department -> {
                    department.setName(updatedDepartment.getName());
                    department.setDocuments(updatedDepartment.getDocuments());
                    department.setUsers(updatedDepartment.getUsers());
                    return departmentRepository.save(department);
                })
                .orElseThrow(() -> new RuntimeException("Departamento não encontrado com id: " + id));
    }
}
