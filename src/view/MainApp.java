package view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	private static Stage primaryStage;

	@Override
	public void start(Stage stage) throws IOException {
		primaryStage = stage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainApp.fxml"));
		Scene scene = new Scene(loader.load(), 900, 600);
		primaryStage.setTitle("PeerLink Chat");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// Entry point of the application
	public static void main(String[] args) {
		launch(args);
	}
}