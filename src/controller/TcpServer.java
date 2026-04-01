package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Listens on a port and accepts one incoming peer connection
public class TcpServer {

    private ServerSocket serverSocket;

    public void listen(int port, ConnectionHandler.ConnectListener listener) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    Socket client = serverSocket.accept();
                    listener.onConnected(new ConnectionHandler(client));
                    serverSocket.close();
                } catch (IOException e) {
                    if (serverSocket != null && serverSocket.isClosed()) {
                        return;
                    }
                    listener.onError("Server error: " + e.getMessage());
                }
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
