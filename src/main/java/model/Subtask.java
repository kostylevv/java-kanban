package model;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(TaskStatusEnum status, String title, String description, int idEpic) {
        super(TaskTypeEnum.SUBTASK, status, title, description);
        setIdEpic(idEpic);
    }

    public Subtask(String title, String description, int idEpic) {
        super(TaskTypeEnum.SUBTASK, TaskStatusEnum.NEW, title, description);
        setIdEpic(idEpic);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Subtask oTask))
            return false;

        if (oTask.id != this.id || !oTask.title.equals(this.title) || !oTask.description.equals(this.description)
                || !oTask.status.equals(this.status) || !oTask.type.equals(this.type) || oTask.idEpic != this.idEpic) {
            return false;
        }

        if (this.startTime != null) {
            if (oTask.startTime != null && !oTask.startTime.equals(this.startTime)) {
                return false;
            }
        } else {
            if (oTask.startTime != null) {
                return false;
            }
        }

        if (this.duration != null) {
            return oTask.duration == null || this.duration.equals(oTask.duration);
        } else {
            return oTask.duration == null;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 * idEpic;
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
        StringBuilder sb = new StringBuilder();
        sb.append(super.serialize());
        sb.append(",");
        sb.append(idEpic);
        return sb.toString();
    }


    // id,type,status,title,description,startTime,duration,idEpic
    //  0, 1,   2,     3,    4,          5,        6,       7
    public static Subtask fromString(String str) throws IllegalArgumentException {
        if (str != null && !str.isEmpty()) {
            String[] words = str.split(",", -1);
            if (words.length == 8) {
                int id = getIdFromString(words[0]);

                TaskTypeEnum type = getTaskTypeFromString(words[1]);
                if (!type.equals(TaskTypeEnum.SUBTASK)) {
                    throw new IllegalArgumentException("Subtask should have appropriate type, got " + words[1] + " (raw)");
                }

                TaskStatusEnum status = getTaskStatusFromString(words[2]);
                int idEpic = getIdFromString(words[7]);
                Subtask result = new Subtask(status, words[3], words[4], idEpic);
                result.id = id;
                result.setIdEpic(idEpic);

                if (!words[5].isBlank()) {
                    result.setStartTime(words[5]);
                }

                if (!words[6].isBlank()) {
                    result.setDuration(words[6]);
                }

                return result;
            } else {
                throw new IllegalArgumentException("Input string should have exactly 8 C-S-V, got " + words.length);
            }
        } else throw new IllegalArgumentException("Input string is null or empty");
    }
}