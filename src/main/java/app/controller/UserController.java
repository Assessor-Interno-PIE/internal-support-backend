package app.controller;

import app.DTO.UserDTO;
import app.ModelMapperConfig.UserMapper;
import app.entity.User;
import app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody UserDTO userDTO) {
        // Mapping entity User to UserDTO
        User user = userMapper.toUser(userDTO);

        String message = userService.save(user);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        User user= userService.findById(id);

        // Mapping entity User to UserDTO
        UserDTO userDTO = userMapper.toUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<UserDTO>> findAll() {
        List<User> users = userService.findAll();

        List<UserDTO> userDTOS = users.stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = userService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<UserDTO> updateById(@Valid @PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userMapper.toUser(userDTO);
        User updatedUser = userService.updateById(id, user);
        UserDTO updatedUserDTO = userMapper.toUserDTO(updatedUser);
        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }

}
