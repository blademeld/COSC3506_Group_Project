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
		instructionLabel.setText("Host Session uses this machine's port. Connect to Peer uses the host IP address and port.");

		hostButton.setOnAction(event -> {
			showHostChatScene();
		});

		connectButton.setOnAction(event -> {
			showConnectChatScene();
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
			instructionLabel
		);

		Scene scene = new Scene(root, 700, 500);

		primaryStage.setTitle("PeerLink Chat");
		primaryStage.setScene(scene);
	}

	// Chat scene for hosting a session
	public static void showHostChatScene() {
		HostChatView hostChatView = new HostChatView();
		Scene scene = hostChatView.getScene();

		primaryStage.setTitle("PeerLink Chat - Host Chat");
		primaryStage.setScene(scene);
	}

	// Chat scene for connecting to a peer
	public static void showConnectChatScene() {
		ConnectChatView connectChatView = new ConnectChatView();
		Scene scene = connectChatView.getScene();

		primaryStage.setTitle("PeerLink Chat - Peer Chat");
		primaryStage.setScene(scene);
	}

	public static void main(String[] args) {
		launch(args);
	}
}