package be.helha.applicine.client.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.URL;

/**
 * This class is the controller for the registration view.
 */
public class RegistrationViewController {
    private RegistrationViewListener listener;
    private static Stage stage;
    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    private static final URL FXML_RESOURCE = RegistrationViewController.class.getResource("registrationView.fxml");

    /**
     * This method returns the URL of the FXML resource.
     * @return the URL of the FXML resource.
     */
    public static URL getFXMLResource() {
        return FXML_RESOURCE;
    }

    /**
     * This method sets the stage of the registration view.
     * @param fxmlLoader the FXML loader.
     * @throws IOException if an I/O error occurs.
     */
    public static void setStageOf(FXMLLoader fxmlLoader) throws IOException {
        stage = new Stage();
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    /**
     * This method returns the stage of the registration view.
     * @return the stage of the registration view.
     */
    public static Window getStage() {
        return stage;
    }

    /**
     * This method sets the listener of the registration view.
     * @param listener the listener of the registration view.
     */
    public void setListener(RegistrationViewListener listener) {
        this.listener = listener;
    }

    /**
     * This method registers a user.
     * @throws IOException if an I/O error occurs.
     */
    public void register() throws IOException {
        String name = nameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match");
            return;
        }
        if (listener.register(name, username, email, password)) {
            System.out.println("Registration successful");
            listener.toLogin();
        }
    }

    /**
     * This method cancels the registration.
     * @throws IOException if an I/O error occurs.
     */
    public void cancelRegistration() throws IOException {
        listener.cancelRegistration();
    }

    /**
     * This method shows an alert.
     * @param message the message of the alert.
     * @param eMessage the error message of the alert.
     */
    public void showAlert(String message, String eMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error in registration");
        alert.setContentText(message + " : " + eMessage);
        alert.showAndWait();
    }

    /**
     * This interface represents the listener of the registration view.
     */
    public interface RegistrationViewListener {
        boolean register(String name,String username, String email, String password);
        void toLogin() throws IOException;
        void cancelRegistration() throws IOException;
    }
}
