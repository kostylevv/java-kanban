package manager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        File save = new File("./out/save.csv");
        return new FileBackedTasksManager(getDefaultHistory(), save.getAbsolutePath());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
