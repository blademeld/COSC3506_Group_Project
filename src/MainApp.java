import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
	private static Stage primaryStage;

	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		showConnectScene();
		primaryStage.show();
	}

	// Main connection scene where users can choose to host a session or connect to a peer
	public static void showConnectScene() {
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
		instructionLabel.setText("Host Session uses this machine's port.\n"
				+ "Connect to Peer uses the host IP address and port.");

		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red;");

		hostButton.setOnAction(event -> {
			String portNumber = portField.getText();
			String validationError = validateHostPort(portNumber);

			if (validationError != null) {
				errorLabel.setText(validationError);
				return;
			}

			String trimmedPort = portNumber.trim();
			errorLabel.setText("");
			showHostChatScene(trimmedPort);
		});

		connectButton.setOnAction(event -> {
			String ipAddress = ipField.getText();
			String portNumber = portField.getText();
			String validationError = validateConnectInput(ipAddress, portNumber);

			if (validationError != null) {
				errorLabel.setText(validationError);
				return;
			}

			String trimmedIp = ipAddress.trim();
			String trimmedPort = portNumber.trim();

			errorLabel.setText("");
			showConnectChatScene(trimmedIp, trimmedPort);
		});

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
				instructionLabel,
				errorLabel
		);

		Scene scene = new Scene(root, 700, 500);
		primaryStage.setTitle("PeerLink Chat");
		primaryStage.setScene(scene);
	}

	// Helper method to validate port number for hosting a session
	private static String validateHostPort(String portNumber) {
		if (portNumber == null || portNumber.trim().isEmpty()) {
			return "Please enter a port number.";
		}

		String trimmedPort = portNumber.trim();
		return validatePortNumber(trimmedPort);
	}

	// Helper method to validate both IP address and port number for the connect scene
	private static String validateConnectInput(String ipAddress, String portNumber) {
		if (ipAddress == null || ipAddress.trim().isEmpty()) {
			return "Please enter a host IP address and port number.";
		}

		if (portNumber == null || portNumber.trim().isEmpty()) {
			return "Please enter a port number.";
		}

		String trimmedPort = portNumber.trim();
		return validatePortNumber(trimmedPort);
	}

	// Helper method to validate that the port number is numeric and within valid range
	private static String validatePortNumber(String trimmedPort) {
		try {
			int port = Integer.parseInt(trimmedPort);

			if (port < 1 || port > 65535) {
				return "Port number must be between 1 and 65535.";
			}
		} catch (NumberFormatException ex) {
			return "Port number must be numeric.";
		}

		return null;
	}

	// Chat scene for hosting a session
	public static void showHostChatScene(String portNumber) {
		// Once networking is implemeented , the host's IP address will be auto-grabbed and displayed here.
		// For now, we will just display the port number.
		HostChatView hostChatView = new HostChatView(portNumber);
		Scene scene = hostChatView.getScene();
		primaryStage.setTitle("PeerLink Chat - Host Chat");
		primaryStage.setScene(scene);
	}

	// Chat scene for connecting to a peer
	public static void showConnectChatScene(String ipAddress, String portNumber) {
		ConnectChatView connectChatView = new ConnectChatView(ipAddress, portNumber);
		Scene scene = connectChatView.getScene();
		primaryStage.setTitle("PeerLink Chat - Peer Chat");
		primaryStage.setScene(scene);
	}

	// Entry point of the application
	public static void main(String[] args) {
		launch(args);
	}
}