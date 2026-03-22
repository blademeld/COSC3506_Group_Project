package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
	private static Stage primaryStage;

	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		try {
			showConnectScene();
		} catch (IOException e) {
			e.printStackTrace();
		}
		primaryStage.show();
	}

	// Main connection scene where users can choose to host a session or connect to a peer
	public static void showConnectScene() throws IOException {
		FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/MainApp.fxml"));
		Scene scene = new Scene(loader.load(), 700, 500);
		primaryStage.setTitle("PeerLink Chat");
		primaryStage.setScene(scene);
	}

	// Chat scene for hosting a session
	public static void showHostChatScene(String portNumber) {
		// Once networking is implemented , the host's IP address will be auto-grabbed and displayed here.
		// For now, we will just display the port number.
		ManagerView managerView = new ManagerView(portNumber);
		Scene scene = managerView.getScene();
		primaryStage.setTitle("PeerLink Chat - Manager Chat");
		primaryStage.setScene(scene);
	}

	// Chat scene for connecting to a peer
	public static void showConnectChatScene(String ipAddress, String portNumber) {
		PeerView peerView = new PeerView(ipAddress, portNumber);
		Scene scene = peerView.getScene();
		primaryStage.setTitle("PeerLink Chat - Peer Chat");
		primaryStage.setScene(scene);
	}

	// Entry point of the application
	public static void main(String[] args) {
		launch(args);
	}
}