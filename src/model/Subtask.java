package model;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String title, String description, TaskStatusEnum status, int idEpic) {
        super(title, description, status, TaskTypeEnum.SUBTASK);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return super.toString() + ", idEpic = " + idEpic;
    }
}
