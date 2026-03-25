package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainAppController {

    @FXML private ComboBox<String> modeSelector;
    @FXML private ListView<String> peerList;
    @FXML private ListView<String> messages;
    @FXML private ListView<String> callRecords;
    @FXML private TextField hostIP;
    @FXML private TextField hostPort;
    @FXML private TextArea transcriptDisplay;
    @FXML private TextField messageContent;
    @FXML private ComboBox<String> peerStatus;
    @FXML private TextField peerUsername;
    @FXML private Label messageReciever;

    private final PeerService peerService = new PeerService();

    @FXML
    public void initialize() {
        modeSelector.getItems().addAll("Host", "Connect", "Manager");
        peerStatus.getItems().addAll("Available", "Busy", "Away");

        // Update UI when mode changes
        modeSelector.setOnAction(e -> updateModeView(modeSelector.getValue()));
    }

    private void updateModeView(String mode) {
        if (mode == null) return;
        switch (mode) {
            case "Host":
                // Hosting: only need a port to listen on, not a remote IP
                hostIP.setVisible(false);
                hostIP.setManaged(false);
                hostPort.setPromptText("Listen port...");
                break;
            case "Connect":
            case "Manager":
                // Connecting out: need both IP and port
                hostIP.setVisible(true);
                hostIP.setManaged(true);
                hostPort.setPromptText("Port...");
                break;
        }
    }

    @FXML
    private void handleConnect() {
        String ip = hostIP.getText().trim();
        String portStr = hostPort.getText().trim();
        String mode = modeSelector.getValue();

        boolean needsIp = !"Host".equals(mode);
        if (mode == null || portStr.isEmpty() || (needsIp && ip.isEmpty())) {
            transcriptDisplay.appendText("[Error] Please select a mode and enter the required fields.\n");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            transcriptDisplay.appendText("[Error] Port must be a number.\n");
            return;
        }

        ConnectionHandler.ConnectListener listener = new ConnectionHandler.ConnectListener() {
            public void onConnected(ConnectionHandler handler) {
                peerService.setConnection(handler);
                handler.startListening(new ConnectionHandler.MessageListener() {
                    public void onMessage(String msg) {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                transcriptDisplay.appendText("Peer: " + msg + "\n");
                            }
                        });
                    }
                });
                Platform.runLater(() -> transcriptDisplay.appendText("[" + mode + "] Connected.\n"));
            }
            public void onError(String message) {
                Platform.runLater(() -> transcriptDisplay.appendText("[Error] " + message + "\n"));
            }
        };

        if ("Host".equals(mode)) {
            transcriptDisplay.appendText("[Host] Listening on port " + port + "...\n");
            messageReciever.setText("Listening on :" + port);
            peerService.connectToNetwork(port, listener);
        } else {
            transcriptDisplay.appendText("[" + mode + "] Connecting to " + ip + ":" + port + "...\n");
            messageReciever.setText(ip + ":" + port);
            peerService.connectToPeer(ip, port, listener);
        }
    }

    @FXML
    private void handleSend() {
        String msg = messageContent.getText().trim();
        if (msg.isEmpty()) return;

        String username = peerUsername.getText().trim();
        String sender = username.isEmpty() ? "Me" : username;

        peerService.sendMessage(msg);
        transcriptDisplay.appendText(sender + ": " + msg + "\n");
        messageContent.clear();
    }
}
