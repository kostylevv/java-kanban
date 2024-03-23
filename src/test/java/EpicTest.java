import manager.Managers;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private static manager.TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
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

        assertTrue(manager.getAllEpics().size() == 3);

        manager.deleteEpicById(2);

        assertTrue(manager.getEpicById(2) == null);
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

        assertTrue(manager.getAllEpics().size() == 0);
    }
}
