import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;

	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		showConnectScene();
	}

	// Main connection scene where users can choose to host a session or connect to a peer
	private void showConnectScene() {
		Label titleLabel = new Label();
		titleLabel.setText("PeerLink Chat");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		Label subtitleLabel = new Label();
		subtitleLabel.setText("Web3-based P2P Chat Application");

		TextField ipField = new TextField();
		ipField.setPromptText("Host IP address (Connect only)");
		ipField.setMaxWidth(250);

		TextField portField = new TextField();
		portField.setPromptText("Port number");
		portField.setMaxWidth(250);

		Button hostButton = new Button();
		hostButton.setText("Host Session");

		Button connectButton = new Button();
		connectButton.setText("Connect to Peer");

		Label instructionLabel = new Label();
		instructionLabel.setText("Host Session uses this machine's port. Connect to Peer uses the host IP address and port.");

		hostButton.setOnAction(event -> {
			showHostChatScene();
		});

		// Layout
		VBox root = new VBox(12);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));
		root.getChildren().addAll(
			titleLabel,
			subtitleLabel,
			ipField,
			portField,
			hostButton,
			connectButton,
			instructionLabel
		);

		Scene scene = new Scene(root, 700, 500);

		primaryStage.setTitle("PeerLink Chat");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void showHostChatScene() {
		Label titleLabel = new Label();
		titleLabel.setText("PeerLink Chat - Host Chat Window");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		Label statusLabel = new Label();
		statusLabel.setText("Hosting session");

		TextArea chatArea = new TextArea();
		chatArea.setEditable(false);
		chatArea.setWrapText(true);
		chatArea.setText("Text placeholder for chat history...\n");

		TextField messageField = new TextField();
		messageField.setPromptText("Type a message...");

		Button sendButton = new Button();
		sendButton.setText("Send");

		Button backButton = new Button();
		backButton.setText("Back");

		sendButton.setOnAction(event -> {
			String text = messageField.getText();

			// Prevent sending empty messages
			if (text == null) {
				return;
			}

			String trimmedText = text.trim();

			if (trimmedText.isEmpty()) {
				return;
			}

			chatArea.appendText("You: " + trimmedText + "\n");
			messageField.clear();
		});

		backButton.setOnAction(event -> {
			showConnectScene();
		});

		// Layout
		HBox inputRow = new HBox(10);
		HBox.setHgrow(messageField, Priority.ALWAYS);
		inputRow.getChildren().addAll(messageField, sendButton);

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(15));
		layout.getChildren().addAll(
			titleLabel,
			statusLabel,
			chatArea,
			inputRow,
			backButton
		);

		Scene scene = new Scene(layout, 700, 500);

		primaryStage.setTitle("PeerLink Chat - Host Chat");
		primaryStage.setScene(scene);
	}

	public static void main(String[] args) {
		launch(args);
	}
}