package web.handler.taskhandler;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import manager.exception.NotFoundException;
import manager.exception.OverlapException;
import model.Task;
import web.handler.BaseHttpHandler;
import web.handler.Endpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        builder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = builder.create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS -> getAllTasks(exchange);
            case GET_TASK -> getTask(exchange, exchange.getRequestURI().getPath());
            case ADD_TASK -> addTask(exchange);
            case UPDATE_TASK -> updateTask(exchange);
            case DELETE_TASK -> deleteTask(exchange, exchange.getRequestURI().getPath());
            case UNKNOWN -> sendNotFound(exchange, Endpoint.UNKNOWN.name() + " invoked");
        }

    }

    /**
     * Get all tasks end output them as JSON
     *
     * @param httpExchange exchange object
     */
    private void getAllTasks(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        String tasksJson = gson.toJson(tasks);
        sendOkWithReply(httpExchange, tasksJson);
    }

    /**
     * Get task by provided ID
     *
     * @param httpExchange exchange object
     * @param requestPath  path containing ID i.e. /tasks/{id}
     * @throws ArithmeticException if ID is not integer
     * @throws IOException         if sendNotFound method will throw this exception
     */
    private void getTask(HttpExchange httpExchange, String requestPath) throws ArithmeticException, IOException {
        String[] words = requestPath.split("/");
        if (words.length == 3 && words[1].equals("tasks")) {
            int taskId = Integer.parseInt(words[2]);
            try {
                Task task = manager.getTaskById(taskId);
                String taskJson = gson.toJson(task, Task.class);
                sendOkWithReply(httpExchange, taskJson);
            } catch (NotFoundException notFoundException) {
                sendNotFound(httpExchange, "Task wasn't found");
            }
        }
    }

    /**
     * Add task
     *
     * @param httpExchange exchange object with new Task in json-string in body
     * @throws IOException if send methods will throw this exception
     */
    private void addTask(HttpExchange httpExchange) throws IOException {
        String body = new BufferedReader(
                new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());
        try {
            Task task = gson.fromJson(body, Task.class);
            manager.addTask(task);
            sendOkNoReply(httpExchange, "added task " + task.toString());
        } catch (IllegalArgumentException iae) {
            sendHasOverlap(httpExchange, "task has overlap with existing task");
        } catch (Exception e) {
            sendError(httpExchange, e.getMessage());
        }
    }

    /**
     * Update task
     *
     * @param httpExchange exchange object with updated Task in json-string in body
     * @throws IOException if send methods will throw this exception
     */
    private void updateTask(HttpExchange httpExchange) throws IOException {
        String body = new BufferedReader(
                new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());
        try {
            Task task = gson.fromJson(body, Task.class);
            manager.updateTask(task);
            sendOkNoReply(httpExchange, "added task " + task.toString());
        } catch (OverlapException iae) {
            sendHasOverlap(httpExchange, "task has overlap with existing task");
        } catch (NotFoundException nfe) {
            sendError(httpExchange, "task wasn't found");
        } catch (Exception e) {
            sendError(httpExchange, e.getMessage());
        }
    }

    private void deleteTask(HttpExchange httpExchange, String requestPath) throws IOException {
        String[] words = requestPath.split("/");
        if (words.length == 3 && words[1].equals("tasks")) {
            int taskId = Integer.parseInt(words[2]);
            //@TODO arithmetic exception catch
            manager.deleteTaskById(taskId);
            sendOkWithReply(httpExchange, "Task deleted");
        }
    }

    /**
     * Parse request to identify appropriate endpoint
     *
     * @param requestPath   request path
     * @param requestMethod request method
     * @return appropriate Endpoint object including Endpoint.UNKNOWN
     */
    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath == null || requestPath.isEmpty() || requestMethod == null || requestMethod.isEmpty()) {
            throw new IllegalArgumentException("requestPath: " + requestPath + ", requestMethod:" + requestMethod);
        }

        String[] words = requestPath.split("/");

        if (words.length == 2 && words[1].equals("tasks")) {
            return switch (requestMethod) {
                case "GET" -> Endpoint.GET_TASKS;       //GET   /TASKS
                case "POST" -> Endpoint.ADD_TASK;    //POST  /TASKS
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