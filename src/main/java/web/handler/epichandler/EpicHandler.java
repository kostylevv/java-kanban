package web.handler.epichandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import manager.exception.NotFoundException;
import model.Epic;
import model.Subtask;
import web.handler.BaseHttpHandler;
import web.handler.subtaskhandler.SubtaskEndpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EpicEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS -> getAllEpics(exchange);
            case GET_EPIC -> getEpic(exchange, exchange.getRequestURI().getPath());
            case ADD_EPIC -> addEpic(exchange);
            case GET_EPIC_SUBTASKS -> getEpicSubtasks(exchange, exchange.getRequestURI().getPath());
            case DELETE_EPIC -> deleteEpic(exchange, exchange.getRequestURI().getPath());
            case UNKNOWN -> sendNotFound(exchange, SubtaskEndpoint.UNKNOWN.name() + " invoked");
        }

    }

    /**
     * Get all epics end output them as JSON
     *
     * @param httpExchange exchange object
     */
    private void getAllEpics(HttpExchange httpExchange) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        String epicsJson = gson.toJson(epics);
        sendOkWithReply(httpExchange, epicsJson);
    }

    /**
     * Get epic by provided ID
     *
     * @param httpExchange exchange object
     * @param requestPath  path containing ID i.e. /epics/{id}
     * @throws ArithmeticException if ID is not integer
     * @throws IOException         if sendNotFound method will throw this exception
     */
    private void getEpic(HttpExchange httpExchange, String requestPath) throws IOException {
        String[] words = requestPath.split("/");
        if (words.length == 3 && words[1].equals("epics")) {
            try {
                int epicId = Integer.parseInt(words[2]);
                Epic epic = manager.getEpicById(epicId);
                String taskJson = gson.toJson(epic, Epic.class);
                sendOkWithReply(httpExchange, taskJson);
            } catch (NotFoundException notFoundException) {
                sendNotFound(httpExchange, "Epic wasn't found");
            } catch (NumberFormatException numberFormatException) {
                sendError(httpExchange, words[2] + " is incorrect id");
            }
        }
    }

    /**
     * Add epic
     *
     * @param httpExchange exchange object with new Epic in json-string in body
     * @throws IOException if send methods will throw this exception
     */
    private void addEpic(HttpExchange httpExchange) throws IOException {
        String body = new BufferedReader(
                new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            Epic epic = gson.fromJson(body, Epic.class);
            manager.addEpic(epic);
            sendOkNoReply(httpExchange, "added epic " + subtask.toString());
        } catch (Exception e) {
            sendError(httpExchange, e.getMessage());
        }
    }

    /**
     * Delete epic with provided ID
     *
     * @param httpExchange exchange object
     * @param requestPath path containing ID i.e. /epics/{id}
     * @throws IOException if send methods will throw this exception
     */
    private void deleteEpic(HttpExchange httpExchange, String requestPath) throws IOException {
        String[] words = requestPath.split("/");
        if (words.length == 3 && words[1].equals("epics")) {
            try {
                int epicId = Integer.parseInt(words[2]);
                manager.deleteEpicById(epicId);
                sendOkWithReply(httpExchange, "Epic deleted");
            } catch (NumberFormatException numberFormatException) {
                sendError(httpExchange, words[2] + " is incorrect id");
            }
        }
    }

    /**
     * Get epic's subtasks
     *
     * @param httpExchange exchange object
     * @param requestPath path containing ID i.e. /epics/{id}/subtasks
     * @throws IOException if send methods will throw this exception
     */
    private void getEpicSubtasks(HttpExchange httpExchange, String requestPath) throws IOException {
        String[] words = requestPath.split("/");
        if (words.length == 4 && words[1].equals("epics") && words[3].equals("subtasks")) {
            try {
                int epicId = Integer.parseInt(words[2]);
                Epic epic = manager.getEpicById(epicId);
                List<Subtask> subtasks = manager.getSubtasks(epic);
                String subtasksJson = gson.toJson(subtasks);
                sendOkWithReply(httpExchange, subtasksJson);
            } catch (NotFoundException notFoundException) {
                sendNotFound(httpExchange, "Epic wasn't found");
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
    private EpicEndpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath == null || requestPath.isEmpty() || requestMethod == null || requestMethod.isEmpty()) {
            throw new IllegalArgumentException("requestPath: " + requestPath + ", requestMethod:" + requestMethod);
        }

        String[] words = requestPath.split("/");

        if (words.length == 2 && words[1].equals("epics")) {
            return switch (requestMethod) {
                case "GET" -> EpicEndpoint.GET_EPICS;
                case "POST" -> EpicEndpoint.ADD_EPIC;
                default -> EpicEndpoint.UNKNOWN;
            };
        }

        if (words.length == 3 && words[1].equals("epics")) {
            return switch (requestMethod) {
                case "GET" -> EpicEndpoint.GET_EPIC;
                case "DELETE" -> EpicEndpoint.DELETE_EPIC;
                default -> EpicEndpoint.UNKNOWN;
            };
        }

        if (words.length == 4 && words[1].equals("epics") && words[3].equals("subtasks")) {
            return switch (requestMethod) {
                case "GET" -> EpicEndpoint.GET_EPIC_SUBTASKS;
                default -> EpicEndpoint.UNKNOWN;
            };
        }

        return EpicEndpoint.UNKNOWN;
    }
}
