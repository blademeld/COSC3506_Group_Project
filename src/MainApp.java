import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) {
		Label titleLabel = new Label("PeerLink Chat");
		Label subtitleLabel = new Label("Server-free peer-to-peer messaging application");

		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(titleLabel, subtitleLabel);

		Scene scene = new Scene(root, 700, 500);

		stage.setTitle("PeerLink Chat");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
