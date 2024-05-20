package be.helha.applicine.client.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.URL;

/**
 * This class is the controller for the login view.
 * It handles the user input and sends it to the listener.
 */
public class LoginViewController {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label emptyErrorLabel;
    private LoginViewListener listener;
    private static Stage stage;

    /**
     * Gets the stage of the login view.
     * @return the stage of the login view.
     */
    public static Window getStage() {
        return stage;
    }

    /**
     * Sets the listener for the login view.
     * @param listener the listener for the login view.
     */
    public void setListener(LoginViewListener listener){
        this.listener = listener;
    }

    /**
     * Sets the stage of the login view.
     * @param fxmlLoader the fxml loader.
     * @throws IOException if an I/O error occurs.
     */
    public static void setStageOf(FXMLLoader fxmlLoader) throws IOException {
        stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 1000, 750);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Checks if login successful.
     */
    public void checkLogin() {
        boolean loginSuccessful = listener.inputHandling(username.getText(), password.getText());
        if(loginSuccessful){
            System.out.println("Login successful");
        }else {
            emptyErrorLabel.setText("Incorrect username or password");
        }
    }

    /**
     * Goes to the registration view, to create a new account.
     */
    @FXML
    public void toRegistration() {
        listener.toRegistration();
    }

    /**
     * Goes back to the client view without logging in.
     */
    @FXML
    public void goBackWithoutLogin() throws Exception {
        listener.toClientWithoutLogin();
    }

    /**
     * The listener for the login view.
     */
    public interface LoginViewListener{
        boolean inputHandling(String username, String password);
        void toRegistration();
        void toClientWithoutLogin();
    }

    /**
     * Gets the FXML resource.
     * @return the FXML resource.
     */
    public static URL getFXMLResource() {
        return LoginViewController.class.getResource("loginView.fxml");
    }
}
