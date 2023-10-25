import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {

    private Set<Long> subtasks;

    public Epic(long id, String title, String description, TASK_STATUS status, Set<Long> subtasks) {
        super(id, title, description, status);
        this.subtasks = subtasks;
    }

    public Epic(long id, String title, String description, TASK_STATUS status) {
        super(id, title, description, status);
        this.subtasks = new HashSet<>();
    }

    public Set<Long> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Set<Long> subtasks) {
        this.subtasks = subtasks;
    }
}
