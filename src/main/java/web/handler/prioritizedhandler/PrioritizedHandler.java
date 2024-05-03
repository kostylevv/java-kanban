package web.handler.prioritizedhandler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Task;
import web.handler.BaseHttpHandler;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        PrioritizedEndpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_PRIORITIZED -> getPrioritized(exchange);
            case UNKNOWN -> sendNotFound(exchange, PrioritizedEndpoint.UNKNOWN.name() + " invoked");
        }

    }

    /**
     * Get all tasks end output them as JSON
     *
     * @param httpExchange exchange object
     */
    private void getPrioritized(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = manager.getPrioritizedTasks();
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
    private PrioritizedEndpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath == null || requestPath.isEmpty() || requestMethod == null || requestMethod.isEmpty()) {
            throw new IllegalArgumentException("requestPath: " + requestPath + ", requestMethod:" + requestMethod);
        }

        String[] words = requestPath.split("/");

        if (words.length == 2 && words[1].equals("prioritized")) {
            return switch (requestMethod) {
                case "GET" -> PrioritizedEndpoint.GET_PRIORITIZED;
                default -> PrioritizedEndpoint.UNKNOWN;
            };
        }

        return PrioritizedEndpoint.UNKNOWN;
    }
}