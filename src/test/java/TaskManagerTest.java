import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest<T extends TaskManager> {

    private static manager.TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    public void getAllTasksTest() {
        Task task = new Task(TaskStatusEnum.NEW, "test title task1", "test desc 1");
        Task task1 = new Task(TaskStatusEnum.IN_PROGRESS, "test title taks2", "test desc2 ");
        Task task2 = new Task(TaskStatusEnum.NEW, "test title task8", "test desc 1");
        Task task3 = new Task("test title taks2", "test desc2 ");
        assertEquals(manager.getAllTasks().size(),0);
        manager.addTask(task);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        assertEquals(manager.getAllTasks().size(),4);
    }

    @Test
    public void getAllSubtasksTest() {
        Epic epic = new Epic("e1", "e1d");
        Epic epic1 = new Epic("e2", "e2d");

        manager.addEpic(epic);
        manager.addEpic(epic1);

        Subtask subtask = new Subtask(TaskStatusEnum.NEW, "St1", "st1d", epic.getId());
        Subtask subtask1 = new Subtask(TaskStatusEnum.NEW, "St2", "st2d", epic.getId());
        Subtask subtask2 = new Subtask(TaskStatusEnum.NEW, "St3", "st3d", epic1.getId());
        Subtask subtask3 = new Subtask(TaskStatusEnum.NEW, "St3", "st3d", epic1.getId());
        Subtask subtask4 = new Subtask(TaskStatusEnum.NEW, "St3", "st3d", epic1.getId());

        assertEquals(manager.getAllSubtasks().size(),0);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask4);
        assertEquals(manager.getAllSubtasks().size(),5);
    }

    @Test
    public void getAllEpicsTest() {
        Epic epic = new Epic("e1", "e1d");
        Epic epic1 = new Epic("e2", "e2d");

        assertEquals(manager.getAllEpics().size(),0);
        manager.addEpic(epic);
        manager.addEpic(epic1);
        assertEquals(manager.getAllEpics().size(),2);
    }

    private void idTestsSetup() {
        Task task = new Task(TaskStatusEnum.NEW, "test title task1", "test desc 1");
        Task task1 = new Task(TaskStatusEnum.IN_PROGRESS, "test title taks2", "test desc2 ");

        Epic epic = new Epic("e1", "e1d");
        Epic epic1 = new Epic("e2", "e2d");

        manager.addTask(task);
        manager.addTask(task1);

        manager.addEpic(epic);
        manager.addEpic(epic1);

        Subtask subtask = new Subtask(TaskStatusEnum.NEW, "St1", "st1d", epic.getId());
        Subtask subtask1 = new Subtask(TaskStatusEnum.NEW, "St2", "st2d", epic.getId());
        Subtask subtask2 = new Subtask(TaskStatusEnum.NEW, "St3", "st3d", epic.getId());

        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
    }

    @Test
    public void getTaskByIdTest() {
        idTestsSetup();
        assertEquals(manager.getTaskById(1).getTitle(), "test title task1");
    }

    @Test
    public void getSubtaskByIdTest() {
        idTestsSetup();
        assertEquals(manager.getSubTaskById(6).getTitle(), "St2");
    }

    @Test
    public void getEpicByIdTest() {
        idTestsSetup();
        assertEquals(manager.getEpicById(3).getTitle(), "e1");
    }

    @Test
    public void tasksAreAddedWithPriority() {
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

        assertEquals(manager.getPrioritizedTasks().size(), 5);
        assertEquals(manager.getPrioritizedTasks().get(0), subtask);
        assertEquals(manager.getPrioritizedTasks().get(4), subtask1);
        assertNotEquals(manager.getPrioritizedTasks().get(2), task);
        assertNotEquals(manager.getPrioritizedTasks().get(1), task1);
    }

    @Test
    public void tasksAreDeletedWithPriority() {
        Task task = new Task(TaskStatusEnum.NEW, "test title task1", "test desc 1");
        Task task1 = new Task(TaskStatusEnum.IN_PROGRESS, "test title taks2", "test desc2 ");
        task.setDuration(60);
        task.setStartTime("2024-03-29T16:30:00");//3
        task1.setDuration(60);
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
        subtask.setDuration(60);
        subtask.setStartTime("2024-02-15T16:30:00");//0

        subtask1.setDuration(60);
        subtask1.setStartTime("2028-02-15T16:30:00");//4

        subtask2.setStartTime("2024-03-12T16:00:00");//1

        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        assertEquals(manager.getPrioritizedTasks().size(), 5);

        manager.deleteTaskById(1);

        assertEquals(manager.getPrioritizedTasks().size(), 4);
        assertEquals(manager.getPrioritizedTasks().get(3), subtask1);

        manager.deleteAllSubTasks();

        assertEquals(manager.getPrioritizedTasks().size(), 1);
    }

    @Test
    public void taskOverlapTestBaseCaseFalse() {
        Task task = new Task(TaskStatusEnum.NEW, "test title task1", "test desc 1");
        Task task1 = new Task(TaskStatusEnum.IN_PROGRESS, "test title taks2", "test desc2 ");
        task.setDuration(60);
        task.setStartTime("2024-01-01T12:00:00");
        task1.setDuration(60);
        task1.setStartTime("2024-01-01T13:00:00");
        manager.addTask(task);
        manager.addTask(task1);
        assertFalse(manager.isTasksOverlap(task, task1));
    }

    @Test
    public void taskOverlapTestBaseCaseTrue() {
        Task task = new Task(TaskStatusEnum.NEW, "test title task1", "test desc 1");
        Task task1 = new Task(TaskStatusEnum.IN_PROGRESS, "test title taks2", "test desc2 ");
        task.setDuration(60);
        task.setStartTime("2024-01-01T12:00:00");
        task1.setDuration(60);
        task1.setStartTime("2024-01-01T12:59:00");
        manager.addTask(task);
        manager.addTask(task1);
        assertTrue(manager.isTasksOverlap(task, task1));
    }

}
