import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
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

        System.out.println("SubTasks:");
        for (Subtask t : taskManager.getAllSubtasks()) {
            System.out.println(t.toString());
        }

        System.out.println("Epics");
        for (Epic t : taskManager.getAllEpics()) {
            System.out.println(t.toString());
        }

        System.out.println("History:");
        System.out.println(taskManager.getHistory());
        System.out.println("------");

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);

        System.out.println("History:");
        for (Task th : taskManager.getHistory()) {
            System.out.println(th);
        }
        System.out.println("------");

        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(6);

        System.out.println("History:");
        for (Task th : taskManager.getHistory()) {
            System.out.println(th);
        }
        System.out.println("------");

        taskManager.deleteTaskById(2);

        System.out.println("History:");
        for (Task th : taskManager.getHistory()) {
            System.out.println(th);
        }
        System.out.println("------");

        taskManager.getEpicById(3);
        taskManager.getEpicById(4);

        System.out.println("History:");
        for (Task th : taskManager.getHistory()) {
            System.out.println(th);
        }
        System.out.println("------");

        taskManager.deleteEpicById(3);

        System.out.println("History:");
        for (Task th : taskManager.getHistory()) {
            System.out.println(th);
        }
        System.out.println("------");

    }
}
