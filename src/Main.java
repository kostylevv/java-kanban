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

        System.out.println("Delete subtask2");
        taskManager.deleteSubtaskById(subtask2.getId());

        System.out.println("Epic1:");
        System.out.println(epic1.toString());

        Subtask subtask3 = new Subtask("St3", "st3d", TaskStatusEnum.DONE, epic1.getId());
        System.out.println("Add subtask3 to epic1");
        taskManager.addSubtask(subtask3);
        System.out.println("Epic1:");
        System.out.println(epic1.toString());

        System.out.println("Chng status in subtask3 to IN_PROG");
        subtask3.setStatus(TaskStatusEnum.IN_PROGRESS);
        System.out.println(subtask3.toString());
        System.out.println("Replace subtaks3");
        taskManager.updateSubtask(subtask3);
        System.out.println("Epic1 is:");
        System.out.println(epic1.toString());

        System.out.println("Delete all tasks");
        taskManager.deleteAllTasks();
        System.out.println("Size of tasks storage:");
        System.out.println(taskManager.getAllTasks().size());

        System.out.println("Subtasks:");
        for (Subtask t : taskManager.getAllSubtasks()) {
            System.out.println(t.toString());
        }

        System.out.println("Epics:");
        for (Epic t : taskManager.getAllEpics()) {
            System.out.println(t.toString());
        }

        System.out.println("Delete epic");
        taskManager.deleteEpicById(epic.getId());

        System.out.println("Subtasks:");
        for (Subtask t : taskManager.getAllSubtasks()) {
            System.out.println(t.toString());
        }

        System.out.println("Epics:");
        for (Epic t : taskManager.getAllEpics()) {
            System.out.println(t.toString());
        }

        Task t1 = new Task("t1", "test desc 1", TaskStatusEnum.NEW);
        Task t2 = new Task("t1", "test desc 1", TaskStatusEnum.NEW);
        Task t3 = new Task("t1", "test desc 1", TaskStatusEnum.NEW);
        Task t4 = new Task("t1", "test desc 1", TaskStatusEnum.NEW);
        Task t5 = new Task("t1", "test desc 1", TaskStatusEnum.NEW);

        taskManager.addTask(t1);
        taskManager.addTask(t2);
        taskManager.addTask(t3);
        taskManager.addTask(t4);
        taskManager.addTask(t5);

        Epic e1 = new Epic("e1", "e1d");
        Epic e2 = new Epic("e2", "e1d");
        taskManager.addEpic(e1);
        taskManager.addEpic(e2);

        Subtask st1 = new Subtask("St1", "st1d", TaskStatusEnum.NEW, e1.getId());
        Subtask st2 = new Subtask("St2", "st1d", TaskStatusEnum.NEW, e1.getId());
        Subtask st3 = new Subtask("St3", "st1d", TaskStatusEnum.NEW, e2.getId());
        Subtask st4 = new Subtask("St4", "st1d", TaskStatusEnum.NEW, e2.getId());

        taskManager.addSubtask(st1);
        taskManager.addSubtask(st2);
        taskManager.addSubtask(st3);
        taskManager.addSubtask(st4);

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

        System.out.println("Task with id 9: " + taskManager.getTaskById(9));
        System.out.println("Task with id 10: " + taskManager.getTaskById(10));
        System.out.println("Task with id 11 " + taskManager.getTaskById(11));
        System.out.println("Task with id 12: " + taskManager.getTaskById(12));
        System.out.println("Task with id 13: " + taskManager.getTaskById(13));

        System.out.println("History:");
        List<Task> hst = taskManager.getHistory();
        for (Task t : hst) {
            System.out.println(t);
        }
        System.out.println("------");

        System.out.println("Epic with id 4: " + taskManager.getEpicById(4));
        System.out.println("Epic with id 14: " + taskManager.getEpicById(14));
        System.out.println("Epic with id 15: " + taskManager.getEpicById(15));

        System.out.println("History:");
        hst = taskManager.getHistory();
        for (Task t : hst) {
            System.out.println(t);
        }
        System.out.println("------");

        System.out.println("Subtask with id 16: " + taskManager.getSubTaskById(16));
        System.out.println("Subtask with id 17: " + taskManager.getSubTaskById(17));
        System.out.println("Subtask with id 18: " + taskManager.getSubTaskById(18));

        System.out.println("History:");
        hst = taskManager.getHistory();
        for (Task t : hst) {
            System.out.println(t);
        }
        System.out.println("------");

    }
}
