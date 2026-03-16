import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;

	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		showConnectScene();
	}

	private void showConnectScene() {
		Label titleLabel = new Label();
		titleLabel.setText("PeerLink Chat");

		Label subtitleLabel = new Label();
		subtitleLabel.setText("Web3-based P2P Chat Application");

		TextField ipField = new TextField();
		ipField.setPromptText("Host IP address (Connect only)");

		TextField portField = new TextField();
		portField.setPromptText("Port number");

		Button hostButton = new Button();
		hostButton.setText("Host Session");

		Button connectButton = new Button();
		connectButton.setText("Connect to Peer");

		Label instructionLabel = new Label();
		instructionLabel.setText("Host Session uses this machine's port. Connect to Peer uses the host IP address and port.");

		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
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

	public static void main(String[] args) {
		launch(args);
	}
}