import manager.Managers;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    private static manager.TaskManager manager;
    private Epic epic;


    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
        epic = new Epic("e1", "e1d");
        manager.addEpic(epic);
    }

    @Test
    public void subtaskIsAdded() {
        Subtask subtask = new Subtask("subt title", "subt desc", epic.getId());
        manager.addSubtask(subtask);
        assertEquals(subtask, manager.getSubTaskById(2));
        assertTrue(manager.getSubTaskById(2).getIdEpic() == epic.getId());
    }

    @Test
    public void multipleSubtasksAreAddedAndDeleted() {
        Subtask task = new Subtask("test title task1", "test desc 1", epic.getId());
        Subtask task2 = new Subtask("test title task2", "test desc 2", epic.getId());
        Subtask task3 = new Subtask("test title task3", "test desc 3", epic.getId());

        manager.addSubtask(task);
        manager.addSubtask(task2);
        manager.addSubtask(task3);

        assertTrue(manager.getAllSubtasks().size() == 3);

        manager.deleteSubtaskById(2);

        assertTrue(manager.getSubTaskById(2) == null);
    }

    @Test
    public void allSubtasksAreDeleted() {
        Subtask task = new Subtask("test title task1", "test desc 1", epic.getId());
        Subtask task2 = new Subtask("test title task2", "test desc 2", epic.getId());
        Subtask task3 = new Subtask("test title task3", "test desc 3", epic.getId());

        manager.addSubtask(task);
        manager.addSubtask(task2);
        manager.addSubtask(task3);

        manager.deleteAllSubTasks();

        assertTrue(manager.getAllSubtasks().size() == 0);
    }




}
