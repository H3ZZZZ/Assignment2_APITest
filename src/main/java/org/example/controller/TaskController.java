package org.example.controller;


import org.example.exception.TaskNotFoundException;
import org.example.model.Task;
import org.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost") // Adjust the port if your HTML is served from a different one
@RestController
@RequestMapping("/tasks")
public class TaskController {

//    FOR AT TESTE IN MEMORY DATABASEN:
//    Open your browser and navigate to http://localhost:8080/h2-console.
//    Use the following settings:
//    JDBC URL: jdbc:h2:mem:testdb
//    User Name: sa
//    Password: password
//    Click on "Connect".
    
    @Autowired
    private TaskService taskService;

    @PostMapping
    public Task addTask(@RequestBody Task task) {
        return taskService.addTask(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Task deleted successfully.");
        } catch (TaskNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping
    public List<Task> getTasks() {
        return taskService.getTasks();
    }

    @PutMapping("/{id}/complete")
    public Task markAsCompleted(@PathVariable Long id) {
        return taskService.markAsCompleted(id);
    }
}