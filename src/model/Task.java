package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Task {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    protected int id;
    protected final TaskTypeEnum type;
    protected TaskStatusEnum status;
    protected String title;
    protected String description;
    protected LocalDateTime startTime;
    protected Duration duration;

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plusMinutes(duration.toMinutes());
        } else return null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTimeString) {
        try {
            LocalDateTime startTime = getTimeFromString(startTimeString);
            this.startTime = startTime;
        } catch (DateTimeParseException e) {
            //log exception
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(long durationLong) {
        try {
            Duration duration = Duration.ofMinutes(durationLong);
            this.duration = duration;
        } catch (ArithmeticException e) {
            //log exception
        }
    }

    public void setDuration(String durationString) {
        try {
            long durationLong = Long.parseLong(durationString);
            setDuration(durationLong);
        } catch (NumberFormatException e) {
            //log exception
        }
    }



    public Task(TaskTypeEnum type, TaskStatusEnum status, String title, String description) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public Task(TaskStatusEnum status, String title, String description) {
        this(TaskTypeEnum.TASK, status, title, description);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ID: ");
        stringBuilder.append(id);
        stringBuilder.append(", ");

        stringBuilder.append("type: ");
        stringBuilder.append(type);
        stringBuilder.append(", ");

        stringBuilder.append("status: ");
        stringBuilder.append(status);
        stringBuilder.append(", ");

        stringBuilder.append("title: ");
        stringBuilder.append(title);
        stringBuilder.append(", ");

        stringBuilder.append("desc: ");
        stringBuilder.append(description);
        stringBuilder.append(", ");

        stringBuilder.append("start: ");
        if (startTime != null) {
            stringBuilder.append(startTime.format(DATE_TIME_FORMATTER));
        }
        stringBuilder.append(",");

        stringBuilder.append("duration: ");
        if (duration != null) {
            stringBuilder.append(duration.toMinutes());
        }

        return stringBuilder.toString();
    }

    public String serialize() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id);
        stringBuilder.append(",");

        stringBuilder.append(type);
        stringBuilder.append(",");

        stringBuilder.append(status);
        stringBuilder.append(",");

        stringBuilder.append(title);
        stringBuilder.append(",");

        stringBuilder.append(description);
        stringBuilder.append(",");

        if (startTime != null) {
            stringBuilder.append(startTime.format(DATE_TIME_FORMATTER));
        }
        stringBuilder.append(",");

        if (duration != null) {
            stringBuilder.append(duration.toMinutes());
        }

        return stringBuilder.toString();
    }

    /**
     *
     * id,type,status,title,description,startTime,duration
     * 0, 1,   2,     3,    4,          5,        6
     *
     * @param str
     * @return
     * @throws IllegalArgumentException
     */

    public static Task fromString(String str) throws IllegalArgumentException {
        if (str != null && !str.isEmpty()) {
            String[] words = str.split(",", -1);
            if (words.length == 7) {
                int id = getIdFromString(words[0]);

                TaskTypeEnum type = getTaskTypeFromString(words[1]);
                if (!type.equals(TaskTypeEnum.TASK)) {
                    throw new IllegalArgumentException("Task should have appropriate type, got " + words[1] + " (raw)");
                }

                TaskStatusEnum status = getTaskStatusFromString(words[2]);
                Task result = new Task(status, words[3], words[4]);
                result.id = id;
                result.setStartTime(words[5]);

                result.setDuration(words[6]);

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

    protected static LocalDateTime getTimeFromString(String startTime) throws DateTimeParseException {
        return LocalDateTime.parse(startTime, DATE_TIME_FORMATTER);
    }
}