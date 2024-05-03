package web.handler.taskhandler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exception.NotFoundException;
import manager.exception.OverlapException;
import model.Task;
import web.handler.BaseHttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        TaskEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS -> getAllTasks(exchange);
            case GET_TASK -> getTask(exchange, exchange.getRequestURI().getPath());
            case ADD_TASK -> addTask(exchange);
            case UPDATE_TASK -> updateTask(exchange);
            case DELETE_TASK -> deleteTask(exchange, exchange.getRequestURI().getPath());
            case UNKNOWN -> sendNotFound(exchange, TaskEndpoint.UNKNOWN.name() + " invoked");
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
    private void getTask(HttpExchange httpExchange, String requestPath) throws IOException {
        String[] words = requestPath.split("/");
        if (words.length == 3 && words[1].equals("tasks")) {
            try {
                int taskId = Integer.parseInt(words[2]);
                Task task = manager.getTaskById(taskId);
                String taskJson = gson.toJson(task, Task.class);
                sendOkWithReply(httpExchange, taskJson);
            } catch (NotFoundException notFoundException) {
                sendNotFound(httpExchange, "Task wasn't found");
            } catch (NumberFormatException numberFormatException) {
                sendError(httpExchange, words[2] + " is incorrect id");
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
        } catch (OverlapException iae) {
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

    /**
     * Delete task with provided ID
     *
     * @param httpExchange exchange object
     * @param requestPath path containing ID i.e. /tasks/{id}
     * @throws IOException if send methods will throw this exception
     */
    private void deleteTask(HttpExchange httpExchange, String requestPath) throws IOException {
        String[] words = requestPath.split("/");
        if (words.length == 3 && words[1].equals("tasks")) {
            try {
                int taskId = Integer.parseInt(words[2]);
                manager.deleteTaskById(taskId);
                sendOkWithReply(httpExchange, "Task deleted");
            } catch (NumberFormatException numberFormatException) {
                sendError(httpExchange, words[2] + " is incorrect id");
            }
        }
    }

    /**
     * Parse request to identify appropriate endpoint
     *
     * @param requestPath   request path
     * @param requestMethod request method
     * @return appropriate Endpoint object including Endpoint.UNKNOWN
     */
    private TaskEndpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath == null || requestPath.isEmpty() || requestMethod == null || requestMethod.isEmpty()) {
            throw new IllegalArgumentException("requestPath: " + requestPath + ", requestMethod:" + requestMethod);
        }

        String[] words = requestPath.split("/");

        if (words.length == 2 && words[1].equals("tasks")) {
            return switch (requestMethod) {
                case "GET" -> TaskEndpoint.GET_TASKS;       //GET   /TASKS
                case "POST" -> TaskEndpoint.ADD_TASK;       //POST  /TASKS
                default -> TaskEndpoint.UNKNOWN;
            };
        }

        if (words.length == 3 && words[1].equals("tasks")) {
            return switch (requestMethod) {
                case "GET" -> TaskEndpoint.GET_TASK;        //GET     /TASKS/{id}
                case "POST" -> TaskEndpoint.UPDATE_TASK;    //POST    /TASKS/{id}
                case "DELETE" -> TaskEndpoint.DELETE_TASK;  //DELETE  /TASKS/{id}
                default -> TaskEndpoint.UNKNOWN;
            };
        }
        return TaskEndpoint.UNKNOWN;
    }
}