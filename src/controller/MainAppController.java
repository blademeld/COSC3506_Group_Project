package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.CallRecord;
import model.PeerProfile;
import model.TranscriptStore;
import java.io.File;
import java.util.Date;
import java.util.UUID;

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
    private Button sendMessage;
    @FXML
    private Label transcriptsLabel;
    @FXML
    private Label callLogsLabel;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private Button sendCall;

    private final TranscriptStore store = new TranscriptStore();
    private final ManagerService peerService = new ManagerService(store);
    private final ChatService chatService = new ChatService(store);
    private final TranscriptService transcriptService = new TranscriptService(store);
    private final KeyManager keyManager = new KeyManager();
    private final ChallengeService challengeService = new ChallengeService();
    private PeerProfile localProfile;
    private String connectedPeerName = null;
    private boolean isInCall = false;
    private CallRecord currentCall = null;

    @FXML
    public void initialize() {
        modeSelector.getItems().addAll("Host", "Connect", "Manager");
        peerStatus.getItems().addAll("Available", "Busy", "Away");
        modeSelector.setOnAction(e -> updateModeView(modeSelector.getValue()));

        peerList.setOnMouseClicked(new javafx.event.EventHandler<javafx.scene.input.MouseEvent>() {
            public void handle(javafx.scene.input.MouseEvent event) {
                String selected = peerList.getSelectionModel().getSelectedItem();
                filterTranscriptFor(selected);
            }
        });

        messageContent.setOnKeyPressed(new javafx.event.EventHandler<javafx.scene.input.KeyEvent>() {
            public void handle(javafx.scene.input.KeyEvent event) {
                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    handleSend();
                }
            }
        });

        sendCall.setDisable(true);
        // Keypair is generated in handleConnect() once the username is known
    }

    private void updateModeView(String mode) {
        if (mode == null)
            return;
        switch (mode) {
            case "Host":
                hostIP.setVisible(false);
                hostIP.setManaged(false);
                hostPort.setVisible(true);
                hostPort.setManaged(true);
                hostPort.setPromptText("Listen port...");
                peerConnect.setVisible(true);
                peerConnect.setManaged(true);
                peerConnect.setText("Listen");
                setSuperPanelVisible(false);
                sendMessage.setDisable(false);
                messageContent.setDisable(false);
                openMenuItem.setDisable(true);
                break;
            case "Connect":
                hostIP.setVisible(true);
                hostIP.setManaged(true);
                hostPort.setVisible(true);
                hostPort.setManaged(true);
                hostPort.setPromptText("Port...");
                peerConnect.setVisible(true);
                peerConnect.setManaged(true);
                peerConnect.setText("Connect");
                setSuperPanelVisible(false);
                sendMessage.setDisable(false);
                messageContent.setDisable(false);
                openMenuItem.setDisable(true);
                break;
            case "Manager":
                hostIP.setVisible(false);
                hostIP.setManaged(false);
                hostPort.setVisible(false);
                hostPort.setManaged(false);
                peerConnect.setVisible(false);
                peerConnect.setManaged(false);
                setSuperPanelVisible(true);
                sendMessage.setDisable(true);
                messageContent.setDisable(true);
                openMenuItem.setDisable(false);
                refreshPeerList();
                refreshCallLogs();
                messages.getItems().clear();
                break;
        }
    }

    // The super panel shows the Host's view: peer list on the left + message
    // history sidebar
    private void setSuperPanelVisible(boolean visible) {
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
        String localId = peerUsername.getText().trim();

        if (localId.isEmpty()) {
            transcriptDisplay.appendText("[Error] Please enter your username before connecting.\n");
            return;
        }

        try {
            localProfile = keyManager.generateProfile(localId);
            peerService.setProfile(localProfile);
        } catch (Exception e) {
            transcriptDisplay.appendText("[Error] Could not generate keypair.\n");
            return;
        }

        boolean needsIp = "Connect".equals(mode); // if mode is Connect, then ip is required
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

        ChallengeService.AuthLogger authLogger = new ChallengeService.AuthLogger() {
            public void log(String message) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        transcriptDisplay.appendText("  " + message + "\n");
                    }
                });
            }
        };

        ConnectionHandler.ConnectListener peerListener = new ConnectionHandler.ConnectListener() {
            public void onConnected(ConnectionHandler handler) {
                log("Establishing cryptographic identity...");

                String peerDisplay;
                if ("Connect".equals(mode)) {
                    peerDisplay = challengeService.authenticateAsClient(handler, localProfile, localId, authLogger);
                } else {
                    peerDisplay = challengeService.authenticateAsHost(handler, localProfile, localId, authLogger);
                }

                if (peerDisplay == null) {
                    log("[Error] Authentication failed. Connection closed.");
                    handler.close();
                    return;
                }

                final String displayName = peerDisplay;
                connectedPeerName = displayName;
                log("Connected to " + displayName);

                Platform.runLater(new Runnable() {
                    public void run() {
                        messageReciever.setText("Chatting with " + displayName);
                    }
                });

                peerService.setConnection(handler);
                chatService.connect(localId, peerDisplay, handler);
                Platform.runLater(new Runnable() {
                    public void run() {
                        sendCall.setDisable(false);
                    }
                });

                handler.startListening(new ConnectionHandler.MessageListener() {
                    public void onMessage(String raw) {
                        chatService.receiveMessage(raw);
                        Platform.runLater(new Runnable() {
                            public void run() {
                                transcriptDisplay.appendText(raw + "\n");
                                refreshTranscriptList();
                            }
                        });
                    }
                });

                log("[" + mode + "] Connected.");
            }

            public void onError(String message) {
                log("[Error] " + message);
            }
        };

        if ("Connect".equals(mode)) {
            transcriptDisplay.appendText("[Connect] Connecting to " + ip + ":" + port + "...\n");
            messageReciever.setText(ip + ":" + port);
            peerService.connectToPeer(ip, port, peerListener);
        } else {
            transcriptDisplay.appendText("[Host] Listening on port " + port + "...\n");
            messageReciever.setText("Listening on :" + port);
            peerService.connectToNetwork(port, peerListener);
        }
    }

    @FXML
    private void handleSend() {
        String msg = messageContent.getText().trim();
        if (msg.isEmpty())
            return;

        String username = peerUsername.getText().trim();
        String role = "Connect".equals(modeSelector.getValue()) ? "[Peer]" : "[Host]";
        String sender = username.isEmpty() ? role : role + " " + username;

        String wireMessage = sender + ": " + msg;
        chatService.sendMessage(wireMessage);
        transcriptDisplay.appendText(wireMessage + "\n");
        messageContent.clear();
        refreshTranscriptList();
    }

    @FXML
    private void handleCall() {
        if (!isInCall) {
            if (connectedPeerName == null) {
                log("[Error] Not connected to a peer.");
                return;
            }
            String localId = peerUsername.getText().trim();
            currentCall = new CallRecord(UUID.randomUUID().toString(), localId, connectedPeerName, new Date(), null);
            store.addCallRecord(currentCall); // add call record to transcript store
            peerService.startCall(connectedPeerName);
            isInCall = true;
            sendCall.setText("Hang Up");
            log("[Call] Call started with " + connectedPeerName);
        } else {
            if (currentCall != null) {
                currentCall.endCall(new Date());
                currentCall = null;
            }
            peerService.endCall();
            isInCall = false;
            sendCall.setText("Call");
            log("[Call] Call ended.");
        }
    }

    @FXML
    private void handleReset() {
        if (isInCall) {
            peerService.endCall();
            isInCall = false;
        }
        peerService.disconnectFromNetwork();
        chatService.disconnect();
        connectedPeerName = null;
        transcriptDisplay.clear();
        peerList.getItems().clear();
        messages.getItems().clear();
        messageReciever.setText("Message with...");
        sendCall.setDisable(true);
        sendCall.setText("Call");
        transcriptDisplay.appendText("Session reset.\n");
    }

    private void log(final String message) {
        Platform.runLater(new Runnable() {
            public void run() {
                transcriptDisplay.appendText(message + "\n");
            }
        });
    }

    private void refreshCallLogs() {
        callRecords.getItems().clear();
        for (CallRecord record : peerService.getActiveCalls()) {
            String status = record.getEndTime() != null ? "ended" : "ongoing";
            String entry = record.getSenderId() + " called " + record.getReceiverId() + " (" + status + ")";
            callRecords.getItems().add(entry);
        }
    }

    private void refreshTranscriptList() {
        messages.getItems().clear();
        for (model.Message msg : transcriptService.getMessages()) {
            messages.getItems().add(msg.getContent());
        }
    }

    // Shows only messages involving the selected peer (sent or received)
    private void filterTranscriptFor(String selected) {
        if (selected == null)
            return;
        String name = selected.replaceFirst("^[🟢⚫] ", "");
        messages.getItems().clear();
        for (model.Message msg : peerService.getMessagesFor(name)) {
            messages.getItems().add(msg.getContent());
        }
        refreshCallLogs();
    }

    private void refreshPeerList() {
        peerList.getItems().clear();
        String localName = peerUsername.getText().trim();
        boolean activeSession = connectedPeerName != null;
        for (model.PeerClient peer : peerService.getActivePeers()) {
            String name = peer.getUsername();
            boolean online = activeSession && (name.equals(connectedPeerName) || name.equals(localName));
            String entry = (online ? "🟢 " : "⚫ ") + name;
            if (!peerList.getItems().contains(entry)) {
                peerList.getItems().add(entry);
            }
        }
    }

    @FXML
    private void handleSave() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Transcript");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = chooser.showSaveDialog(transcriptDisplay.getScene().getWindow());
        if (file != null) {
            transcriptService.saveToFile(file.getAbsolutePath());
            transcriptDisplay.appendText("[Saved] Transcript saved to " + file.getName() + "\n");
        }
    }

    @FXML
    private void handleOpen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Import Transcript");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = chooser.showOpenDialog(transcriptDisplay.getScene().getWindow());
        if (file != null) {
            transcriptService.loadFromFile(file.getAbsolutePath());
            transcriptDisplay.appendText("[Loaded] Imported transcript from " + file.getName() + "\n");
            refreshPeerList();
            refreshTranscriptList();
        }
    }

    @FXML
    private void handleQuit() {
        Platform.exit();
    }
}
