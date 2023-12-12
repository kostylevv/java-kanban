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
}
