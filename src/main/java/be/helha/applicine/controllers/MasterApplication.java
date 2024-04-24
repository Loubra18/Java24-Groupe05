package be.helha.applicine.controllers;

import be.helha.applicine.FileMangement.FileManager;
import be.helha.applicine.controllers.managercontrollers.ManagerController;
import be.helha.applicine.dao.ClientsDAO;
import be.helha.applicine.dao.MovieDAO;
import be.helha.applicine.dao.RoomDAO;
import be.helha.applicine.dao.impl.ClientsDAOImpl;
import be.helha.applicine.dao.impl.MovieDAOImpl;
import be.helha.applicine.dao.impl.RoomDAOImpl;
import be.helha.applicine.database.ApiRequest;
import be.helha.applicine.models.Client;
import be.helha.applicine.models.Session;
import be.helha.applicine.views.WaitingWindowViewController;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * It is the main class of the application.
 * It is responsible for starting the application and switching between windows.
 */
public class MasterApplication extends Application {
    /**
     * The current opened window of the application.
     */
    private Window currentWindow;

    private Session session;
    public MasterApplication(){
        session = new Session();
    }
    /**client
     * Setter for the current window.
     * @param currentWindow The window to set as the current window.
     */
    public void setCurrentWindow(Window currentWindow) {
        this.currentWindow = currentWindow;
        System.out.println("Current window set to: " + currentWindow);
    }

    /**
     * Start point of the application.
     */
    @Override
    public void start(Stage stage) throws Exception {
        try {
            WaitingWindowViewController waitingWindowViewController = new WaitingWindowViewController();
            Frame waitingWindow = waitingWindowViewController.getWaitingWindow();
            waitingWindow.setVisible(true);

            initializeAppdata();

            setCurrentWindow(stage);
            toClient();

            waitingWindow.dispose();
        }catch (Exception e){
            popUpAlert("Erreur lors de l'initialisation de l'application. Essayez de relancer l'application ou contactez un administrateur.");
            closeAllWindows();
        }
    }

    private void initializeAppdata(){
        FileManager.createDataFolder();
        MovieDAO movieDAO = new MovieDAOImpl();
        try {
            if (movieDAO.isMovieTableEmpty()) {
                ApiRequest apiRequest = new ApiRequest();
                apiRequest.fillDatabase();
            }
            RoomDAO roomDAO = new RoomDAOImpl();
            if (roomDAO.isRoomTableEmpty()) {
                roomDAO.fillRoomTable();
            }
        }catch (SQLException e){
            popUpAlert("Erreur lors de la récupération des données, veuillez réessayer plus tard.");
            closeAllWindows();
            toLogin();
        }
    }

    /**
     * Switch to the login window and close the currentWindow.
     */
    public void toLogin() {
        try {
            currentWindow.hide();
            LoginController loginController = new LoginController(this);
            loginController.start(new Stage());
        }catch (IOException e){
            popUpAlert("Erreur de redirection de page vers le login, veuillez redémarrez l'application.");
            closeAllWindows();
        }
    }

    /**
     * Switch to the client window and close the currentWindow.
     *
     */
    public void toClient() {
        if(currentWindow != null)
            currentWindow.hide();
        try {
            ClientController clientController = new ClientController(this);
            clientController.start(new Stage());
        }catch (Exception e){
            popUpAlert("Erreur lors de l'ouverture de la fenêtre client, essayez de vous reconnecter.");
            toLogin();
        }
    }

    //Dites moi s'il faut un exception ici
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
     * @throws Exception
     */
    public void toAdmin(){
        try {
            currentWindow.hide();
            ManagerController managerController = new ManagerController(this);
            managerController.start(new Stage());
        }catch (SQLException e) {
            popUpAlert("Erreur lors de la récupération des données, veuillez réessayer plus tard.");
            closeAllWindows();
            toLogin();
        }catch (IOException e){
            popUpAlert("Erreur lors de l'ouverture de la fenêtre, veuillez réessayer plus tard.");
            closeAllWindows();
            toLogin();
        }
    }

    /**
     * Switch to the client account window and close the currentWindow.
     * @throws Exception
     */
    public void toClientAccount(){
        currentWindow.hide();
        ClientAccountApplication clientAccountApplication = new ClientAccountApplication(this);
        clientAccountApplication.start(new Stage());
    }
    public void toRegistration() {
        currentWindow.hide();
        RegistrationController registrationController = new RegistrationController(this);
        registrationController.start(new Stage());
    }

    public static void main(String[] args) {
        launch();
    }
    public Session getSession() {
        return session;
    }

    /**
     * It shows an alert with the given parameters, and returns true if the user clicks on OK.
     *
     * @param alertType
     * @param title
     * @param headerText
     * @param contentText
     * @return
     */
    //pour éviter de répéter le code de l'alerte, je crée une méthode showAlert dans MasterApplication
    public boolean showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        //alert.showAndWait();
        Optional<ButtonType> result = alert.showAndWait();
        //Si l'utilisateur clique sur OK, la méthode retourne true
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void popUpAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Erreur", message, "Veuillez réessayer plus tard");
    }
}
