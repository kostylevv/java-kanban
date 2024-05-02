package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.*;

public interface TaskManager {
    /*
    Getting all tasks
     */
    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    /*
    Получение задач по идентификатору
     */


    Task getTaskById(int id);

    Subtask getSubTaskById(int id);

    Epic getEpicById(int id);

    /*
    Удаление по идентификатору
     */
    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    /*
    Получение подзадач эпика
     */
    List<Subtask> getSubtasks(Epic epic);

    /*
    Удаление всех задач
     */
    void deleteAllTasks();

    void deleteAllSubTasks();

    void deleteAllEpics();

    /*
    Обновление задач
     */
    void updateTask(Task task);

    void updateSubtask(Subtask task);

    void updateEpic(Epic task);

    /*
    Добавление задач
     */
    void addTask(Task task);

    void addSubtask(Subtask task);

    void addEpic(Epic task);

    /*
    Получение истории просмотров задач
     */
    List<Task> getHistory();

    /*
    Получение задач в порядке приоритета (по времени начала)
     */
    List<Task> getPrioritizedTasks();
}
