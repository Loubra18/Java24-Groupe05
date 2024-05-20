package be.helha.applicine.client.controllers;

import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.client.controllers.managercontrollers.ManagerController;
import be.helha.applicine.common.models.Session;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * It is the main class of the application.
 * It is responsible for starting the application and switching between windows.
 */
public class MasterApplication extends Application {
    private Window currentWindow;
    private final Session session;

    /**
     * Constructor of the MasterApplication.
     * It initializes the session.
     */
    public MasterApplication() {
        session = new Session();
    }

    /**
     * Setter for the current window.
     * @param currentWindow The window to set as the current window.
     */
    public void setCurrentWindow(Window currentWindow) {
        this.currentWindow = currentWindow;
    }

    /**
     * Starts the application.
     * @param stage The stage of the application.
     */
    @Override
    public void start(Stage stage) {
        setCurrentWindow(stage);
        toClient();
    }

    /**
     * Switch to the login window and close the currentWindow.
     */
    public void toLogin() {
        try {
            closeAllWindows();
            LoginController loginController = new LoginController(this);
            loginController.start(new Stage());
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Erreur lors de l'ouverture de la fenêtre de connexion, veuillez réessayer plus tard.");
            closeAllWindows();
        }
    }

    /**
     * Switch to the client window and close the currentWindow.
     */
    public void toClient() {
        try {
            if (currentWindow != null) {
                closeAllWindows();
            }
            ClientController clientController = new ClientController(this);
            clientController.start(new Stage());
        } catch (Exception e){
            AlertViewController.showErrorMessage("Erreur lors de l'ouverture de la fenêtre client, veuillez réessayer plus tard.");
            toLogin();
        }
    }

    /**
     * Close all the windows.
     */
    public void closeAllWindows() {
        List<Window> stages = new ArrayList<>(Window.getWindows());
        for (Window window : stages) {
            if (window instanceof Stage && window.isShowing()) {
                ((Stage) window).close();
            }
        }
    }

    /**
     * Switch to the manager window and close the currentWindow.
     */
    public void toAdmin() {
        try {
            closeAllWindows();
            ManagerController managerController = new ManagerController(this);
            managerController.start(new Stage());
        } catch (SQLException | IOException | ClassNotFoundException e) {
            AlertViewController.showErrorMessage("Erreur lors de l'ouverture de la fenêtre manager, veuillez réessayer plus tard.");
            closeAllWindows();
            toLogin();
        }
    }

    /**
     * Switch to the client account window and close the currentWindow.
     */
    public void toClientAccount() {
        try {
            closeAllWindows();
            ClientAccountApplication clientAccountApplication = new ClientAccountApplication(this);
            clientAccountApplication.start(new Stage());
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Erreur lors de l'ouverture de la fenêtre de compte client, tentez de vous reconnecter. Si l'erreur persiste contactez un administrateur réseaux.");
            closeAllWindows();
            toLogin();
        }
    }

    /**
     * Switch to the registration window and close the currentWindow.
     */
    public void toRegistration() {
        closeAllWindows();
        RegistrationController registrationController = new RegistrationController(this);
        registrationController.start(new Stage());
    }

    /**
     * Switch to the movie window and close the currentWindow.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Getter for the session.
     * @return The session.
     */
    public Session getSession() {
        return session;
    }
}