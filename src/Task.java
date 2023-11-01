public class Task {
    public enum TASK_STATUS {NEW, DONE, IN_PROGRESS}
    public enum TASK_TYPE {TASK, SUBTASK, EPIC} //для упрощения определения типа задачи

    private int id;
    private String title;
    private String description;
    private TASK_STATUS status;
    private final TASK_TYPE type;

    public Task(String title, String description, TASK_STATUS status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = TASK_TYPE.TASK;
        this.id = -1;               //присваивается таск менеджером
    }

    //конструктор для наследников
    public Task(String title, String description, TASK_STATUS status, TASK_TYPE type) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = -1;
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
