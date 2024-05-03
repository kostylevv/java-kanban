package web.handler.subtaskhandler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import manager.exception.NotFoundException;
import manager.exception.OverlapException;
import model.Subtask;
import web.handler.BaseHttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SubtaskEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS -> getAllSubtasks(exchange);
            case GET_SUBTASK -> getSubtask(exchange, exchange.getRequestURI().getPath());
            case ADD_SUBTASK -> addSubtask(exchange);
            case UPDATE_SUBTASK -> updateSubtask(exchange);
            case DELETE_SUBTASK -> deleteSubtask(exchange, exchange.getRequestURI().getPath());
            case UNKNOWN -> sendNotFound(exchange, SubtaskEndpoint.UNKNOWN.name() + " invoked");
        }

    }

    /**
     * Get all subtasks end output them as JSON
     *
     * @param httpExchange exchange object
     */
    private void getAllSubtasks(HttpExchange httpExchange) throws IOException {
        List<Subtask> subtasks = manager.getAllSubtasks();
        String tasksJson = gson.toJson(subtasks);
        sendOkWithReply(httpExchange, tasksJson);
    }

    /**
     * Get subtask by provided ID
     *
     * @param httpExchange exchange object
     * @param requestPath  path containing ID i.e. /subtasks/{id}
     * @throws ArithmeticException if ID is not integer
     * @throws IOException         if sendNotFound method will throw this exception
     */
    private void getSubtask(HttpExchange httpExchange, String requestPath) throws IOException {
        String[] words = requestPath.split("/");
        if (words.length == 3 && words[1].equals("subtasks")) {
            try {
                int subtaskId = Integer.parseInt(words[2]);
                Subtask subtask = manager.getSubTaskById(subtaskId);
                String taskJson = gson.toJson(subtask, Subtask.class);
                sendOkWithReply(httpExchange, taskJson);
            } catch (NotFoundException notFoundException) {
                sendNotFound(httpExchange, "Subtask wasn't found");
            } catch (NumberFormatException numberFormatException) {
                sendError(httpExchange, words[2] + " is incorrect id");
            }
        }
    }

    /**
     * Add subtask
     *
     * @param httpExchange exchange object with new Subtask in json-string in body
     * @throws IOException if send methods will throw this exception
     */
    private void addSubtask(HttpExchange httpExchange) throws IOException {
        String body = new BufferedReader(
                new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            manager.addSubtask(subtask);
            sendOkNoReply(httpExchange, "added task " + subtask.toString());
        } catch (OverlapException iae) {
            sendHasOverlap(httpExchange, "subtask has overlap with existing task");
        } catch (Exception e) {
            sendError(httpExchange, e.getMessage());
        }
    }

    /**
     * Update subtask
     *
     * @param httpExchange exchange object with updated Subtask in json-string in body
     * @throws IOException if send methods will throw this exception
     */
    private void updateSubtask(HttpExchange httpExchange) throws IOException {
        String body = new BufferedReader(
                new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            manager.updateSubtask(subtask);
            sendOkNoReply(httpExchange, "added subtask " + subtask.toString());
        } catch (OverlapException iae) {
            sendHasOverlap(httpExchange, "subtask has overlap with existing task");
        } catch (NotFoundException nfe) {
            sendError(httpExchange, "subtask wasn't found");
        } catch (Exception e) {
            sendError(httpExchange, e.getMessage());
        }
    }

    /**
     * Delete subtask with provided ID
     *
     * @param httpExchange exchange object
     * @param requestPath path containing ID i.e. /subtasks/{id}
     * @throws IOException if send methods will throw this exception
     */
    private void deleteSubtask(HttpExchange httpExchange, String requestPath) throws IOException {
        String[] words = requestPath.split("/");
        if (words.length == 3 && words[1].equals("subtasks")) {
            try {
                int subtaskId = Integer.parseInt(words[2]);
                manager.deleteSubtaskById(subtaskId);
                sendOkWithReply(httpExchange, "Subtask deleted");
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
    private SubtaskEndpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath == null || requestPath.isEmpty() || requestMethod == null || requestMethod.isEmpty()) {
            throw new IllegalArgumentException("requestPath: " + requestPath + ", requestMethod:" + requestMethod);
        }

        String[] words = requestPath.split("/");

        if (words.length == 2 && words[1].equals("subtasks")) {
            return switch (requestMethod) {
                case "GET" -> SubtaskEndpoint.GET_SUBTASKS;
                case "POST" -> SubtaskEndpoint.ADD_SUBTASK;
                default -> SubtaskEndpoint.UNKNOWN;
            };
        }

        if (words.length == 3 && words[1].equals("subtasks")) {
            return switch (requestMethod) {
                case "GET" -> SubtaskEndpoint.GET_SUBTASK;
                case "POST" -> SubtaskEndpoint.UPDATE_SUBTASK;
                case "DELETE" -> SubtaskEndpoint.DELETE_SUBTASK;
                default -> SubtaskEndpoint.UNKNOWN;
            };
        }
        return SubtaskEndpoint.UNKNOWN;
    }
}
