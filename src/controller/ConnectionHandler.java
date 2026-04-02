package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

// Wraps a connected socket and handles reading/writing messages
public class ConnectionHandler {

    public interface ConnectListener {
        void onConnected(ConnectionHandler handler);

        void onError(String message);
    }

    public interface MessageListener {
        void onMessage(String message);
    }

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public ConnectionHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Send a line of text to the peer
    public void send(String message) {
        out.println(message);
    }

    public Socket getSocket() {
        return socket;
    }

    // Retrieve the InputStream of the socket
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    // Retrieve the OutputStream of the socket
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    // Read one line — used during auth handshake before chat starts
    public String readLine() throws IOException {
        return in.readLine();
    }

    // Start reading chat messages from the peer in a background thread
    public void startListening(final MessageListener listener) {
        Runnable task = new Runnable() {
            public void run() {
                try {
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
        t.setDaemon(true);
        t.start();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            // do nothing
        }
    }
}
