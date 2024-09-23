package org.example.service;

import org.example.model.Task;
import org.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        // Initializes mocks annotated with @Mock before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddTask_ValidationFails_TitleTooShort() {
        // Arrange: Create a task with a title that's too short
        Task task = new Task(null, "A", "Valid description", LocalDate.now(), "Category");

        // Act & Assert: Expect a ResponseStatusException when validation fails
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            taskService.addTask(task);
        });

        // Assert that the correct exception message is thrown
        assertEquals("400 BAD_REQUEST \"Task title must be at least 3 characters long\"", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void testAddTask_ValidationFails_TitleTooLong() {
        // Arrange: Create a task with a title that's too long (over 50 characters)
        String longTitle = "A".repeat(51); // Title with 51 characters
        Task task = new Task(null, longTitle, "Valid description", LocalDate.now(), "Category");

        // Act & Assert: Expect a ResponseStatusException when validation fails
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            taskService.addTask(task);
        });

        // Assert that the correct exception message is thrown
        assertEquals("400 BAD_REQUEST \"Task title must be no more than 50 characters long\"", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void testAddTask_ValidTitleLength() {
        // Arrange: Create a task with a valid title length
        Task task = new Task(null, "Valid Title", "Valid description", LocalDate.now(), "Category");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act: Save the task using the service
        Task savedTask = taskService.addTask(task);

        // Assert: Verify the saved task's title and that save was called once
        assertEquals("Valid Title", savedTask.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testAddTask() {
        // Arrange: Create a task and set up the mock to return it when saved
        Task task = new Task(null, "Title", "Description", LocalDate.now(), "Category");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act: Save the task using the service
        Task savedTask = taskService.addTask(task);

        // Assert: Verify the saved task's title and that save was called once
        assertEquals(task.getTitle(), savedTask.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testAddTask_ValidationFails_EmptyTitle() {
        // Arrange: Create a task with an empty title
        Task task = new Task(null, "", "Valid description", LocalDate.now(), "Category");

        // Act & Assert: Expect a ResponseStatusException when validation fails
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            taskService.addTask(task);
        });

        assertEquals("400 BAD_REQUEST \"Task title must not be empty\"", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void testAddTask_ValidationFails_ShortDescription() {
        // Arrange: Create a task with a description that's too short
        Task task = new Task(null, "Valid Title", "123", LocalDate.now(), "Category");

        // Act & Assert: Expect a ResponseStatusException when validation fails
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            taskService.addTask(task);
        });

        assertEquals("400 BAD_REQUEST \"Task description must be at least 5 characters long\"", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void testDeleteTask_Success() {
        // Arrange: Create a mock task and set up the repository to return it when findById is called
        Task task = new Task(1L, "Title", "Description", LocalDate.now(), "Category");
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));  // Mock task found

        // Act: Call deleteTask with a valid ID
        taskService.deleteTask(1L);

        // Assert: Verify that findById was called once and delete was called once with the correct task
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(task);  // Change this to delete(task) instead of deleteById
    }



    @Test
    public void testGetTasks() {
        // Arrange: Create a list of tasks and set up the mock to return it
        List<Task> tasks = List.of(new Task(1L, "Title", "Description", LocalDate.now(), "Category"));
        when(taskRepository.findAll()).thenReturn(tasks);

        // Act: Retrieve tasks using the service
        List<Task> result = taskService.getTasks();

        // Assert: Verify the number of tasks retrieved and that findAll was called once
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateTask_TaskNotFound() {
        // Arrange: Set up the mock to return empty when a task ID is not found
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: Expect a RuntimeException when updating a non-existent task
        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.updateTask(1L, new Task(1L, "Title", "Description", LocalDate.now(), "Category"));
        });

        // Verify the exception message
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    public void testUpdateTask_ExistingTask_ValidData() {
        // Arrange: Create an existing task and the updated task details
        Task existingTask = new Task(1L, "Old Title", "Old Description", LocalDate.now(), "Category");
        Task updatedTaskDetails = new Task(1L, "New Title", "New Description", LocalDate.now(), "Category");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        // Act: Update the task
        Task updatedTask = taskService.updateTask(1L, updatedTaskDetails);

        // Assert: Verify that the task's title and description were updated and save was called
        assertEquals("New Title", updatedTask.getTitle());
        assertEquals("New Description", updatedTask.getDescription());
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    public void testMarkAsCompleted_ExistingTaskId_MarksTaskAsCompleted() {
        // Arrange: Create a task, set it to PENDING, and set up the mock to return it
        Task existingTask = new Task(1L, "Title", "Description", LocalDate.now(), "Category");
        existingTask.setStatus(Task.TaskStatus.PENDING);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        // Act: Mark the task as completed using the service
        Task result = taskService.markAsCompleted(1L);

        // Assert: Verify the task's status is COMPLETED and that findById and save were called once
        assertEquals(Task.TaskStatus.COMPLETED, result.getStatus());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    public void testMarkAsCompleted_TaskNotFound() {
        // Arrange: Set up the mock to return empty when a task ID is not found
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: Expect a RuntimeException when marking a non-existent task as completed
        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.markAsCompleted(1L);
        });

        // Verify the exception message
        assertEquals("Task not found", exception.getMessage());
    }
}
