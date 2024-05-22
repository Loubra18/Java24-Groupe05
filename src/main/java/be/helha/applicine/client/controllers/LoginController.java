package be.helha.applicine.client.controllers;

import be.helha.applicine.common.models.Client;
import be.helha.applicine.common.models.Session;
import be.helha.applicine.client.views.LoginViewController;
import be.helha.applicine.common.models.request.CheckLoginRequest;
import be.helha.applicine.common.models.request.ClientEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the Login window.
 */
public class LoginController extends Application implements LoginViewController.LoginViewListener, ServerRequestHandler.OnClientEventReceived {
    /**
     * The parent controller of the Login window used to navigate between windows.
     * @see MasterApplication
     */
    private MasterApplication parentController;
    private final FXMLLoader fxmlLoader = new FXMLLoader(LoginViewController.getFXMLResource());

    private LoginViewController loginViewController;

    private ServerRequestHandler serverRequestHandler;

    public LoginController(MasterApplication masterApplication) {
        this.parentController = masterApplication;
    }

    /**
     * Starts the Login window.
     * @param stage the stage of the Login window.
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        serverRequestHandler = ServerRequestHandler.getInstance();
        LoginViewController.setStageOf(fxmlLoader);
        loginViewController = fxmlLoader.getController();
        loginViewController.setListener(this);
        parentController.setCurrentWindow(LoginViewController.getStage());
        serverRequestHandler.setOnViewablesReceivedListener(this);
    }

    /**
     * Launches the Login window.
     * @param args the arguments of the application.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Handles the input from the user.
     * @param username
     * @param password
     * @return true if the input is correct, false otherwise.
     */
    @Override
    public boolean inputHandling(String username, String password) {
        if(Objects.equals(username, "admin") && Objects.equals(password, "admin")){
            toAdmin();
            return true;
        }
        CheckLoginRequest checkLoginRequest = new CheckLoginRequest(username, password);
        try {
            serverRequestHandler.sendRequest(checkLoginRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Ask the master controller to navigate to the client window.
     */
    public void toClient() {
        parentController.toClient();
    }

    /**
     * Ask the master controller to navigate to the admin window.
     */
    public void toAdmin(){
        parentController.toAdmin();
    }

    public void toClientWithoutLogin(){
        parentController.toClient();
    }


    @Override
    public void toRegistration(){
        parentController.toRegistration();
    }

    @Override
    public void onClientEvenReceived(ClientEvent clientEvent) {
        if (clientEvent instanceof CheckLoginRequest) {
            CheckLoginRequest checkLoginRequest = (CheckLoginRequest) clientEvent;
            Session session = parentController.getSession();
            session.setCurrentClient(checkLoginRequest.getClient());
            session.setLogged(true);
            Platform.runLater(this::toClient);
        }
    }
}