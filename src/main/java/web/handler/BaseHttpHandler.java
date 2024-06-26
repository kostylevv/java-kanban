package web.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final int CODE_OK_WITH_REPLY = 200;
    private static final int CODE_OK_NO_REPLY = 201;
    private static final int CODE_NOT_FOUND = 404;
    private static final int CODE_NOT_ACCEPTABLE = 406;
    private static final int CODE_ERROR = 500;

    protected TaskManager manager;

    protected static Gson gson;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public abstract void handle(HttpExchange exchange) throws IOException;

    public static Gson getGson() {
        return gson;
    }


    protected void sendOkWithReply(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(CODE_OK_WITH_REPLY, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected void sendOkNoReply(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(CODE_OK_NO_REPLY, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    public void sendNotFound(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(CODE_NOT_FOUND, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    public void sendHasOverlap(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(CODE_NOT_ACCEPTABLE, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    public void sendError(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(CODE_ERROR, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }
}
