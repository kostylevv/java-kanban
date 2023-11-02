public class Task {

    protected int id;
    protected String title;
    protected String description;
    protected TaskStatusEnum status;
    protected final TaskTypeEnum type;

    public Task(String title, String description, TaskStatusEnum status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = TaskTypeEnum.TASK;
    }

    //конструктор для наследников
    public Task(String title, String description, TaskStatusEnum status, TaskTypeEnum type) {
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

    public TaskStatusEnum getStatus() {
        return status;
    }

    protected void setStatus(TaskStatusEnum status) {
        this.status = status;
    }

    public TaskTypeEnum getType() {
        return type;
    }
}
