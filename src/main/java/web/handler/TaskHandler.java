package web.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS -> sendOkNoReply(exchange, Endpoint.GET_TASKS.name() + " invoked");
            case GET_TASK -> sendOkNoReply(exchange, Endpoint.GET_TASK.name() + " invoked");
            case CREATE_TASK -> sendOkWithReply(exchange, Endpoint.CREATE_TASK.name() + " invoked");
            case UPDATE_TASK -> sendOkWithReply(exchange, Endpoint.UPDATE_TASK.name() + " invoked");
            case DELETE_TASK -> sendOkNoReply(exchange, Endpoint.DELETE_TASK.name() + " invoked");
            case UNKNOWN -> sendNotFound(exchange, Endpoint.UNKNOWN.name() + " invoked");
            default -> new UnsupportedOperationException("Unsupported endpoint provided");
        }

    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestPath == null || requestPath.isEmpty() || requestMethod == null || requestMethod.isEmpty()) {
            throw new IllegalArgumentException("requestPath: " + requestPath + ", requestMethod:" + requestMethod);
        }

        String[] words = requestPath.split("/");

        if (words.length == 2 && words[1].equals("tasks")) {
            return switch (requestMethod){
                case "GET" -> Endpoint.GET_TASKS;       //GET   /TASKS
                case "POST" -> Endpoint.CREATE_TASK;    //POST  /TASKS
                default -> Endpoint.UNKNOWN;
            };
        }

        if (words.length == 3 && words[1].equals("tasks")) {
            return switch (requestMethod){
                case "GET" -> Endpoint.GET_TASK;        //GET     /TASKS/{id}
                case "POST" -> Endpoint.UPDATE_TASK;    //POST    /TASKS/{id}
                case "DELETE" -> Endpoint.DELETE_TASK;  //DELETE  /TASKS/{id}
                default -> Endpoint.UNKNOWN;
            };
        }
        return Endpoint.UNKNOWN;
    }
}
