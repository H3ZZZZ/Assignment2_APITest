package org.example.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Task;
import org.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Set up ObjectMapper for JSON conversion, especially for LocalDate
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testGetTasks_ReturnsListOfTasks() throws Exception {
        // Arrange: Create and save tasks in the repository
        Task task1 = new Task(null, "Title 1", "Description 1", LocalDate.now(), "Category 1");
        Task task2 = new Task(null, "Title 2", "Description 2", LocalDate.now(), "Category 2");
        taskRepository.saveAll(List.of(task1, task2));

        // Act & Assert: Perform GET request and verify the response
        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())  // Expect HTTP 200 status
            .andExpect(jsonPath("$", hasSize(2)))  // Expect two tasks in the response
            .andExpect(jsonPath("$[0].title", is("Title 1")))  // Check first task's title
            .andExpect(jsonPath("$[1].title", is("Title 2")));  // Check second task's title
    }

    @Test
    public void testAddTask_ValidTask_ReturnsCreatedTask() throws Exception {
        // Arrange: Create a task
        Task task = new Task(null, "Title", "Description", LocalDate.now(), "Category");

        // Act & Assert: Perform POST request and verify the response
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isOk())  // Expect HTTP 200 status
            .andExpect(jsonPath("$.title", is("Title")))  // Check the task's title
            .andExpect(jsonPath("$.description", is("Description")));  // Check the task's description
    }
    @Test
    public void testUpdateTask_ValidTask_ReturnsUpdatedTask() throws Exception {
        // Arrange: Create and save a task in the repository
        Task task = new Task(null, "Title", "Description", LocalDate.now(), "Category");
        taskRepository.save(task);

        // Update the task's title and description
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");

        // Act & Assert: Perform PUT request and verify the response
        mockMvc.perform(put("/tasks/{id}", task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
            .andExpect(status().isOk())  // Expect HTTP 200 status
            .andExpect(jsonPath("$.title", is("Updated Title")))  // Check the updated title
            .andExpect(jsonPath("$.description", is("Updated Description")));  // Check the updated description
    }
    @Test
    public void testDeleteTask_ValidTask_ReturnsNoContent() throws Exception {
        // Arrange: Create and save a task in the repository
        Task task = new Task(null, "Title", "Description", LocalDate.now(), "Category");
        taskRepository.save(task);

        // Act & Assert: Perform DELETE request and verify the response
        mockMvc.perform(delete("/tasks/{id}", task.getId()))
            .andExpect(status().isNoContent());  // Expect HTTP 204 status

        // Test catching exception
        mockMvc.perform(delete("/tasks/{id}", task.getId()))
            .andExpect(status().isNotFound());  // Expect HTTP 404 status

    }

    @Test
    public void testMarkAsCompleted_ValidTask_ReturnsCompletedTask() throws Exception {
        // Arrange: Create and save a task in the repository
        Task task = new Task(null, "Title", "Description", LocalDate.now(), "Category");
        taskRepository.save(task);

        // Act & Assert: Perform PUT request and verify the response
        mockMvc.perform(put("/tasks/{id}/complete", task.getId()))
            .andExpect(status().isOk())  // Expect HTTP 200 status
            .andExpect(jsonPath("$.status", is("COMPLETED")));  // Check the task's status
    }
}
