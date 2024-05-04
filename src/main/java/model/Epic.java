package model;

import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {

    private Set<Integer> subtasks; //эпик знает ID подзадач, но не хранит их
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(TaskTypeEnum.EPIC, TaskStatusEnum.NEW, title, description);
        this.subtasks = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Epic oTask))
            return false;

        if (oTask.id != this.id || !oTask.title.equals(this.title) || !oTask.description.equals(this.description)
                || !oTask.status.equals(this.status) || !oTask.type.equals(this.type)) {
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
            if (oTask.duration == null || !this.duration.equals(oTask.duration)) {
                return false;
            }
        } else {
            if (oTask.duration != null) {
                return false;
            }
        }

        return oTask.subtasks.equals(this.subtasks);
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i : subtasks) {
            result = result * 31 * i;
        }
        return super.hashCode() * result;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        if (endTime != null) {
            return Optional.of(endTime);
        } else {
            return Optional.empty();
        }
    }

    public void setEndTime(Optional<LocalDateTime> endTime) {
        if (endTime.isPresent()) {
            this.endTime = endTime.get();
        } else {
            this.endTime = null;
        }
    }

    public Set<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Set<Integer> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(int id) {
        subtasks.add(id);
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return super.toString() + ", subtasks = " + Arrays.asList(subtasks.toArray());
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder(super.serialize());
        for (int i : subtasks) {
            sb.append(",");
            sb.append(i);
        }
        return sb.toString();
    }

    // id,type,status,title,description,startTime,duration,idSubtask...
    //  0, 1,   2,     3,    4,          5,        6,       7
    public static Epic fromString(String str) throws IllegalArgumentException {
        if (str != null && !str.isEmpty()) {
            String[] words = str.split(",", -1);
            if (words.length >= 7) {
                int id = getIdFromString(words[0]);

                TaskTypeEnum type = getTaskTypeFromString(words[1]);
                if (!type.equals(TaskTypeEnum.EPIC)) {
                    throw new IllegalArgumentException("Epic should have appropriate type, got " + words[1] + " (raw)");
                }

                TaskStatusEnum status = getTaskStatusFromString(words[2]);

                Epic result = new Epic(words[3], words[4]);
                result.id = id;
                result.status = status;
                result.subtasks = getSubtasksFromStringArr(Arrays.copyOfRange(words, 7, words.length));

                return result;
            } else {
                throw new IllegalArgumentException("Input string should have >= 7 C-S-V, got " + words.length);
            }
        } else throw new IllegalArgumentException("Input string is null or empty");
    }

    private static Set<Integer> getSubtasksFromStringArr(String[] array) throws IllegalArgumentException {
        Set<Integer> result = new HashSet<>();
        for (String s : array) {
            try {
                result.add(Integer.parseInt(s));
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Subtask id should be of an int type, got " + s);
            }
        }
        return result;
    }
}
