package org.example.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskNotFoundExceptionTest {
    @Test
    public void testTaskNotFoundException() {
        String errorMessage = "Task not found";
        TaskNotFoundException exception = new TaskNotFoundException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }
}
