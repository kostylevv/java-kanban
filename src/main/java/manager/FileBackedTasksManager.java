package manager;

import model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final String fileName;
    private static final String HEADER = "id,type,status,title,description,startTime,duration,id_epic/id_subtasks...";

    public FileBackedTasksManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        this.fileName = fileName;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fmgr = (FileBackedTasksManager) Managers.getDefault();

        Task task = new Task(TaskStatusEnum.NEW, "test title task1", "test desc 1");
        Task task1 = new Task(TaskStatusEnum.IN_PROGRESS, "test title taks2", "test desc2 ");
        task.setDuration(60);
        task.setStartTime("2024-03-29T16:30:00");
        task1.setDuration(60);
        task1.setStartTime("2024-03-12T16:30:00");


        Epic epic = new Epic("e1", "e1d");
        Epic epic1 = new Epic("e2", "e2d");

        fmgr.addTask(task);
        fmgr.addTask(task1);

        fmgr.addEpic(epic);
        fmgr.addEpic(epic1);

        Subtask subtask = new Subtask(TaskStatusEnum.NEW, "St1", "st1d", epic.getId());
        Subtask subtask1 = new Subtask(TaskStatusEnum.NEW, "St2", "st2d", epic.getId());
        Subtask subtask2 = new Subtask(TaskStatusEnum.NEW, "St3", "st3d", epic.getId());
        subtask.setDuration(60);
        subtask.setStartTime("2024-03-15T16:30:00");

        fmgr.addSubtask(subtask);
        fmgr.addSubtask(subtask1);
        fmgr.addSubtask(subtask2);

        System.out.println("---------------------");
        System.out.println("with priority");

        for (Task t : fmgr.getPrioritizedTasks()) {
            System.out.println(t.toString());
        }

        fmgr.getTaskById(1);
        fmgr.getTaskById(2);
        fmgr.getTaskById(2);
        fmgr.getTaskById(1);
        fmgr.getTaskById(2);
        fmgr.getTaskById(1);
        fmgr.getSubTaskById(6);
        fmgr.getSubTaskById(5);
        fmgr.getSubTaskById(5);
        fmgr.getSubTaskById(7);
        fmgr.getSubTaskById(7);

        System.out.println("Source fmgr:");
        System.out.println("Tasks:");
        for (Task t : fmgr.getAllTasks()) {
            System.out.println(t.toString());
        }
        System.out.println("Subtasks:");
        for (Task t : fmgr.getAllSubtasks()) {
            System.out.println(t.toString());
        }
        System.out.println("Epics:");
        for (Task t : fmgr.getAllEpics()) {
            System.out.println(t.toString());
        }
        printHistory(fmgr);

        System.out.println("Now create new fmgr from file:");

        FileBackedTasksManager fmgrRestored = (FileBackedTasksManager) Managers.getDefault();

        fmgrRestored.restoreFromFile();

        System.out.println("Tasks:");
        for (Task t : fmgrRestored.getAllTasks()) {
            System.out.println(t.toString());
        }
        System.out.println("Subtasks:");
        for (Task t : fmgrRestored.getAllSubtasks()) {
            System.out.println(t.toString());
        }
        System.out.println("Epics:");
        for (Task t : fmgrRestored.getAllEpics()) {
            System.out.println(t.toString());
        }
        printHistory(fmgrRestored);
    }

    @Override
    public Task getTaskById(int id) {
        Task t = super.getTaskById(id);
        save();
        return t;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask s = super.getSubTaskById(id);
        save();
        return s;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic e = super.getEpicById(id);
        save();
        return e;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    public void restoreFromFile() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            boolean parseHistory = false;
            while (fileReader.ready()) {
                String str = fileReader.readLine();
                if (str.isBlank()) {
                    parseHistory = true;
                } else if (!str.startsWith("id,") && !parseHistory) {
                    String[] words = str.split(",");
                    switch (words[1].trim().toUpperCase()) {
                        case "TASK" -> {
                            Task t = Task.fromString(str);
                            super.tasks.put(t.getId(), t);
                        }
                        case "SUBTASK" -> {
                            Subtask s = Subtask.fromString(str);
                            super.subTasks.put(s.getId(), s);
                        }
                        case "EPIC" -> {
                            Epic e = Epic.fromString(str);
                            super.epics.put(e.getId(), e);
                        }
                        default -> throw new ManagerSaveException("Type should be TASK|SUBTASK|EPIC, got " + words[1]);
                    }
                } else if (parseHistory) {
                    List<Integer> history = historyFromString(str);
                    for (int i : history) {
                        Task t = tasks.get(i);
                        if (t == null) {
                            t = subTasks.get(i);
                            if (t == null) {
                                t = epics.get(t);
                            }
                        }
                        if (t != null) {
                            historyManager.add(t);
                        } else {
                            throw new ManagerSaveException("Failed to found task with id=" + i + ".");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void save() {
        List<Task> tasks = this.getAllTasks();
        tasks.addAll(this.getAllSubtasks());
        tasks.addAll(this.getAllEpics());

        try (FileWriter out = new FileWriter(fileName)) {
             out.write(HEADER + "\n");
             for (Task t : tasks) {
                out.write(t.serialize() + "\n");
             }
             out.write("\n");
             out.write(historyToString(historyManager));
        } catch (Exception e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        if (value != null && !value.isEmpty()) {
            String[] words = value.split(",");
            for (String word : words) {
                try {
                    int i = Integer.parseInt(word);
                    result.add(i);
                } catch (ArithmeticException ae) {
                    throw new ManagerSaveException(ae.getMessage());
                }
            }
        }
        return result;
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> list = manager.getHistory();
        if (list.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (Task t : list) {
                sb.append(t.getId());
                sb.append(",");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        } else return "";
    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("--- History ---");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task.toString());
        }
        System.out.println("--- End of History ---");
    }
}
