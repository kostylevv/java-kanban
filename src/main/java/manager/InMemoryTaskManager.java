package manager;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subTasks;
    protected final Map<Integer, Epic> epics;
    private int lastId;
    protected final HistoryManager historyManager;
    private static TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        lastId = 0;
        this.historyManager = historyManager;

        prioritizedTasks = new TreeSet<>(
                (t1, t2) -> {
                    if (t1.getStartTime().isPresent() && t2.getStartTime().isPresent()) {
                        return t1.getStartTime().get().compareTo(t2.getStartTime().get());
                    }
                    return 0;
                });
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /*
    Получение списков всех задач
     */
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /*
    Получение задач по идентификатору
     */
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubTaskById(int id) {
        Subtask subtask = subTasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    /*
    Удаление по идентификатору
     */
    @Override
    public void deleteTaskById(int id) {
        Task task = getTaskById(id);
        tasks.remove(id);
        historyManager.remove(id);
        deleteWithPriority(task);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subTasks.get(id);
        Epic epic = epics.get(subtask.getIdEpic());
        subTasks.remove(id);
        epic.deleteSubtask(id);
        updateEpicStatus(epic);
        historyManager.remove(id);
        deleteWithPriority(subtask);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        deleteAllEpicSubtasks(epic);
        epics.remove(id);
        historyManager.remove(id);
    }

    /*
    Получение подзадач эпика
     */
    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            return subTasks.entrySet().stream()
                    .filter(e -> epic.getSubtasks().contains(e.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

        } else {
            System.out.println("model.Epic == null или нет такого эпика");
            return new ArrayList<>();
        }
    }

    /*
    Удаление всех задач
     */
    @Override
    public void deleteAllTasks() {
        for (Task task : getAllTasks()) {
            historyManager.remove(task.getId());
            deleteWithPriority(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Subtask task : getAllSubtasks()) {
            historyManager.remove(task.getId());
            deleteWithPriority(task);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubTasks();
        for (Epic epic : getAllEpics()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    /*
    Обновление задач
     */
    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {

            boolean overlapTask = prioritizedTasks.stream()
                    .filter(t -> t.getId() != task.getId())
            .anyMatch(t -> isTasksOverlap(t, task));

            if (!overlapTask) {
                deleteWithPriority(task);
                tasks.put(task.getId(), task);
                addWithPriority(task);
            } else {
                throw new IllegalArgumentException("Невозможно обновить задачу, она пересекается с уже существующей");
            }
        } else {
            System.out.println("model.Task == null или задачи не существует");
        }
    }

    @Override
    public void updateSubtask(Subtask task) {
        if (task != null && subTasks.containsKey(task.getId())) {

            boolean overlapTask = prioritizedTasks.stream()
                            .filter(t -> t.getId() != task.getId())
                    .anyMatch(t -> isTasksOverlap(t, task));

            if (!overlapTask) {
                deleteWithPriority(task);
                subTasks.put(task.getId(), task);
                Epic epic = getEpicById(task.getIdEpic());
                updateEpicStatus(epic);
                addWithPriority(task);
            } else {
                throw new IllegalArgumentException("Невозможно обновить подзадачу, она пересекается с уже существующей");
            }
        } else {
            System.out.println("model.Task == null или задачи не существует");
        }
    }

    @Override
    public void updateEpic(Epic task) {
        if (task != null && epics.containsKey(task.getId())) {
            epics.put(task.getId(), task);
            updateEpicStatus(task);
        } else {
            System.out.println("model.Task == null или задачи не существует");
        }
    }

    /*
    Добавление задач
     */
    @Override
    public void addTask(Task task) {
        if (task != null && task.getType() == TaskTypeEnum.TASK) {
            boolean overlapTask = prioritizedTasks.stream().anyMatch(t -> isTasksOverlap(t, task));

            if (!overlapTask) {
                task.setId(getTaskId());
                tasks.put(task.getId(), task);
                addWithPriority(task);
            } else {
                throw new IllegalArgumentException("Невозможно обновить пзадачу, она пересекается с уже существующей");
            }
        } else {
            System.out.println("model.Task == null или неверный тип задачи");
        }
    }

    @Override
    public void addSubtask(Subtask task) {
        if (task != null && task.getType() == TaskTypeEnum.SUBTASK) {
            if (epics.containsKey(task.getIdEpic())) {
                boolean overlapTask = prioritizedTasks.stream().anyMatch(t -> isTasksOverlap(t, task));

                if (!overlapTask) {
                    task.setId(getTaskId());
                    subTasks.put(task.getId(), task);
                    Epic epic = epics.get(task.getIdEpic());
                    epic.addSubtask(task.getId());
                    updateEpicStatus(epic);
                    addWithPriority(task);
                } else {
                    System.out.println("Задача " + task + " пересекается с уже существующей задачей. Невозможно добавить");
                }
            } else {
                System.out.println("Невозможно добавить подзадачу для несуществующего " +
                        "эпика с ID = " + task.getIdEpic());
            }
        } else {
            System.out.println("model.Task == null или неверный тип задачи");
        }
    }

    @Override
    public void addEpic(Epic task) {
        if (task != null && task.getType() == TaskTypeEnum.EPIC) {
            if (subTasks.keySet().containsAll(task.getSubtasks())) {
                task.setId(getTaskId());
                epics.put(task.getId(), task);
                updateEpicStatus(task);
            } else {
                System.out.println("Невозможно добавить эпик c несуществующими подзадачами");
            }
        } else {
            System.out.println("model.Task == null или неверный тип задачи");
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        System.out.println(prioritizedTasks.size());
        return prioritizedTasks.stream().toList();
    }

    private boolean isTasksOverlap(Task t1, Task t2) {
        if (t1.getStartTime().isEmpty() || t2.getStartTime().isEmpty() ||
                t1.getDuration().isEmpty() || t2.getDuration().isEmpty()) {
            return false;
        }

        if (t1.getStartTime().get().isEqual(t2.getStartTime().get())) {
            return true;
        }

        if (t1.getStartTime().get().isBefore(t2.getStartTime().get())) {
            return t1.getEndTime().get().isAfter(t2.getStartTime().get());
        } else {
            return t2.getEndTime().get().isAfter(t1.getStartTime().get());
        }
    }

    /*
    Метод следит за статусом эпика исходя из статусов подзадач
     */
    private void updateEpicStatus(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            List<Subtask> subtasks = getSubtasks(epic);
            setEpicTimeBound(epic);

            if (subtasks.isEmpty()) {
                epic.setStatus(TaskStatusEnum.NEW);
            } else {
                boolean statusNew = false;
                boolean statusDone = false;
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

                if (statusDone && !statusNew) {
                    epic.setStatus(TaskStatusEnum.DONE);
                } else if (!statusDone && statusNew) {
                    epic.setStatus(TaskStatusEnum.NEW);
                } else {
                    epic.setStatus(TaskStatusEnum.IN_PROGRESS);
                }

            }
        } else {
            System.out.println("model.Epic == null или нет такого эпика");
        }
    }

    private void setEpicTimeBound(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {

            epic.setStartTime(getSubtasks(epic).stream()
                    .map(Subtask::getStartTime)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .min(LocalDateTime::compareTo));

            epic.setEndTime(getSubtasks(epic).stream()
                    .map(Subtask::getEndTime)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .max(LocalDateTime::compareTo)
            );

            epic.setDuration(getSubtasks(epic).stream()
                    .map(Subtask::getDuration)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(Duration.ZERO, Duration::plus));
        }
    }

    private int getTaskId() {
        return ++lastId;
    }

    private void deleteAllEpicSubtasks(Epic epic) {
        for (int id : epic.getSubtasks()) {
            subTasks.remove(id);
            historyManager.remove(id);
        }
    }

    private static void addWithPriority(Task task) {
        if (task.getStartTime().isPresent()) {
            prioritizedTasks.add(task);
        }
    }

    private static void deleteWithPriority(Task task) {
        prioritizedTasks.remove(task);
    }
}
