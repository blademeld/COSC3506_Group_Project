import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ConnectChatView
{
	private String ipAddress;
	private String portNumber;

	public ConnectChatView(String ipAddress, String portNumber)
	{
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}

	public Scene getScene()
	{
		Label titleLabel = new Label();
		titleLabel.setText("PeerLink Chat - Peer Chat Window");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		Label statusLabel = new Label();
		statusLabel.setText("Connected to peer");

		Label ipLabel = new Label();
		ipLabel.setText("Peer IP: " + ipAddress);

		Label portLabel = new Label();
		portLabel.setText("Peer Port: " + portNumber);

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

		sendButton.setOnAction(event ->
		{
			String text = messageField.getText();

			// Prevent user from sending empty messages
			if (text == null)
			{
				return;
			}
			String trimmedText = text.trim();
			if (trimmedText.isEmpty())
			{
				return;
			}

			chatArea.appendText("You: " + trimmedText + "\n");
			messageField.clear();
		});

		backButton.setOnAction(event ->
		{
			MainApp.showConnectScene();
		});

		HBox inputRow = new HBox(10);
		HBox.setHgrow(messageField, Priority.ALWAYS);
		inputRow.getChildren().addAll(messageField, sendButton);

		// Layout for chat scene
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(15));
		layout.getChildren().addAll(
				titleLabel,
				statusLabel,
				ipLabel,
				portLabel,
				chatArea,
				inputRow,
				backButton
		);

		return new Scene(layout, 700, 500);
	}
}