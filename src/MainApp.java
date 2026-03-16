import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) {
		Label label = new Label("PeerLink Chat");
		StackPane root = new StackPane(label);
		Scene scene = new Scene(root, 700, 500);

		stage.setTitle("PeerLink Chat");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}