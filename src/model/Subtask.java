package model;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(TaskStatusEnum status, String title, String description, int idEpic) {
        super(TaskTypeEnum.SUBTASK, status, title, description);
        setIdEpic(idEpic);
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
                result.setStartTime(words[5]);
                result.setDuration(words[6]);


                return result;
            } else {
                throw new IllegalArgumentException("Input string should have exactly 8 C-S-V, got " + words.length);
            }
        } else throw new IllegalArgumentException("Input string is null or empty");
    }
}