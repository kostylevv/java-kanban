import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatusEnum;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("test title task1", "test desc 1", TaskStatusEnum.NEW);
        Task task1 = new Task("test title taks2", "test desc2 ", TaskStatusEnum.IN_PROGRESS);

        Epic epic = new Epic("e1", "e1d");
        Epic epic1 = new Epic("e2", "e2d");

        taskManager.addTask(task);
        taskManager.addTask(task1);

        taskManager.addEpic(epic);
        taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("St1", "st1d", TaskStatusEnum.NEW, epic.getId());
        Subtask subtask1 = new Subtask("St2", "st2d", TaskStatusEnum.NEW, epic.getId());
        Subtask subtask2 = new Subtask("St3", "st3d", TaskStatusEnum.NEW, epic1.getId());

        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        System.out.println("Tasks:");
        for (Task t : taskManager.getAllTasks()) {
            System.out.println(t.toString());
        }
        System.out.println("Subtasks:");
        for (Task t : taskManager.getAllSubtasks()) {
            System.out.println(t.toString());
        }
        System.out.println("Epics:");
        for (Task t : taskManager.getAllEpics()) {
            System.out.println(t.toString());
        }

        printHistory(taskManager);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        printHistory(taskManager);

        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        printHistory(taskManager);

        taskManager.getSubTaskById(6);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(7);
        taskManager.getEpicById(3);
        taskManager.getEpicById(4);

        printHistory(taskManager);

        taskManager.deleteTaskById(1);
        printHistory(taskManager);

        taskManager.deleteEpicById(3);
        printHistory(taskManager);

        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        printHistory(taskManager);

        taskManager.deleteAllEpics();
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager taskManager){
        System.out.println("--- History ---");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task.toString());
        }
        System.out.println("--- End of History ---");

    }
}
