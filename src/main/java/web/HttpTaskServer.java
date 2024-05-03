package web;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import web.handler.epichandler.EpicHandler;
import web.handler.historyhandler.HistoryHandler;
import web.handler.prioritizedhandler.PrioritizedHandler;
import web.handler.subtaskhandler.SubtaskHandler;
import web.handler.taskhandler.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        taskManager = manager;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

        httpServer.start();
        System.out.println("Task server is running on port " + PORT);
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("Task server is stopped");
    }
}
