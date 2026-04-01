// NOTE: This controller is no longer referenced by MainApp.fxml.
// The active controller is controller.MainAppController.
// Keeping this file for reference only.
package view;

import java.net.URL;
import java.util.ResourceBundle;

import controller.CallService;
import controller.PeerService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainAppController implements Initializable {

	@FXML
	private ListView<String> peerList;

	@FXML
	private ComboBox<String> modeSelector;

	@FXML
	private Button peerConnect;

	@FXML
	private TextField hostIP;

	@FXML
	private TextField hostPort;

	@FXML
	private Button sendCall;

	@FXML
	private ListView<String> callRecords;

	@FXML
	private Label callLogsLabel;

	@FXML
	private TextArea transcriptDisplay;

	@FXML
	private TextField messageContent;

	@FXML
	private Button sendMessage;

	@FXML
	private Label messageReciever;

	@FXML
	private ComboBox<String> peerStatus;

	@FXML
	private TextField peerUsername;

	@FXML
	private ListView<String> messages;

	@FXML
	private Label transcriptsLabel;

	private final PeerService peerService = new PeerService();
	private final CallService callService = new CallService();

	private boolean isInCall = false;
	private String activeCallPeerId = null;

	private final java.util.List<String> peers = java.util.Arrays.asList("peer1", "peer2", "peer3");

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		modeSelector.getItems().addAll("Host", "Connect");
		modeSelector.getSelectionModel().selectFirst();
		peerStatus.getItems().addAll("Available", "Busy", "Offline");
		peerStatus.getSelectionModel().select("Available");
		peerStatus.setOnAction(evt -> refreshPeerListDisplay());

		callLogsLabel.setVisible(false);
		callRecords.setVisible(false);
		transcriptsLabel.setVisible(false);
		messages.setVisible(false);

		refreshPeerListDisplay();
	}

	private void refreshPeerListDisplay() {
		String status = peerStatus.getValue();
		peerList.getItems().clear();
		for (String peer : peers) {
			peerList.getItems().add(String.format("%s <%s>", peer, status));
		}
	}

	@FXML
	private void handleConnect() {
		String selectedMode = modeSelector.getValue();
		String ip = hostIP.getText();
		String port = hostPort.getText();

		if (selectedMode == null) {
			return;
		}

		try {
			int parsedPort = Integer.parseInt(port);
			if ("Host".equalsIgnoreCase(selectedMode)) {
				peerService.connectToNetwork(parsedPort, new controller.ConnectionHandler.ConnectListener() {
					@Override
					public void onConnected(controller.ConnectionHandler handler) {
						Platform.runLater(() -> messageReciever.setText("Listening on " + parsedPort));
						peerService.setConnection(handler);
					}

					@Override
					public void onError(String error) {
						Platform.runLater(() -> messageReciever.setText("Host failed: " + error));
					}
				});
			} else {
				peerService.connectToPeer(ip, parsedPort, new controller.ConnectionHandler.ConnectListener() {
					@Override
					public void onConnected(controller.ConnectionHandler handler) {
						Platform.runLater(() -> messageReciever.setText("Connected to " + ip + ":" + parsedPort));
						peerService.setConnection(handler);
					}

					@Override
					public void onError(String error) {
						Platform.runLater(() -> messageReciever.setText("Connect failed: " + error));
					}
				});
			}
		} catch (NumberFormatException ex) {
			messageReciever.setText("Invalid port");
		}
	}

	@FXML
	private void handleSend() {
		String text = messageContent.getText();
		if (text == null || text.trim().isEmpty()) {
			return;
		}
		transcriptDisplay.appendText("You: " + text + "\n");
		messageContent.clear();
		peerService.sendMessage(text);
	}

	@FXML
	private void handleCall() {
		String selectedPeer = peerList.getSelectionModel().getSelectedItem();
		if (selectedPeer == null || selectedPeer.trim().isEmpty()) {
			messageReciever.setText("Select a peer first");
			return;
		}

		if (!isInCall) {
			activeCallPeerId = selectedPeer;
			peerService.startCall(selectedPeer);
			callService.connect(selectedPeer);
			callService.sendAudio();
			isInCall = true;
			sendCall.setText("Hang Up");
			messageReciever.setText("In call with " + selectedPeer);
			callLogsLabel.setVisible(true);
			callRecords.setVisible(true);
			callRecords.getItems().add("Call started with " + selectedPeer);
		} else {
			peerService.endCall();
			callService.disconnect(activeCallPeerId);
			isInCall = false;
			sendCall.setText("Call");
			messageReciever.setText("Call ended");
			callRecords.getItems().add("Call ended with " + activeCallPeerId);
			activeCallPeerId = null;
		}
	}

	@FXML
	private void handleOpen() {
		messageReciever.setText("Open menu clicked");
	}

	@FXML
	private void handleSave() {
		messageReciever.setText("Save menu clicked");
	}

	@FXML
	private void handleReset() {
		messageReciever.setText("Reset menu clicked");
	}

	@FXML
	private void handleQuit() {
		Platform.exit();
	}
}
