import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerSubtasksTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    public HttpTaskManagerSubtasksTest() {
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
    public void testGetSubtasks() throws InterruptedException, IOException {
        Epic epic = new Epic("t", "d");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1);
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",1);
        subtask.setStartTime(Optional.of(LocalDateTime.now()));
        subtask.setDuration(Duration.ofMinutes(60));

        String staskJson = gson.toJson(subtask);
        String staskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson2)).build();
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
    public void testGetSubtask() throws InterruptedException, IOException {
        Epic epic = new Epic("t", "d");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1);
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",1);
        subtask.setStartTime(Optional.of(LocalDateTime.now()));
        subtask.setDuration(Duration.ofMinutes(60));

        String staskJson = gson.toJson(subtask);
        String staskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());


        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/2")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask t = gson.fromJson(response.body(), Subtask.class);
        subtask.setId(2);
        assertEquals(t,subtask);
        assertNotEquals(t, subtask2);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/4")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddSubtask() throws InterruptedException, IOException {
        Epic epic = new Epic("t", "d");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1);
        subtask.setStartTime(Optional.of(LocalDateTime.now()));
        subtask.setDuration(Duration.ofMinutes(60));

        String staskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Task list is empty");
        assertEquals(1, tasksFromManager.size(), "Incorrect task count");
        assertEquals("ST1 title", tasksFromManager.get(0).getTitle(), "Incorrect task name");

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testUpdateSubtask() throws InterruptedException, IOException {
        Epic epic = new Epic("t", "d");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1);
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",1);
        subtask.setStartTime(Optional.of(LocalDateTime.now()));
        subtask.setDuration(Duration.ofMinutes(60));

        String staskJson = gson.toJson(subtask);
        String staskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask3 = new Subtask("ST2 title upd", "ST2 desc",1);
        subtask3.setId(3);
        String taskJson3 = gson.toJson(subtask3);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/3"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(subtask3, manager.getSubTaskById(3));
        assertNotEquals(subtask3, manager.getSubTaskById(2));

    }

    @Test
    public void testDeleteTask() throws InterruptedException, IOException {
        Epic epic = new Epic("t", "d");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1);
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",1);
        subtask.setStartTime(Optional.of(LocalDateTime.now()));
        subtask.setDuration(Duration.ofMinutes(60));

        String staskJson = gson.toJson(subtask);
        String staskJson2 = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(staskJson2)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks/2"))
                .DELETE().build();
        HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(manager.getAllSubtasks().size(),1);

    }
}
