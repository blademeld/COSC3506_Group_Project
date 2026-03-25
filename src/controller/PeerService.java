package controller;

// Used to connect to the network and send/receive messages
public class PeerService {

    private ConnectionHandler connection;
    private final TcpServer server;
    private final TcpClient client;

    public PeerService() {
        server = new TcpServer();
        client = new TcpClient();
    }

    // Host mode - start listening for 1 connection
    public void connectToNetwork(int port, ConnectionHandler.ConnectListener listener) {
        server.listen(port, listener);
    }

    // Connect to a peer at ip:port
    public void connectToPeer(String ip, int port, ConnectionHandler.ConnectListener listener) {
        client.connect(ip, port, listener);
    }

    // Store the active connection once established
    public void setConnection(ConnectionHandler handler) {
        this.connection = handler;
    }

    // Send a message to the connected peer
    public void sendMessage(String message) {
        if (connection != null) {
            connection.send(message);
        }
    }

    // Disconnect from the network
    public void disconnectFromNetwork() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    public void startCall(String peerId) {
        // TODO Phase 6: initiate call with peerId
    }

    public void endCall() {
        // TODO Phase 6: terminate current call
    }
}
