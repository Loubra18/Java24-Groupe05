package be.helha.applicine.client.views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * A class to display alerts to the user.
 * The class is used to display error, information and confirmation messages.
 */
public class AlertViewController {
    /**
     * Displays an error message to the user.
     * @param message the message to display.
     * @return true if the user clicked on the OK button, false otherwise.
     */
    public static boolean showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Displays an information message to the user.
     * @param message the message to display.
     * @return true if the user clicked on the OK button, false otherwise.
     */
    public static boolean showInfoMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Information");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Displays a confirmation message to the user.
     * @param message the message to display.
     * @return true if the user clicked on the OK button, false otherwise.
     */
    public static boolean showConfirmationMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
