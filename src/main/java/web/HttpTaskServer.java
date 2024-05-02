package web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import web.handler.taskhandler.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager taskManager;
    private static TaskHandler taskHandler;
    HttpServer httpServer;
    private Gson taskGson;

    public HttpTaskServer(TaskManager manager) {
        taskManager = manager;
        taskHandler = new TaskHandler(taskManager);
        taskGson = taskHandler.getGson();
    }

    public Gson getTaskGson() {
        return taskGson;
    }

    public static void main(String[] args) {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
    }

    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", taskHandler);
            httpServer.start();
            System.out.println("Task server is running on port " + PORT);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("Task server is stopped");
    }
}
