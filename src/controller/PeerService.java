package controller;

// Manages peer connections using TcpServer (host) or TcpClient (connect)
public class PeerService {

    private ConnectionHandler connection;
    private final TcpServer server;
    private final TcpClient client;

    public PeerService() {
        server = new TcpServer();
        client = new TcpClient();
    }

    // Start listening for one incoming peer connection (Host mode)
    public void connectToNetwork(int port, ConnectionHandler.ConnectListener listener) {
        server.listen(port, listener);
    }

    // Connect outbound to a peer at ip:port (Connect/Manager mode)
    public void connectToPeer(String ip, int port, ConnectionHandler.ConnectListener listener) {
        client.connect(ip, port, listener);
    }

    public void setConnection(ConnectionHandler handler) {
        this.connection = handler;
    }

    public void sendMessage(String message) {
        if (connection != null) {
            connection.send(message);
        }
    }

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
