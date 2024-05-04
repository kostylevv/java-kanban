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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerEpicsTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    public HttpTaskManagerEpicsTest() {
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
    public void testGetEpics() throws InterruptedException, IOException {
        Epic epic = new Epic("epic 1", "desc of epic 1");  //id=1
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1); //id=2
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",1); //id=3
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("epic 2", "desc of epic 2");  //id=4
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("ST3 title", "ST3 desc",4); //id=2
        manager.addSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(jsonArray.size(),2);
    }

    @Test
    public void testGetEpic() throws InterruptedException, IOException {
        Epic epic = new Epic("epic 1", "desc of epic 1");  //id=1
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1); //id=2
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",1); //id=3
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("epic 2", "desc of epic 2");  //id=4
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("ST3 title", "ST3 desc",4); //id=2
        manager.addSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/4")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        Epic t = gson.fromJson(response.body(), Epic.class);

        epic2.setId(4);
        assertEquals(t,epic2);
        assertNotEquals(t, epic);

        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/3")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testAddEpic() throws InterruptedException, IOException {
        Epic epic = new Epic("epic 1", "d");

        String eJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(eJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = manager.getAllEpics();

        assertNotNull(tasksFromManager, "Epics list is empty");
        assertEquals(1, tasksFromManager.size(), "Incorrect epic count");
        assertEquals("epic 1", tasksFromManager.get(0).getTitle(), "Incorrect epic name");
    }

    @Test
    public void testDeleteEpic() throws InterruptedException, IOException {
        Epic epic = new Epic("epic 1", "desc of epic 1");  //id=1
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1); //id=2
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",1); //id=3
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("epic 2", "desc of epic 2");  //id=4
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("ST3 title", "ST3 desc",4); //id=2
        manager.addSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/1"))
                .DELETE().build();
        HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(manager.getAllEpics().size(),1);
    }

    @Test
    public void testGetEpicSubtasks() throws InterruptedException, IOException {
        Epic epic = new Epic("epic 1", "desc of epic 1");  //id=1
        manager.addEpic(epic);
        Subtask subtask = new Subtask("ST1 title", "ST1 desc",1); //id=2
        Subtask subtask2 = new Subtask("ST2 title", "ST2 desc",1); //id=3
        manager.addSubtask(subtask);
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("epic 2", "desc of epic 2");  //id=4
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("ST3 title", "ST3 desc",4); //id=2
        manager.addSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics/1/subtasks"))
                .GET().build();
        HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> parsed = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        epic.setId(1);
        assertEquals(parsed, manager.getSubtasks(epic));
    }

}
