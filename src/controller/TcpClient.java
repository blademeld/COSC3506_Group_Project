package controller;

import java.io.IOException;
import java.net.Socket;

// Connects outbound to a peer at ip:port
public class TcpClient {

    // Dial out to ip:port (runs in background thread)
    public void connect(String ip, int port, ConnectionHandler.ConnectListener listener) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    Socket socket = new Socket(ip, port);
                    listener.onConnected(new ConnectionHandler(socket));
                } catch (IOException e) {
                    listener.onError("Connection failed: " + e.getMessage());
                }
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
