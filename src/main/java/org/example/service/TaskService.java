package org.example.service;

import org.example.exception.TaskNotFoundException;
import org.example.model.Task;
import org.example.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private static final int MIN_DESCRIPTION_LENGTH = 5;
    private static final int MIN_TITLE_LENGTH = 3; // Minimum title length
    private static final int MAX_TITLE_LENGTH = 50; // Maximum title length

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task addTask(Task task) {
        validateTask(task);
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setTitle(taskDetails.getTitle());
            task.setDescription(taskDetails.getDescription());
            task.setStatus(taskDetails.getStatus());
            task.setDeadline(taskDetails.getDeadline());
            task.setCategory(taskDetails.getCategory());

            validateTask(task); // Validate before updating
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found."));
        taskRepository.delete(task);
    }


    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Task markAsCompleted(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(Task.TaskStatus.COMPLETED);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    // Validation method
    private void validateTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task title must not be empty");
        }

        if (task.getTitle().length() < MIN_TITLE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task title must be at least " + MIN_TITLE_LENGTH + " characters long");
        }

        if (task.getTitle().length() > MAX_TITLE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task title must be no more than " + MAX_TITLE_LENGTH + " characters long");
        }

        if (task.getDescription() == null || task.getDescription().length() < MIN_DESCRIPTION_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task description must be at least " + MIN_DESCRIPTION_LENGTH + " characters long");
        }
    }
}