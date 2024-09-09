package app.controller;

import app.DTO.AccessLevelDTO;
import app.DTO.DepartmentDTO;
import app.ModelMapperConfig.DepartmentMapper;
import app.entity.AccessLevel;
import app.entity.Department;
import app.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentMapper departmentMapper;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody DepartmentDTO departmentDTO) {
        Department department = departmentMapper.toDepartment(departmentDTO);

        String message = departmentService.save(department);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<DepartmentDTO> findById(@PathVariable Long id) {
        Department department = departmentService.findById(id);

        DepartmentDTO departmentDTO = departmentMapper.toDepartmentDTO(department);
        return new ResponseEntity<>(departmentDTO, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<DepartmentDTO>> findAll() {
        List<Department> departments = departmentService.findAll();

        List<DepartmentDTO> departmentDTOS = departments.stream()
                .map(departmentMapper::toDepartmentDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(departmentDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = departmentService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<DepartmentDTO> updateById(@Valid @PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {

        Department department = departmentMapper.toDepartment(departmentDTO);
        Department updatedDepartment = departmentService.updateById(id, department);
        DepartmentDTO updatedDepartmentDTO = departmentMapper.toDepartmentDTO(updatedDepartment);
        return new ResponseEntity<>(updatedDepartmentDTO, HttpStatus.OK);
    }
}
