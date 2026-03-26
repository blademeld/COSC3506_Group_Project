package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Listens on a port and accepts one incoming peer connection
public class TcpServer {

    public void listen(int port, ConnectionHandler.ConnectListener listener) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(port);
                    Socket client = serverSocket.accept();
                    listener.onConnected(new ConnectionHandler(client));
                    serverSocket.close();
                } catch (IOException e) {
                    listener.onError("Server error: " + e.getMessage());
                }
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
