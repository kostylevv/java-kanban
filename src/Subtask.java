public class Subtask extends Task {

    private long idEpic;
    
    public Subtask(long id, String title, String description, TASK_STATUS status, long idEpic) {
        super(id, title, description, status);
        this.idEpic = idEpic;
    }

    public long getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(long idEpic) {
        this.idEpic = idEpic;
    }
}
