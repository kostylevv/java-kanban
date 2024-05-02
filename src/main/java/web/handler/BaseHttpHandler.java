package web.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Task;
import model.TaskStatusEnum;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final int CODE_OK_WITH_REPLY = 200;
    private static final int CODE_OK_NO_REPLY = 201;
    private static final int CODE_NOT_FOUND = 404;
    private static final int CODE_NOT_ACCEPTABLE = 406;
    private static final int CODE_ERROR = 500;

    protected TaskManager manager;

    protected Gson gson;

    BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    public Gson getGson() {
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
