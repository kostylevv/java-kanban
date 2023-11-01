public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task = new Task("test title task1", "test desc 1", TASK_STATUS.NEW);
        Task task1 = new Task("test title taks2", "test desc2 ", TASK_STATUS.IN_PROGRESS);

        Epic epic = new Epic("e1", "e1d");
        Epic epic1 = new Epic("e2", "e2d");

        taskManager.addTask(task);
        taskManager.addTask(task1);

        taskManager.addEpic(epic);
        taskManager.addEpic(epic1);

        Subtask subtask = new Subtask("St1", "st1d", TASK_STATUS.NEW, epic.getId());
        Subtask subtask1 = new Subtask("St2", "st2d", TASK_STATUS.NEW, epic.getId());
        Subtask subtask2 = new Subtask("St3", "st3d", TASK_STATUS.NEW, epic1.getId());

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

        Subtask subtask3 = new Subtask("St3", "st3d", TASK_STATUS.DONE, epic1.getId());
        System.out.println("Add subtask3 to epic1");
        taskManager.addSubtask(subtask3);
        System.out.println("Epic1:");
        System.out.println(epic1.toString());

        System.out.println("Chng status in subtask3 to IN_PROG");
        subtask3.setStatus(TASK_STATUS.IN_PROGRESS);
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
    }
}
