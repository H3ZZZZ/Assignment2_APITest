document.getElementById('addTaskForm').addEventListener('submit', function(event) {
    event.preventDefault();

    // Clear any previous error messages
    const errorMessage = document.getElementById('error-message');
    if (errorMessage) {
        errorMessage.remove();
    }

    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    const deadline = document.getElementById('deadline').value;
    const category = document.getElementById('category').value;

    fetch('http://localhost:8080/tasks', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ title, description, deadline, category })
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message);
                });
            }
            return response.json();
        })
        .then(task => {
            console.log('Task added:', task);
            loadTasks(); // Reload the list of tasks
        })
        .catch(error => {
            console.error('Error adding task:', error);
            displayErrorMessage(error.message); // Display error message to user
        });
});

function loadTasks() {
    fetch('http://localhost:8080/tasks')
        .then(response => response.json())
        .then(tasks => {
            const taskList = document.getElementById('taskList');
            taskList.innerHTML = ''; // Clear existing tasks
            tasks.forEach(task => {
                const li = document.createElement('li');
                li.textContent = `${task.title} - ${task.description} (Due: ${task.deadline}) `;

                // Create delete button
                const deleteBtn = document.createElement('button');
                deleteBtn.textContent = 'Delete';
                deleteBtn.onclick = function() { deleteTask(task.id); };
                li.appendChild(deleteBtn);

                taskList.appendChild(li);
            });
        })
        .catch(error => console.error('Error loading tasks:', error));
}

function deleteTask(taskId) {
    fetch(`http://localhost:8080/tasks/${taskId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                loadTasks(); // Reload tasks to update the list
            }
        })
        .catch(error => console.error('Error deleting task:', error));
}

function displayErrorMessage(message) {
    const form = document.getElementById('addTaskForm');
    const errorDiv = document.createElement('div');
    errorDiv.id = 'error-message';
    errorDiv.style.color = 'red';
    errorDiv.textContent = message;
    form.appendChild(errorDiv);
}

// Load tasks on initial page load
loadTasks();