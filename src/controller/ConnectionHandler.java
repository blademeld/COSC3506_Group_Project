package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

// Wraps a connected socket and handles reading/writing messages
public class ConnectionHandler {

    // Error and success callbacks
    public interface ConnectListener {
        void onConnected(ConnectionHandler handler);

        void onError(String message);
    }

    // Called for each line received from the peer
    public interface MessageListener {
        void onMessage(String message);
    }

    private final Socket socket;
    private final PrintWriter out;

    public ConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    // Send a line of text to the peer
    public void send(String message) {
        out.println(message);
    }

    // Start reading from the peer in a background thread
    public void startListening(final MessageListener listener) {
        Runnable task = new Runnable() {
            public void run() {
                try {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    String line;
                    while ((line = in.readLine()) != null) {
                        listener.onMessage(line);
                    }
                } catch (IOException e) {
                    listener.onMessage("[Disconnected]");
                }
            }
        };

        Thread t = new Thread(task);
        t.setDaemon(true); // thread stops automatically when the app closes
        t.start();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            /* ignore */ }
    }
}
