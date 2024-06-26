import com.google.gson.*;
import manager.Managers;
import manager.TaskManager;
import model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import web.HttpTaskServer;
import web.handler.BaseHttpHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    public HttpTaskManagerTasksTest() {
        manager = Managers.getDefault();
        taskServer = new HttpTaskServer(manager);
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.start();
        gson = BaseHttpHandler.getGson();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetTasks() throws InterruptedException, IOException {
        Task task = new Task("Test 1", "Testing task 1");
        Task task2 = new Task("Test 2", "Testing task 2");
        task.setStartTime(Optional.of(LocalDateTime.now()));
        task.setDuration(Duration.ofMinutes(60));

        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());


        request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(jsonArray.size(),2);
    }

    @Test
    public void testGetTask() throws InterruptedException, IOException {
        Task task = new Task("Test 1", "Testing task 1");
        Task task2 = new Task("Test 2", "Testing task 2");
        task.setStartTime(Optional.of(LocalDateTime.now()));
        task.setDuration(Duration.ofMinutes(60));

        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());


        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task t = gson.fromJson(response.body(), Task.class);
        task.setId(1);
        assertEquals(t,task);
        assertNotEquals(t, task2);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/3")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddTask() throws InterruptedException, IOException {
        Task task = new Task("Test 2", "Testing task 2");
        task.setStartTime(Optional.of(LocalDateTime.now()));
        task.setDuration(Duration.ofMinutes(30));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Task list is empty");
        assertEquals(1, tasksFromManager.size(), "Incorrect task count");
        assertEquals("Test 2", tasksFromManager.get(0).getTitle(), "Incorrect task name");

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testUpdateTask() throws InterruptedException, IOException {
        Task task = new Task("Test 1", "Testing task 1");
        Task task2 = new Task("Test 2", "Testing task 2");
        task.setStartTime(Optional.of(LocalDateTime.now()));
        task.setDuration(Duration.ofMinutes(60));

        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task3 = new Task("Test 1 updated", "Testing task 1");
        task3.setId(1);
        String taskJson3 = gson.toJson(task3);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(task3, manager.getTaskById(1));
        assertNotEquals(task, manager.getTaskById(1));

    }

    @Test
    public void testDeleteTask() throws InterruptedException, IOException {
        Task task = new Task("Test 1", "Testing task 1");
        Task task2 = new Task("Test 2", "Testing task 2");
        task.setStartTime(Optional.of(LocalDateTime.now()));
        task.setDuration(Duration.ofMinutes(60));

        String taskJson = gson.toJson(task);
        String taskJson2 = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/1"))
                .DELETE().build();
        HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(manager.getAllTasks().size(),1);

    }
}