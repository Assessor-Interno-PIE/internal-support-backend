package app.controller;

import app.DTO.AccessLevelDTO;
import app.DTO.LogsDTO;
import app.DTO.UserDTO;
import app.ModelMapperConfig.LogsMapper;
import app.entity.AccessLevel;
import app.entity.Logs;
import app.entity.User;
import app.service.LogsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogsController {

    @Autowired
    private LogsService logsService;
    @Autowired
    private LogsMapper logsMapper;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody LogsDTO logsDTO) {
        Logs logs = logsMapper.toLogs(logsDTO);

        String message = logsService.save(logs);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<LogsDTO> findById(@PathVariable Long id) {
        Logs log = logsService.findById(id);

        LogsDTO logsDTO = logsMapper.toLogsDTO(log);
        return new ResponseEntity<>(logsDTO, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<LogsDTO>> findAll() {
        List<Logs> logs = logsService.findAll();

        List<LogsDTO> logsDTOS = logs.stream()
                .map(logsMapper::toLogsDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(logsDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = logsService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<LogsDTO> updateById(@Valid @PathVariable Long id, @RequestBody LogsDTO logsDTO) {
        Logs log = logsMapper.toLogs(logsDTO);
        Logs updatedLogs = logsService.updateById(id, log);
        LogsDTO updatedLogsDTO = logsMapper.toLogsDTO(updatedLogs);

        return new ResponseEntity<>(updatedLogsDTO, HttpStatus.OK);
    }
}
