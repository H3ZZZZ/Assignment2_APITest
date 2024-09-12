package org.example.service;

import org.example.exception.TaskNotFoundException;
import org.example.model.Task;
import org.example.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task addTask(Task task) {
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
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id " + id);
        }
        taskRepository.deleteById(id);
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
}