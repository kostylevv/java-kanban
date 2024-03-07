package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    protected int id;
    protected String title;
    protected String description;
    protected LocalDateTime startTime;
    protected Duration duration;

    protected TaskStatusEnum status;
    protected final TaskTypeEnum type;

    public Task(String title, String description, TaskStatusEnum status, TaskTypeEnum type) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public Task(String title, String description, TaskStatusEnum status) {
        this(title, description, status, TaskTypeEnum.TASK);
    }

    public Task(String title, String description, TaskStatusEnum status, String startTimeString, long durationMinutes) {
        this(title, description, status, TaskTypeEnum.TASK);
        try {
            LocalDateTime startTime = getTimeFromString(startTimeString);
            this.startTime = startTime;
        } catch (IllegalArgumentException iae) {
            //log parsing error
        }
        Duration duration = getDurationFromMinutes(durationMinutes);
        this.duration = duration;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ID: ");
        stringBuilder.append(id);
        stringBuilder.append(", ");

        stringBuilder.append("title: ");
        stringBuilder.append(title);
        stringBuilder.append(", ");

        stringBuilder.append("desc: ");
        stringBuilder.append(description);
        stringBuilder.append(", ");

        stringBuilder.append("status: ");
        stringBuilder.append(status);

        if (duration != null) {
            stringBuilder.append(", ");
            stringBuilder.append("duration: ");
            stringBuilder.append(duration.toMinutes());
        }

        if (startTime != null) {
            stringBuilder.append(", ");
            stringBuilder.append("start time: ");
            stringBuilder.append(startTime.format(DATE_TIME_FORMATTER));
        }

        return stringBuilder.toString();
    }

    public String serialize() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id);
        stringBuilder.append(",");

        stringBuilder.append(title);
        stringBuilder.append(",");

        stringBuilder.append(description);
        stringBuilder.append(",");

        stringBuilder.append(status);
        stringBuilder.append(",");

        if (duration != null) {
            stringBuilder.append(duration.toMinutes());
        }
        stringBuilder.append(",");

        if (startTime != null) {
            stringBuilder.append(startTime.format(DATE_TIME_FORMATTER));
        }
        stringBuilder.append(",");

        return stringBuilder.toString();
    }

    public static Task fromString(String str) throws IllegalArgumentException {
        if (str != null && !str.isEmpty()) {
            String[] words = str.split(",");
            if (words.length == 7) {
                int id = getIdFromString(words[0]);

                TaskTypeEnum type = getTaskTypeFromString(words[1]);
                if (!type.equals(TaskTypeEnum.TASK)) {
                    throw new IllegalArgumentException("Task should have appropriate type, got " + words[1] + " (raw)");
                }

                TaskStatusEnum status = getTaskStatusFromString(words[2]);
                Task result = new Task(words[3], words[4], status);
                result.id = id;
                return result;
            } else {
                throw new IllegalArgumentException("Input string should have exactly 7 C-S-V, got " + words.length);
            }
        } else throw new IllegalArgumentException("Input string is null or empty");
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

    public void setStatus(TaskStatusEnum status) {
        this.status = status;
    }

    public TaskTypeEnum getType() {
        return type;
    }

    protected static int getIdFromString(String s) throws IllegalArgumentException {
        int id;
        try {
            id = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Task id should be an int type, got " + s);
        }
        return id;
    }

    protected static TaskTypeEnum getTaskTypeFromString(String s) throws IllegalArgumentException {
        return switch (s.trim().toUpperCase()) {
            case "TASK" -> TaskTypeEnum.TASK;
            case "SUBTASK" -> TaskTypeEnum.SUBTASK;
            case "EPIC" -> TaskTypeEnum.EPIC;
            default -> throw new IllegalArgumentException("Type should be TASK|SUBTASK|EPIC, got "
                    + s.trim().toUpperCase());
        };
    }

    protected static TaskStatusEnum getTaskStatusFromString(String s) {
        return switch (s.trim().toUpperCase()) {
            case "NEW" -> TaskStatusEnum.NEW;
            case "IN_PROGRESS" -> TaskStatusEnum.IN_PROGRESS;
            case "DONE" -> TaskStatusEnum.DONE;
            default -> throw new IllegalArgumentException("Task status should be NEW | IN PROGRESS | DONE, got "
                    + s.trim().toUpperCase());
        };
    }

    protected static Duration getDurationFromMinutes(long minutes) {
        return Duration.ofMinutes(minutes);
    }

    protected static LocalDateTime getTimeFromString(String startTime) throws IllegalArgumentException {
        return LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
    }
}
