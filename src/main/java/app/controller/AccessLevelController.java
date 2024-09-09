package app.controller;

import app.DTO.AccessLevelDTO;
import app.DTO.UserDTO;
import app.ModelMapperConfig.AccessLevelMapper;
import app.entity.AccessLevel;
import app.entity.User;
import app.service.AccessLevelService;
import jakarta.persistence.Access;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/access-levels")
public class AccessLevelController {

    @Autowired
    private AccessLevelService accessLevelService;

    @Autowired
    private AccessLevelMapper accessLevelMapper;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody AccessLevelDTO accessLevelDTO) {
        AccessLevel accessLevel = accessLevelMapper.toAccessLevel(accessLevelDTO);

        String message = accessLevelService.save(accessLevel);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<AccessLevelDTO> findById(@PathVariable Long id) {
        AccessLevel accessLevel = accessLevelService.findById(id);

        AccessLevelDTO accessLevelDTO = accessLevelMapper.toAccessLevelDTO(accessLevel);
        return new ResponseEntity<>(accessLevelDTO, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<AccessLevelDTO>> findAll() {
        List<AccessLevel> accessLevels = accessLevelService.findAll();

        List<AccessLevelDTO> accessLevelDTOS = accessLevels.stream()
                .map(accessLevelMapper::toAccessLevelDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(accessLevelDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = accessLevelService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<AccessLevelDTO> updateById(@Valid @PathVariable Long id, @RequestBody AccessLevelDTO accessLevelDTO) {
        AccessLevel accessLevel = accessLevelMapper.toAccessLevel(accessLevelDTO);
        AccessLevel updatedAccessLevel = accessLevelService.updateById(id, accessLevel);
        AccessLevelDTO updatedAccessLevelDTO = accessLevelMapper.toAccessLevelDTO(updatedAccessLevel);
        return new ResponseEntity<>(updatedAccessLevelDTO, HttpStatus.OK);
    }
}
