package app.controller;

import app.entity.Logs;
import app.service.LogsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogsController {

    @Autowired
    private LogsService logsService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody Logs logs) {
        String message = logsService.save(logs);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<Logs> findById(@PathVariable Long id) {
        Logs log = logsService.findById(id);
        return new ResponseEntity<>(log, HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Logs>> findAll() {
        List<Logs> logs = logsService.findAll();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @DeleteMapping("/delete-by-id/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        String responseMessage = logsService.deleteById(id);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("/update-by-id/{id}")
    public ResponseEntity<Logs> updateById(@Valid @PathVariable Long id, @RequestBody Logs updatedLogs) {
        Logs log = logsService.updateById(id, updatedLogs);
        return new ResponseEntity<>(log, HttpStatus.OK);
    }
}
