package manager;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(getDefaultHistory(), "C:\\Users\\kosty\\Desktop\\java-kanbanf\\out\\save.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}