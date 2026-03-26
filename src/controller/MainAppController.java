package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.TranscriptStore;

public class MainAppController {

    @FXML
    private ComboBox<String> modeSelector;
    @FXML
    private ListView<String> peerList;
    @FXML
    private ListView<String> messages;
    @FXML
    private ListView<String> callRecords;
    @FXML
    private TextField hostIP;
    @FXML
    private TextField hostPort;
    @FXML
    private TextArea transcriptDisplay;
    @FXML
    private TextField messageContent;
    @FXML
    private ComboBox<String> peerStatus;
    @FXML
    private TextField peerUsername;
    @FXML
    private Label messageReciever;
    @FXML
    private Button peerConnect;
    @FXML
    private Label transcriptsLabel;
    @FXML
    private Label callLogsLabel;

    private final TranscriptStore store = new TranscriptStore();
    private final PeerService peerService = new PeerService();
    private final ChatService chatService = new ChatService(store);
    private final TranscriptService transcriptService = new TranscriptService(store);

    @FXML
    public void initialize() {
        modeSelector.getItems().addAll("Host", "Connect", "Manager");
        peerStatus.getItems().addAll("Available", "Busy", "Away");
        modeSelector.setOnAction(e -> updateModeView(modeSelector.getValue()));
    }

    private void updateModeView(String mode) {
        if (mode == null)
            return;
        switch (mode) {
            case "Host":
                hostIP.setVisible(false);
                hostIP.setManaged(false);
                hostPort.setPromptText("Listen port...");
                peerConnect.setText("Listen");
                setManagerPanelVisible(false);
                break;
            case "Connect":
                hostIP.setVisible(true);
                hostIP.setManaged(true);
                hostPort.setPromptText("Port...");
                peerConnect.setText("Connect");
                setManagerPanelVisible(false);
                break;
            case "Manager":
                hostIP.setVisible(true);
                hostIP.setManaged(true);
                hostPort.setPromptText("Port...");
                peerConnect.setText("Connect");
                setManagerPanelVisible(true);
                break;
        }
    }

    private void setManagerPanelVisible(boolean visible) {
        messages.setVisible(visible);
        messages.setManaged(visible);
        transcriptsLabel.setVisible(visible);
        transcriptsLabel.setManaged(visible);
        callRecords.setVisible(visible);
        callRecords.setManaged(visible);
        callLogsLabel.setVisible(visible);
        callLogsLabel.setManaged(visible);
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

        String localId = peerUsername.getText().trim().isEmpty() ? "me" : peerUsername.getText().trim();
        String remoteId = ip.isEmpty() ? "peer" : ip;

        ConnectionHandler.ConnectListener listener = new ConnectionHandler.ConnectListener() {
            public void onConnected(ConnectionHandler handler) {
                peerService.setConnection(handler);
                chatService.connect(localId, remoteId, handler);

                handler.startListening(new ConnectionHandler.MessageListener() {
                    public void onMessage(String raw) {
                        chatService.receiveMessage(raw);
                        Platform.runLater(new Runnable() {
                            public void run() {
                                transcriptDisplay.appendText("Peer: " + raw + "\n");
                                refreshTranscriptList();
                            }
                        });
                    }
                });

                Platform.runLater(new Runnable() {
                    public void run() {
                        transcriptDisplay.appendText("[" + mode + "] Connected.\n");
                    }
                });
            }

            public void onError(String message) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        transcriptDisplay.appendText("[Error] " + message + "\n");
                    }
                });
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
        if (msg.isEmpty())
            return;

        String username = peerUsername.getText().trim();
        String sender = username.isEmpty() ? "Me" : username;

        chatService.sendMessage(msg);
        transcriptDisplay.appendText(sender + ": " + msg + "\n");
        messageContent.clear();
        refreshTranscriptList();
    }

    private void refreshTranscriptList() {
        messages.getItems().clear();
        for (model.Message msg : transcriptService.getMessages()) {
            messages.getItems().add(msg.getSenderId() + ": " + msg.getContent());
        }
    }
}
