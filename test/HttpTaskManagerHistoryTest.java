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
import web.handler.SubtaskListTypeToken;
import web.handler.TaskListTypeToken;

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


public class HttpTaskManagerHistoryTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    public HttpTaskManagerHistoryTest() {
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
    public void testGetHistory() throws InterruptedException, IOException {
        Task task = new Task("Test 1", "Testing task 1");
        Task task2 = new Task("Test 2", "Testing task 2");
        manager.addTask(task);
        manager.addTask(task2);

        Epic epic = new Epic("t", "d");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",3);
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",3);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(1);

        manager.getSubTaskById(4);
        manager.getSubTaskById(4);
        manager.getSubTaskById(4);
        manager.getSubTaskById(5);

        manager.getEpicById(3);
        manager.getTaskById(2);
        manager.getTaskById(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> parsed = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(parsed.size(), 5);
        assertEquals(parsed, manager.getHistory());

        //delete one task and check again
        manager.deleteTaskById(1);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        parsed = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(parsed.size(), 4);
        assertEquals(parsed, manager.getHistory());

    }

}

