public class Task {

    public enum TASK_STATUS {NEW, DONE, IN_PROGRESS}

    protected long id;
    protected String title;
    protected String description;
    protected TASK_STATUS status;

    public Task(long id, String title, String description, TASK_STATUS status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TASK_STATUS getStatus() {
        return status;
    }

    public void setStatus(TASK_STATUS status) {
        this.status = status;
    }
}
