import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage stage) {
		Label titleLabel = new Label();
		titleLabel.setText("PeerLink Chat");

		Label subtitleLabel = new Label();
		subtitleLabel.setText("Web3-based P2P Chat Application");

		TextField ipField = new TextField();
		ipField.setPromptText("Enter peer IP address");

		TextField portField = new TextField();
		portField.setPromptText("Enter port");

		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(titleLabel, subtitleLabel, ipField, portField);

		Scene scene = new Scene(root, 700, 500);

		stage.setTitle("PeerLink Chat");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}