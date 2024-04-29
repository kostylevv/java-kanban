package web.handler;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;
import model.TaskStatusEnum;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        builder.registerTypeAdapter(Duration.class, new DurationAdapter());

        gson = builder.create();

        /*
        For testing purposes
         */
        Task task = new Task(TaskStatusEnum.NEW, "test title task1", "test desc 1");
        Task task1 = new Task(TaskStatusEnum.IN_PROGRESS, "test title taks2", "test desc2 ");
        manager.addTask(task);
        manager.addTask(task1);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS -> getAllTasks(exchange);
            case GET_TASK -> getTask(exchange, exchange.getRequestURI().getPath());
            case CREATE_TASK -> sendOkWithReply(exchange, Endpoint.CREATE_TASK.name() + " invoked");
            case UPDATE_TASK -> sendOkWithReply(exchange, Endpoint.UPDATE_TASK.name() + " invoked");
            case DELETE_TASK -> sendOkNoReply(exchange, Endpoint.DELETE_TASK.name() + " invoked");
            case UNKNOWN -> sendNotFound(exchange, Endpoint.UNKNOWN.name() + " invoked");
            default -> new UnsupportedOperationException("Unsupported endpoint provided");
        }

    }

    /**
     * Get all tasks end output them as JSON
     *
     * @param httpExchange
     */
    private void getAllTasks(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        String tasksJson = gson.toJson(tasks);
        System.out.println(tasksJson);
        sendOkWithReply(httpExchange, tasksJson);
    }

    private void getTask(HttpExchange httpExchange, String requestPath) throws ArithmeticException, IOException {
        if (requestPath != null) {
            String[] words = requestPath.split("/");
            if (words.length == 3 && words[1].equals("tasks")) {
                Integer taskId = Integer.parseInt(words[2]);
                Task task = manager.getTaskById(taskId);
                if (task != null) {
                    String taskJson = gson.toJson(task, Task.class);
                    sendOkWithReply(httpExchange, taskJson);
                } else {
                    sendNotFound(httpExchange, "Task with id = " + taskId + " not found");
                }
            }
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath == null || requestPath.isEmpty() || requestMethod == null || requestMethod.isEmpty()) {
            throw new IllegalArgumentException("requestPath: " + requestPath + ", requestMethod:" + requestMethod);
        }

        String[] words = requestPath.split("/");

        if (words.length == 2 && words[1].equals("tasks")) {
            return switch (requestMethod) {
                case "GET" -> Endpoint.GET_TASKS;       //GET   /TASKS
                case "POST" -> Endpoint.CREATE_TASK;    //POST  /TASKS
                default -> Endpoint.UNKNOWN;
            };
        }

        if (words.length == 3 && words[1].equals("tasks")) {
            return switch (requestMethod) {
                case "GET" -> Endpoint.GET_TASK;        //GET     /TASKS/{id}
                case "POST" -> Endpoint.UPDATE_TASK;    //POST    /TASKS/{id}
                case "DELETE" -> Endpoint.DELETE_TASK;  //DELETE  /TASKS/{id}
                default -> Endpoint.UNKNOWN;
            };
        }
        return Endpoint.UNKNOWN;
    }
}

class TaskListTypeToken extends TypeToken<List<Task>> {
}

class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String timeString = reader.nextString();
        return LocalDateTime.parse(timeString, timeFormatter);
    }

    @Override
    public void write(JsonWriter writer, LocalDateTime time) throws IOException {
        if (time == null) {
            writer.nullValue();
            return;
        }
        writer.value(time.format(timeFormatter));
    }
}

class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public Duration read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String durationString = reader.nextString();
        try {
            long durationLong = Long.parseLong(durationString);
            Duration duration = Duration.ofMinutes(durationLong);
            return duration;
        } catch (Exception e) {
            //pass
        }
        return null;
    }

    @Override
    public void write(JsonWriter writer, Duration duration) throws IOException {
        if (duration == null) {
            writer.nullValue();
            return;
        }
        writer.value(duration.toMinutes());
    }
}


