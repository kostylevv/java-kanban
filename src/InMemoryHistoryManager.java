import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> viewedTasks; //хранит просмотренные задачи
    private int tasksViewedCount; // сколько задач записано в историю просмотров
    private static final int HISTORY_CAPACITY = 10;

    public InMemoryHistoryManager() {
        viewedTasks = new ArrayList<>(HISTORY_CAPACITY);
        tasksViewedCount = 0;
    }

    /*
    Возвращает последние HISTORY_CAPACITY просмотренных задач
     */
    @Override
    public List<Task> getHistory() {
        return viewedTasks;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (tasksViewedCount == HISTORY_CAPACITY) {
                //shift
                List<Task> newHistory = new ArrayList<>();

                for (int i = 0; i < HISTORY_CAPACITY - 1; i++) {
                    newHistory.add(i, viewedTasks.get(i + 1));
                }
                newHistory.add(task);
                viewedTasks = newHistory;
            } else {
                viewedTasks.add(task);
                tasksViewedCount++;
            }
        }
    }
}
