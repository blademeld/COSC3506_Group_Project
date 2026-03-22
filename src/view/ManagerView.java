package view;

import java.io.IOException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ManagerView {
	private String portNumber;

	public ManagerView(String portNumber) {
		this.portNumber = portNumber;
	}

	public Scene getScene() {
		Label titleLabel = new Label();
		titleLabel.setText("PeerLink Chat - Host Chat Window");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		Label statusLabel = new Label();
		statusLabel.setText("Hosting session");

		Label portLabel = new Label();
		portLabel.setText("Listening on port: " + portNumber);

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

			// Prevent sending empty messages
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
			try {
				MainApp.showConnectScene();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		HBox inputRow = new HBox(10);
		HBox.setHgrow(messageField, Priority.ALWAYS);
		inputRow.getChildren().addAll(messageField, sendButton);

		// Layout for the host chat scene
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(15));
		layout.getChildren().addAll(
				titleLabel,
				statusLabel,
				portLabel,
				chatArea,
				inputRow,
				backButton
		);

		return new Scene(layout, 700, 500);
	}
}