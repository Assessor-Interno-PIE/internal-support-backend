package app.controller;

import app.entity.Document;
import app.entity.User;
import app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody User user) {
        String message = userService.save(user);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = userService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<User> updateById(@Valid @PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateById(id, updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/by-department/{id}")
    public ResponseEntity<List<User>> getUsersByDepartment(@PathVariable Long id) {
        List<User> users = userService.findUsersByDepartment(id);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
