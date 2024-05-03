package web.handler.historyhandler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;
import web.handler.BaseHttpHandler;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HistoryEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY -> getHistory(exchange);
            case UNKNOWN -> sendNotFound(exchange, HistoryEndpoint.UNKNOWN.name() + " invoked");
        }

    }

    /**
     * Get all tasks end output them as JSON
     *
     * @param httpExchange exchange object
     */
    private void getHistory(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = manager.getHistory();
        String tasksJson = gson.toJson(tasks);
        sendOkWithReply(httpExchange, tasksJson);
    }

    /**
     * Parse request to identify appropriate endpoint
     *
     * @param requestPath   request path
     * @param requestMethod request method
     * @return appropriate Endpoint object including Endpoint.UNKNOWN
     */
    private HistoryEndpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath == null || requestPath.isEmpty() || requestMethod == null || requestMethod.isEmpty()) {
            throw new IllegalArgumentException("requestPath: " + requestPath + ", requestMethod:" + requestMethod);
        }

        String[] words = requestPath.split("/");

        if (words.length == 2 && words[1].equals("history")) {
            return switch (requestMethod) {
                case "GET" -> HistoryEndpoint.GET_HISTORY;
                default -> HistoryEndpoint.UNKNOWN;
            };
        }

        return HistoryEndpoint.UNKNOWN;
    }
}