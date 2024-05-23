package be.helha.applicine.client.controllers;

import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.Session;
import be.helha.applicine.common.models.Viewable;
import be.helha.applicine.client.views.ClientViewController;
import be.helha.applicine.client.views.MoviePaneViewController;
import be.helha.applicine.common.models.request.ClientEvent;
import be.helha.applicine.common.models.request.GetViewablesRequest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.util.List;

/**
 * This class is the main class for the client interface application.
 */
public class ClientController extends Application implements ClientViewController.ClientViewListener, MoviePaneViewController.MoviePaneViewListener, ServerRequestHandler.OnClientEventReceived {
    private final MasterApplication parentController;
    private ClientViewController clientViewController;

    private ServerRequestHandler serverRequestHandler;

    public ClientController(MasterApplication masterApplication) {
        this.parentController = masterApplication;
    }

    /**
     * starts the client view.
     * @param clientWindow The stage of the client view.
     */
    public void start(Stage clientWindow) {
        try {
            serverRequestHandler = ServerRequestHandler.getInstance();
            FXMLLoader clientFXML = new FXMLLoader(ClientViewController.getFXMLResource());
            clientViewController = new ClientViewController();
            clientFXML.setController(clientViewController);
            clientViewController.setListener(this);
            ClientViewController.setStageOf(clientFXML);
            setCurrentWindow(clientViewController.getStage());

            Session session = parentController.getSession();
            boolean isLogged = session.isLogged();
            clientViewController.updateButtonText(isLogged);
            serverRequestHandler.setOnViewablesReceivedListener(this);
            GetViewablesRequest getViewablesRequest = new GetViewablesRequest();
            serverRequestHandler.sendRequest(getViewablesRequest);
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Erreur lors de l'affichage de la fenêtre client: " + e.getMessage());
            parentController.toLogin();
        }
    }

    /**
     * Add movies to the client view.
     * @param controller The controller of the client view.
     * @param movies The list of movies to add.
     */
    public void addMovies(ClientViewController controller, List<Viewable> movies) {
        try {
            for (Viewable movie : movies) {
                controller.addMovie(movie, this);
            }
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Erreur lors de l'ajout des films: " + e.getMessage());
        }
    }

    /**
     * Switches to the login page.
     * @throws Exception if there is an error with the fxml file.
     */
    @Override
    @FXML
    public void toLoginPage() throws Exception {
        parentController.toLogin();
    }

    /**
     * Setter for the current window.
     * @param currentWindow The current window.
     */
    @Override
    public void setCurrentWindow(Window currentWindow) {
        parentController.setCurrentWindow(currentWindow);
    }

    /**
     * Switches to the client account page.
     */
    @Override
    @FXML
    public void toClientAccount() {
        System.out.println("Account button clicked, je vais afficher les informations du compte");
        parentController.toClientAccount();
    }

    @Override
    public void onBuyTicketClicked(Viewable movie) {
        Session session = parentController.getSession();
        if (session.isLogged()) {
            TicketPageController ticketPageController = new TicketPageController(parentController);
            ticketPageController.setViewable(movie);
            ticketPageController.start(new Stage());
        } else {
            clientViewController.showNotLoggedInAlert();
        }
    }

    @Override
    public void onClientEvenReceived(ClientEvent clientEvent) {
        Platform.runLater(() -> {
            if (clientEvent instanceof GetViewablesRequest) {
                GetViewablesRequest getViewablesRequest = (GetViewablesRequest) clientEvent;
                List<Viewable> viewables = getViewablesRequest.getViewables();
                addMovies(clientViewController, viewables);
            } else {
                System.out.println("Client event received: " + clientEvent);
            }
        });
    }
}