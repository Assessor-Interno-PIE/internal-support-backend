package app.controller;

import app.entity.Department;
import app.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody Department department) {
        String message = departmentService.save(department);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<Department> findById(@PathVariable Long id) {
        Department department = departmentService.findById(id);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Department>> findAll() {
        List<Department> departments = departmentService.findAll();
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = departmentService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<Department> updateById(@Valid @PathVariable Long id, @RequestBody Department updatedDepartment) {
        Department department = departmentService.updateById(id, updatedDepartment);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }
}
