import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> viewedTasks; //хранит просмотренные задачи
    private static final int HISTORY_CAPACITY = 10;

    public InMemoryHistoryManager() {
        viewedTasks = new ArrayList<>(HISTORY_CAPACITY);
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
        if (viewedTasks.size() == HISTORY_CAPACITY) {
            //shift to the left
            viewedTasks.remove(0);
        }

        viewedTasks.add(task);
    }
}
