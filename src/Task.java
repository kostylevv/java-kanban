public class Task {

    protected int id;
    protected String title;
    protected String description;
    protected TASK_STATUS status;
    protected final TASK_TYPE type;

    public Task(String title, String description, TASK_STATUS status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = TASK_TYPE.TASK;
    }

    //конструктор для наследников
    public Task(String title, String description, TASK_STATUS status, TASK_TYPE type) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", title: " + title + ", desc: " + description + ", status: " + status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    protected void setStatus(TASK_STATUS status) {
        this.status = status;
    }

    public TASK_TYPE getType() {
        return type;
    }
}
