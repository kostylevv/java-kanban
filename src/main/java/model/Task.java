package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class Task {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    protected int id;
    protected final TaskTypeEnum type;
    protected TaskStatusEnum status;
    protected String title;
    protected String description;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(TaskTypeEnum type, TaskStatusEnum status, String title, String description) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = type;
    }

    public Task(TaskStatusEnum status, String title, String description) {
        this(TaskTypeEnum.TASK, status, title, description);
    }

    public Task(String title, String description) {
        this(TaskTypeEnum.TASK, TaskStatusEnum.NEW, title, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Task))
            return false;

        Task oTask = (Task) o;
        boolean check = oTask.id == this.id && oTask.title.equals(this.title) && oTask.description.equals(this.description)
                && oTask.status.equals(this.status) && oTask.type.equals(this.type);
        if (!check) {
            return false;
        } else {
            if (this.startTime != null) {
                if (oTask.startTime != null && oTask.startTime.equals(this.startTime))
                    return false;
            } else {
                if (oTask.startTime != null)
                    return false;
            }
            if (this.duration != null) {
                if (oTask.duration != null && this.duration.equals(oTask.duration))
                    return false;
            } else {
                if (oTask.duration != null)
                    return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (getStartTime().isPresent() && getDuration().isPresent()) {
            return Optional.of(startTime.plusMinutes(duration.toMinutes()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setStartTime(Optional<LocalDateTime> startTime) {
        if (startTime.isPresent()) {
            this.startTime = startTime.get();
        } else {
            this.startTime = null;
        }
    }

    public void setStartTime(String startTimeString) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeString.trim(), DATE_TIME_FORMATTER);
            this.setStartTime(Optional.ofNullable(startTime));
        } catch (DateTimeParseException e) {
            //log exception
            e.printStackTrace();
        }
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public void setDuration(Duration duration) {
        if (duration != null) {
            this.duration = duration;
        } else {
            this.duration = null;
        }
    }

    public void setDuration(long durationLong) {
        try {
            Duration duration = Duration.ofMinutes(durationLong);
            setDuration(duration);
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

        stringBuilder.append("Type: ");
        stringBuilder.append(type);
        stringBuilder.append(", ");

        stringBuilder.append("title: ");
        stringBuilder.append(title);
        stringBuilder.append(", ");

        stringBuilder.append("desc: ");
        stringBuilder.append(description);
        stringBuilder.append(", ");

        stringBuilder.append("start: ");
        if (getStartTime().isPresent()) {
            stringBuilder.append(startTime.format(DATE_TIME_FORMATTER));
        }
        stringBuilder.append(",");

        stringBuilder.append("duration: ");
        if (getDuration().isPresent()) {
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
        stringBuilder.append(" ,");

        if (getStartTime().isPresent()) {
            stringBuilder.append(startTime.format(DATE_TIME_FORMATTER));
        }
        stringBuilder.append(" ,");

        if (getDuration().isPresent()) {
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
                if (!words[5].isBlank()) {
                    result.setStartTime(words[5]);
                }

                if (!words[6].isBlank()) {
                    result.setDuration(words[6]);
                }

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
}