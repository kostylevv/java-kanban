import manager.Managers;
import manager.exception.NotFoundException;
import model.Epic;
import model.Subtask;
import model.TaskStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private static manager.TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void epicStatusIsNew() {
        Epic epic = new Epic("e1", "e1d");
        manager.addEpic(epic);

        Subtask task = new Subtask(TaskStatusEnum.NEW, "test title task1", "test desc 1", epic.getId());
        Subtask task2 = new Subtask(TaskStatusEnum.NEW,"test title task2", "test desc 2", epic.getId());
        Subtask task3 = new Subtask(TaskStatusEnum.NEW,"test title task3", "test desc 3", epic.getId());

        manager.addSubtask(task2);
        manager.addSubtask(task);
        manager.addSubtask(task3);

        assertEquals(epic.getStatus(), TaskStatusEnum.NEW);
    }

    @Test
    public void epicStatusIsDone() {
        Epic epic = new Epic("e1", "e1d");
        manager.addEpic(epic);

        Subtask task = new Subtask(TaskStatusEnum.DONE, "test title task1", "test desc 1", epic.getId());
        Subtask task2 = new Subtask(TaskStatusEnum.DONE,"test title task2", "test desc 2", epic.getId());
        Subtask task3 = new Subtask(TaskStatusEnum.DONE,"test title task3", "test desc 3", epic.getId());

        manager.addSubtask(task2);
        manager.addSubtask(task);
        manager.addSubtask(task3);

        assertEquals(epic.getStatus(), TaskStatusEnum.DONE);
    }

    @Test
    public void epicStatusIsInProgress() {
        Epic epic = new Epic("e1", "e1d");
        manager.addEpic(epic);

        Subtask task = new Subtask(TaskStatusEnum.DONE, "test title task1", "test desc 1", epic.getId());
        Subtask task2 = new Subtask(TaskStatusEnum.DONE,"test title task2", "test desc 2", epic.getId());
        Subtask task3 = new Subtask(TaskStatusEnum.NEW,"test title task3", "test desc 3", epic.getId());

        manager.addSubtask(task2);
        manager.addSubtask(task);
        manager.addSubtask(task3);

        assertEquals(epic.getStatus(), TaskStatusEnum.IN_PROGRESS);
    }

    @Test
    public void epicStatusIsInProgress2() {
        Epic epic = new Epic("e1", "e1d");
        manager.addEpic(epic);

        Subtask task = new Subtask(TaskStatusEnum.DONE, "test title task1", "test desc 1", epic.getId());
        Subtask task2 = new Subtask(TaskStatusEnum.IN_PROGRESS,"test title task2", "test desc 2", epic.getId());
        Subtask task3 = new Subtask(TaskStatusEnum.DONE,"test title task3", "test desc 3", epic.getId());

        manager.addSubtask(task2);
        manager.addSubtask(task);
        manager.addSubtask(task3);

        assertEquals(epic.getStatus(), TaskStatusEnum.IN_PROGRESS);
    }

    @Test
    public void epicIsAdded() {
        Epic epic = new Epic("e1", "e1d");
        manager.addEpic(epic);
        assertEquals(epic, manager.getEpicById(1));
    }

    @Test
    public void epicIsDeleted() {
        Epic epic = new Epic("e1", "e1d");
        manager.addEpic(epic);
        manager.deleteEpicById(1);
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void multipleEpicsAreAddedAndDeleted() {
        Epic epic = new Epic("e1", "e1d");
        Epic epic2 = new Epic("e2", "e1d");
        Epic epic3 = new Epic("e3", "e1d");

        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.addEpic(epic3);

        assertEquals(3, manager.getAllEpics().size());

        manager.deleteEpicById(2);

        assertThrows(NotFoundException.class, () -> manager.getEpicById(2));
    }

    @Test
    public void allTasksAreDeleted() {
        Epic epic = new Epic("e1", "e1d");
        Epic epic2 = new Epic("e2", "e1d");
        Epic epic3 = new Epic("e3", "e1d");

        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.addEpic(epic3);

        manager.deleteAllEpics();

        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    public void timeBoundsAreSet() {
        Epic epic = new Epic("e1", "e1d");
        manager.addEpic(epic);
        assertTrue(epic.getStartTime().isEmpty());
        assertTrue(epic.getDuration().get().isZero());
        assertTrue(epic.getEndTime().isEmpty());

        Subtask task = new Subtask("test title task1", "test desc 1", epic.getId());
        Subtask task2 = new Subtask("test title task2", "test desc 2", epic.getId());
        Subtask task3 = new Subtask("test title task3", "test desc 3", epic.getId());

        task.setStartTime("2024-03-23T20:00:00");
        task.setDuration(60);

        task2.setStartTime("2024-03-25T20:00:00");
        task2.setDuration(60);

        task3.setStartTime("2024-03-26T20:00:00");
        task3.setDuration(60);

        manager.addSubtask(task2);
        manager.addSubtask(task);
        manager.addSubtask(task3);

        assertTrue(epic.getEndTime().isPresent(), epic.getEndTime().get().toString());
        assertEquals(180, epic.getDuration().get().toMinutes());
        assertEquals(epic.getStartTime().get(), LocalDateTime.parse("2024-03-23T20:00:00"));
        assertEquals(epic.getEndTime().get(), LocalDateTime.parse("2024-03-26T21:00:00"));

        manager.deleteSubtaskById(task3.getId());
        assertEquals(epic.getEndTime().get(), LocalDateTime.parse("2024-03-25T21:00:00"));

        manager.deleteAllSubTasks();
        assertTrue(epic.getStartTime().isEmpty());
        assertTrue(epic.getDuration().get().isZero());
        assertTrue(epic.getEndTime().isEmpty());
    }
}
