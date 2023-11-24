import java.util.*;

public interface TaskManager {
    public List<Task> getHistory();

    /*
    Получение списков всех задач
     */
    public List<Task> getAllTasks();

    public List<Subtask> getAllSubtasks();

    public List<Epic> getAllEpics();

    /*
    Получение задач по идентификатору
     */
    public Task getTaskById(int id);

    public Subtask getSubTaskById(int id);

    public Epic getEpicById(int id);

    /*
    Удаление по идентификатору
     */
    public void deleteTaskById(int id);

    public void deleteSubtaskById(int id);

    public void deleteEpicById(int id);

    /*
    Получение подзадач эпика
     */
    public List<Subtask> getSubtasks(Epic epic);

    /*
    Удаление всех задач
     */
    public void deleteAllTasks();

    public void deleteAllSubTasks();

    public void deleteAllEpics();

    /*
    Обновление задач
     */

    public void updateTask(Task task);

    public void updateSubtask(Subtask task);

    public void updateEpic(Epic task);

    /*
    Добавление задач
     */

    public void addTask(Task task);

    public void addSubtask(Subtask task);

    public void addEpic(Epic task);
}
