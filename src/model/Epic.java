package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {

    private Set<Integer> subtasks; //эпик знает ID подзадач, но не хранит их

    public Epic(String title, String description) {
        super(title, description, TaskStatusEnum.NEW, TaskTypeEnum.EPIC);
        this.subtasks = new HashSet<>();
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

    public void clearSubtasks() {subtasks.clear();}

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

    public static Epic fromString(String str) throws IllegalArgumentException {
        if (str != null && !str.isEmpty()) {
            String[] words = str.split(",");
            if (words.length >= 6) {
                int id = getIdFromString(words[0]);

                TaskTypeEnum type = getTaskTypeFromString(words[1]);
                if (!type.equals(TaskTypeEnum.EPIC)) {
                    throw new IllegalArgumentException("Epic should have appropriate type, got " + words[1] + " (raw)");
                }

                TaskStatusEnum status = getTaskStatusFromString(words[2]);

                Epic result = new Epic(words[3], words[4]);
                result.id = id;
                result.status = status;
                result.subtasks = getSubtasksFromStringArr(Arrays.copyOfRange(words, 5, words.length));
                return result;
            } else {
                throw new IllegalArgumentException("Input string should have >= 6 C-S-V, got " + words.length);
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
