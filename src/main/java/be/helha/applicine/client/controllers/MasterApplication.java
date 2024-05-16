package be.helha.applicine.client.controllers;

import be.helha.applicine.server.FileManager;
import be.helha.applicine.client.controllers.managercontrollers.ManagerController;
import be.helha.applicine.server.dao.ClientsDAO;
import be.helha.applicine.server.dao.MovieDAO;
import be.helha.applicine.server.dao.RoomDAO;
import be.helha.applicine.server.dao.impl.ClientsDAOImpl;
import be.helha.applicine.server.dao.impl.MovieDAOImpl;
import be.helha.applicine.server.dao.impl.RoomDAOImpl;
import be.helha.applicine.server.database.ApiRequest;
import be.helha.applicine.common.models.Session;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
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

    private ServerRequestHandler serverRequestHandler;

    public MasterApplication() throws IOException {
        session = new Session();
    }

    /**
     * client
     * Setter for the current window.
     *
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
            serverRequestHandler = new ServerRequestHandler();
        } catch (IOException e) {
            popUpAlert("Erreur lors de la connexion au serveur");
            return;
        }
        setCurrentWindow(stage);
        toClient();
    }

    private void initializeAppdata() {
        FileManager.createDataFolder();

        MovieDAO movieDAO = new MovieDAOImpl();

        ClientsDAO clientsDAO = new ClientsDAOImpl();

        if (movieDAO.isMovieTableEmpty()) {
            ApiRequest apiRequest = new ApiRequest();
            apiRequest.fillDatabase();
        }

        if (clientsDAO.isClientTableEmpty()) {
            clientsDAO.createClient("John Doe", "john.doe@example.com", "johndoe", "motdepasse");
        }

        RoomDAO roomDAO = new RoomDAOImpl();
        if (roomDAO.isRoomTableEmpty()) {
            roomDAO.fillRoomTable();
        }
    }

    /**
     * Switch to the login window and close the currentWindow.
     */
    public void toLogin() throws IOException {
        currentWindow.hide();
        LoginController loginController = new LoginController(this);
        loginController.start(new Stage());
    }

    /**
     * Switch to the client window and close the currentWindow.
     *
     * @throws Exception
     */
    public void toClient() {
        System.out.println("current" + currentWindow);
        if (currentWindow != null)
            currentWindow.hide();
        try {
            ClientController clientController = new ClientController(this);
            clientController.start(new Stage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            popUpAlert("Erreur lors de l'ouverture de la fenêtre");
        }
    }

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
     *
     * @throws Exception
     */
    public void toAdmin() throws Exception {
        currentWindow.hide();
        ManagerController managerController = new ManagerController(this);
        managerController.start(new Stage());
    }

    /**
     * Switch to the client account window and close the currentWindow.
     *
     * @throws Exception
     */
    public void toClientAccount() throws Exception {
        currentWindow.hide();
        ClientAccountApplication clientAccountApplication = new ClientAccountApplication(this);
        clientAccountApplication.start(new Stage());
    }

    public void toRegistration() throws IOException {
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

    public void closeCurrentWindow() {
        currentWindow.hide();
    }

    public void toTicketPage() throws Exception {
        TicketPageController ticketPageController = new TicketPageController(this);
        ticketPageController.start(new Stage());
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

    public ServerRequestHandler getServerRequestHandler() {
        return serverRequestHandler;
    }
}

