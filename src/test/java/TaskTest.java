import manager.Managers;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class TaskTest {
    private static manager.TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void taskIsAdded() {
        Task task = new Task("test title task1", "test desc 1");
        manager.addTask(task);
        assertEquals(task, manager.getTaskById(1));
    }

    @Test
    public void taskIsDeleted() {
        Task task = new Task("test title task1", "test desc 1");
        manager.addTask(task);
        manager.deleteTaskById(1);
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void multipleTasksAreAddedAndDeleted() {
        Task task = new Task("test title task1", "test desc 1");
        Task task2 = new Task("test title task2", "test desc 2");
        Task task3 = new Task("test title task3", "test desc 3");

        manager.addTask(task);
        manager.addTask(task2);
        manager.addTask(task3);

        assertTrue(manager.getAllTasks().size() == 3);

        manager.deleteTaskById(2);

        assertTrue(manager.getTaskById(2) == null);
    }

    @Test
    public void allTasksAreDeleted() {
        Task task = new Task("test title task1", "test desc 1");
        Task task2 = new Task("test title task2", "test desc 2");
        Task task3 = new Task("test title task3", "test desc 3");

        manager.addTask(task);
        manager.addTask(task2);
        manager.addTask(task3);

        manager.deleteAllTasks();

        assertTrue(manager.getAllTasks().size() == 0);
    }

    @Test
    public void startTimeIsSet() {
        Task task = new Task("test title task1", "test desc 1");
        assertTrue(task.getStartTime().isEmpty());
        task.setStartTime("2024-03-23T20:00:00");
        LocalDateTime localDateTime = LocalDateTime.parse("2024-03-23T20:00:00");
        assertTrue(task.getStartTime().get().equals(localDateTime));
    }

    @Test
    public void durationIsSet() {
        Task task = new Task("test title task1", "test desc 1");
        assertTrue(task.getDuration().isEmpty());
        task.setDuration(60);
        assertTrue(task.getDuration().get().toMinutes() == 60);
    }

    @Test
    public void endTimeIsSet() {
        Task task = new Task("test title task1", "test desc 1");
        assertTrue(task.getEndTime().isEmpty());
        task.setStartTime("2024-03-23T20:00:00");
        task.setDuration(60);
        LocalDateTime localDateTime = LocalDateTime.parse("2024-03-23T21:00:00");
        assertTrue(task.getEndTime().get().equals(localDateTime));
    }
}
