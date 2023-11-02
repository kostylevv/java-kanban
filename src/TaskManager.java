import java.util.*;

public class TaskManager {

    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subTasks;
    private Map<Integer, Epic> epics;
    private int lastId;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        lastId = 0;
    }

    /*
    Получение списков всех задач
     */
    public List<Task> getAllTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<Subtask>(subTasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    /*
    Получение задач по идентификатору
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    /*
    Удаление по идентификатору
     */
    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subTasks.get(id);
        Epic epic = epics.get(subtask.getIdEpic());

        subTasks.remove(id);

        epic.deleteSubtask(id);

        identifyEpicStatus(epic);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        deleteAllEpicSubtasks(epic);
        epics.remove(id);
    }

    /*
    Получение подзадач эпика
     */
    public List<Subtask> getSubtasks(Epic epic) {
        List<Subtask> result = new ArrayList<>();
        if (epic != null && epics.containsKey(epic.getId())) {
            for (int id : epic.getSubtasks()) {
                result.add(subTasks.get(id));
            }
        } else {
            System.out.println("Epic == null или нет эпика с ID = " + epic.getId());
        }
        return result;
    }

    /*
    Удаление всех задач
     */
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            identifyEpicStatus(epic);
        }
    }

    public void deleteAllEpics() {
        deleteAllSubTasks();
        epics.clear();
    }

    /*
    Обновление задач
     */

    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Task == null или задачи не существует");
        }
    }

    public void updateSubtask(Subtask task) {
        if (task != null && subTasks.containsKey(task.getId())) {
            subTasks.put(task.getId(), task);
            Epic epic = getEpicById(task.getIdEpic());
            identifyEpicStatus(epic);
        } else {
            System.out.println("Task == null или задачи не существует");
        }
    }

    public void updateEpic(Epic task) {
        if (task != null && epics.containsKey(task.getId())) {
            epics.put(task.getId(), task);
            identifyEpicStatus(task);
        } else {
            System.out.println("Task == null или задачи не существует");
        }
    }

    /*
    Добавление задач
     */

    public void addTask(Task task) {
        if (task != null && task.getType() == TaskTypeEnum.TASK) {
            task.setId(getTaskId());
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Task == null или неверный тип задачи");
        }
    }

    public void addSubtask(Subtask task) {
        if (task != null && task.getType() == TaskTypeEnum.SUBTASK) {
            if (epics.containsKey(task.getIdEpic())) {
                task.setId(getTaskId());
                subTasks.put(task.getId(), task);
                Epic epic = epics.get(task.getIdEpic());
                epic.addSubtask(task.getId());
                identifyEpicStatus(epic);
            } else {
                System.out.println("Невозможно добавить подзадачу для несуществующего " +
                        "эпика с ID = " + task.getIdEpic());
            }
        } else {
            System.out.println("Task == null или неверный тип задачи");
        }
    }

    public void addEpic(Epic task) {
        if (task != null && task.getType() == TaskTypeEnum.EPIC) {
            if (subTasks.keySet().containsAll(task.getSubtasks())) {
                task.setId(getTaskId());
                epics.put(task.getId(), task);
                identifyEpicStatus(task);
            } else {
                System.out.println("Невозможно добавить эпик c несуществующими подзадачами");
            }
        } else {
            System.out.println("Task == null или неверный тип задачи");
        }
    }

    /*
    Метод следит за статусом эпика исходя из статусов подзадач
     */
    private void identifyEpicStatus(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            if (epic.getSubtasks().isEmpty()) {
                epic.setStatus(TaskStatusEnum.NEW);
            } else {
                boolean statusNew = false;
                boolean statusDone = false;

                List<Subtask> subtasks = this.getSubtasks(epic);
                for (Subtask subtask : subtasks) {
                    if (subtask.getStatus() == TaskStatusEnum.IN_PROGRESS) {
                        epic.setStatus(TaskStatusEnum.IN_PROGRESS);
                        return;
                    } else if (subtask.getStatus() == TaskStatusEnum.NEW) {
                        statusNew = true;
                    } else if (subtask.getStatus() == TaskStatusEnum.DONE) {
                        statusDone = true;
                    }
                }

                if (statusDone == true && statusNew == false) {
                    epic.setStatus(TaskStatusEnum.DONE);
                } else if (statusDone = false && statusNew == true) {
                    epic.setStatus(TaskStatusEnum.NEW);
                } else {
                    epic.setStatus(TaskStatusEnum.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Epic == null или нет эпика с ID = " + epic.getId());
        }
    }

    private int getTaskId() {
        return ++lastId;
    }

    private void deleteAllEpicSubtasks(Epic epic) {
        for (int id : epic.getSubtasks()) {
            subTasks.remove(id);
        }
    }

}
