package web;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import web.handler.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler(Managers.getDefault()));
            httpServer.start();
            System.out.println("Task server is running on port " + PORT);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
