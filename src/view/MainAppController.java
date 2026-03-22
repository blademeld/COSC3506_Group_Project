package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainAppController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private Button hostButton;

    @FXML
    private Button connectButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Label footerLabel;

    @FXML
    private void handleHost() {
        String portNumber = portField.getText();
        String validationError = validateHostPort(portNumber);

        if (validationError != null) {
            errorLabel.setText(validationError);
            return;
        }

        String trimmedPort = portNumber.trim();
        errorLabel.setText("");
        MainApp.showHostChatScene(trimmedPort);
    }

    @FXML
    private void handleConnect() {
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
        MainApp.showConnectChatScene(trimmedIp, trimmedPort);
    }

    // Helper method to validate port number for hosting a session
    private String validateHostPort(String portNumber) {
        if (portNumber == null || portNumber.trim().isEmpty()) {
            return "Please enter a port number.";
        }

        String trimmedPort = portNumber.trim();
        return validatePortNumber(trimmedPort);
    }

    // Helper method to validate both IP address and port number for the connect scene
    private String validateConnectInput(String ipAddress, String portNumber) {
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
    private String validatePortNumber(String trimmedPort) {
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
}