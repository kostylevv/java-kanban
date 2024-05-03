import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatusEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import web.HttpTaskServer;
import web.handler.BaseHttpHandler;
import web.handler.TaskListTypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HttpTaskManagerPriorityTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson;

    public HttpTaskManagerPriorityTest() {
        manager = Managers.getDefault();
        taskServer = new HttpTaskServer(manager);
    }

    @BeforeEach
    public void setUp() {
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
    public void testGetPrioritized() throws InterruptedException, IOException {
        Task task = new Task(TaskStatusEnum.NEW, "test title task1", "test desc 1");
        Task task1 = new Task(TaskStatusEnum.IN_PROGRESS, "test title taks2", "test desc2 ");
        task.setDuration(60);
        task.setStartTime("2024-03-29T16:30:00");//3
        task1.setDuration(25);
        task1.setStartTime("2024-03-12T16:30:00");//2
        manager.addTask(task);
        manager.addTask(task1);

        Epic epic = new Epic("e1", "e1d");
        Epic epic1 = new Epic("e2", "e2d");

        manager.addEpic(epic);
        manager.addEpic(epic1);

        Subtask subtask = new Subtask(TaskStatusEnum.NEW, "St1", "st1d", epic.getId());
        Subtask subtask1 = new Subtask(TaskStatusEnum.NEW, "St2", "st2d", epic.getId());
        Subtask subtask2 = new Subtask(TaskStatusEnum.NEW, "St3", "st3d", epic1.getId());
        Subtask subtask3 = new Subtask(TaskStatusEnum.NEW, "St4", "st4d", epic1.getId());

        subtask.setDuration(60);
        subtask.setStartTime("2024-02-15T16:30:00");//0

        subtask1.setDuration(60);
        subtask1.setStartTime("2028-02-15T16:30:00");//4

        subtask2.setStartTime("2024-03-12T16:00:00");//1

        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> parsed = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(parsed.size(), 5);
        assertEquals(parsed, manager.getPrioritizedTasks());

    }

}

