package be.helha.applicine.client.controllers;

import be.helha.applicine.client.network.ServerRequestHandler;
import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.Session;
import be.helha.applicine.common.models.Viewable;
import be.helha.applicine.client.views.ClientViewController;
import be.helha.applicine.client.views.MoviePaneViewController;
import be.helha.applicine.common.models.request.*;
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
public class ClientController extends Application implements ClientViewController.ClientViewListener, MoviePaneViewController.MoviePaneViewListener, ServerRequestHandler.Listener, RequestVisitor {
    private final MasterApplication parentController;
    private ClientViewController clientViewController;

    private ServerRequestHandler serverRequestHandler;

    public ClientController(MasterApplication masterApplication) throws IOException {
        this.parentController = masterApplication;
    }

    /**
     * starts the client view.
     *
     * @param clientWindow
     * @throws Exception
     */
    public void start(Stage clientWindow) throws Exception {
        try {
            serverRequestHandler = ServerRequestHandler.getInstance();
            serverRequestHandler.setListener(this);
            getMovies();
            FXMLLoader clientFXML = new FXMLLoader(ClientViewController.getFXMLResource());
            clientViewController = new ClientViewController();
            clientFXML.setController(clientViewController);
            clientViewController.setListener(this);
            ClientViewController.setStageOf(clientFXML);
            setCurrentWindow(clientViewController.getStage());

            Session session = parentController.getSession();
            boolean isLogged = session.isLogged();
            clientViewController.updateButtonText(isLogged);
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Erreur lors de l'affichage de la fenêtre client: " + e.getMessage());
            parentController.toLogin();
        }
    }

    private void getMovies() throws IOException {
        GetViewablesRequest request = new GetViewablesRequest();
        serverRequestHandler.sendRequest(request);
    }

    /**
     * Add movies to the client view.
     *
     * @param controller
     * @param movies
     */
    public void addMovies(ClientViewController controller, List<Viewable> movies) {
        String moviesBugged = "";
        for (Viewable movie : movies) {
            try {
                controller.addMovie(movie, this);
            }catch (IOException e){
                moviesBugged = "Problème de chargement du/des film(s) suivant(s):\n";
                moviesBugged += movie.getTitle() + "\n";
            }
        }
        if (!moviesBugged.isEmpty())
            AlertViewController.showErrorMessage(moviesBugged);
    }

    /**
     * Switches to the login page.
     *
     * @throws Exception when the login page cannot be displayed.
     */
    @Override
    @FXML
    public void toLoginPage() throws Exception {
        parentController.toLogin();
    }

    /**
     * Setter for the current window.
     *
     * @param currentWindow
     */
    @Override
    public void setCurrentWindow(Window currentWindow) {
        parentController.setCurrentWindow(currentWindow);
    }

    /**
     * Switches to the client account page.
     *
     * @throws Exception
     */
    @Override
    @FXML
    public void toClientAccount() throws Exception {
        System.out.println("Account button clicked, je vais afficher les informations du compte");
        parentController.toClientAccount();
    }

    @Override
    public void onBuyTicketClicked(Viewable movie) throws Exception {
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
    public void onResponseReceive(ClientEvent clientEvent) {
        clientEvent.dispatchOn(this);
    }

    @Override
    public void onConnectionLost() {

    }

    @Override
    public void visit(GetViewablesRequest getViewablesRequest) {
        System.out.println("Received movies: " + getViewablesRequest.getViewables());
        Platform.runLater(() -> {
            List<Viewable> movies = getViewablesRequest.getViewables();
            addMovies(clientViewController, movies);
        });
    }

}