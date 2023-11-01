import java.util.*;

public class TaskManager {

    private Map<Integer, Task> tasks;
    private Map<Integer, Subtask> subTasks;
    private Map<Integer, Epic> epics;
    private Set<Integer> taskIds; //хранит идентификаторы задач всех типов
    private int lastId;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        taskIds = new HashSet<>();
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
        taskIds.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subTasks.get(id);
        Epic epic = epics.get(subtask.getIdEpic());

        subTasks.remove(id);
        taskIds.remove(id);

        epic.deleteSubtask(id);

        epic.setStatus(identifyEpicStatus(epic));
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        deleteAllEpicSubtasks(epic);

        epics.remove(id);
        taskIds.remove(id);
    }

    /*
    Получение подзадач эпика
     */
    public List<Subtask> getSubtasks(Epic epic) {
        List<Subtask> result = new ArrayList<>();
        if (epic != null && epics.containsKey(epic.getId())) {
            for (int id : subTasks.keySet()) {
                if (epic.getSubtasks().contains(id)) {
                    result.add(subTasks.get(id));
                }
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
        taskIds.removeAll(tasks.keySet());
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        taskIds.removeAll(subTasks.keySet());
        subTasks.clear();
        for (Epic epic : epics.values()) {
            identifyEpicStatus(epic);
        }
    }

    public void deleteAllEpics() {
        deleteAllSubTasks();
        taskIds.removeAll(epics.keySet());
        epics.clear();
    }

    /*
    Добавление задач
    Один метод-интерфейс на все типы
    Не совсем уверен, что это правильное решение, но
    мне показалось оно приемлемым
     */
    public void add(Task task) {
        if (task != null && task.getType() != null) {
            if (!taskIds.contains(task.getId())) {
                switch (task.getType()) {
                    case TASK:
                        addTask(task);
                        break;
                    case SUBTASK:
                        addSubTask((Subtask) task);
                        break;
                    case EPIC:
                        addEpic((Epic) task);
                        break;
                    default:
                        System.out.println("Тип задачи не поддерживается");
                }
            } else {
                System.out.println("Задача с ID = " + task.getId() + " уже есть.");
            }
        } else {
            System.out.println("Task == null или не установлен тип задачи");
        }
    }

    /*
    Обновление задачи
     */
    public void updateTask(Task task) {
        if (taskIds.contains(task.getId())) {
            switch (task.getType()) {
                case TASK:
                    tasks.put(task.getId(), task);
                    break;
                case SUBTASK:
                    subTasks.put(task.getId(), (Subtask) task);
                    Epic epic = getEpicById(((Subtask) task).getIdEpic());
                    epic.setStatus(identifyEpicStatus(epic));
                    break;
                case EPIC:
                    epics.put(task.getId(), (Epic) task);
                    task.setStatus(identifyEpicStatus((Epic) task));
                    break;
                default:
                    System.out.println("Тип задачи не поддерживается");
            }
        } else {
            System.out.println("Такой задачи нет (ID = " + task.getId() + ")");
        }
    }

    /*
    Приватные методы
     */
    private void addTask(Task task) {
        task.setId(getTaskId());
        tasks.put(task.getId(), task);
        taskIds.add(task.getId());
    }

    private void addSubTask(Subtask task) {
        if (taskIds.contains(task.getIdEpic())) {
            task.setId(getTaskId());
            subTasks.put(task.getId(), task);
            taskIds.add(task.getId());
            Epic epic = epics.get(task.getIdEpic());
            epic.addSubtask(task.getId());
            epic.setStatus(identifyEpicStatus(epic));
        } else {
            System.out.println("Невозможно добавить подзадачу для несуществующего эпика с ID = " + task.getIdEpic());
        }
    }

    private void addEpic(Epic task) {
        if (task.getSubtasks() != null && taskIds.containsAll(task.getSubtasks())) {
            task.setId(getTaskId());
            epics.put(task.getId(), task);
            taskIds.add(task.getId());
            task.setStatus(this.identifyEpicStatus(task));
        } else {
            System.out.println("Невозможно добавить эпик c несуществующими подзадачами");
        }
    }

    /*
    Метод следит за статусом эпика исходя из статусов подзадач
     */
    private Task.TASK_STATUS identifyEpicStatus(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            if (epic.getSubtasks().isEmpty()) {
                return Task.TASK_STATUS.NEW;
            }
            boolean statusNew = false;
            boolean statusDone = false;
            boolean statusInProgress = false;
            List<Subtask> subtasks = this.getSubtasks(epic);
            for (Subtask subtask : subtasks) {
                if (subtask.getStatus() == Task.TASK_STATUS.NEW) {
                    statusNew = true;
                } else if (subtask.getStatus() == Task.TASK_STATUS.DONE) {
                    statusDone = true;
                } else if (subtask.getStatus() == Task.TASK_STATUS.IN_PROGRESS) {
                    statusInProgress = true;
                }
            }

            if (statusDone == true && statusNew == false && statusInProgress == false) {
                return Task.TASK_STATUS.DONE;
            } else if (statusDone = false && statusNew == true && statusInProgress == false) {
                return Task.TASK_STATUS.NEW;
            } else {
                return Task.TASK_STATUS.IN_PROGRESS;
            }

        } else {
            System.out.println("Epic == null или нет эпика с ID = " + epic.getId());
        }
        return Task.TASK_STATUS.NEW;
    }

    private int getTaskId() {
        return ++lastId;
    }

    private void deleteAllEpicSubtasks(Epic epic) {
        List<Subtask> subtasks = this.getSubtasks(epic);
        for (Subtask subtask : subtasks) {
            subTasks.remove(subtask);
            taskIds.remove(subtask.getId());
        }
    }

}
