package model;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String title, String description, TaskStatusEnum status, int idEpic) {
        super(title, description, status, TaskTypeEnum.SUBTASK);
        this.idEpic = idEpic;
    }

    public Subtask(int id, String title, String description, TaskStatusEnum status, int idEpic) {
        this(title, description, status, idEpic);
        this.id = id;
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

    @Override
    public String serialize() {
        return super.serialize() + "," + idEpic;
    }

    public static Subtask fromString(String str) throws IllegalArgumentException {
        if (str != null && !str.isEmpty()) {
            String[] words = str.split(",");
            if (words.length == 6) {
                int id = getIdFromString(words[0]);

                TaskTypeEnum type = getTaskTypeFromString(words[1]);
                if (!type.equals(TaskTypeEnum.SUBTASK)) {
                    throw new IllegalArgumentException("Subtask should have appropriate type, got " + words[1] + " (raw)");
                }

                TaskStatusEnum status = getTaskStatusFromString(words[2]);
                int idEpic = getIdFromString(words[5]);
                return new Subtask(id, words[3], words[4], status, idEpic);
            } else {
                throw new IllegalArgumentException("Input string should have exactly 6 C-S-V, got " + words.length);
            }
        } else throw new IllegalArgumentException("Input string is null or empty");
    }
}
