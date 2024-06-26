package be.helha.applicine.client.controllers;

import be.helha.applicine.client.network.ServerRequestHandler;
import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.Client;
import be.helha.applicine.common.models.Session;
import be.helha.applicine.common.models.Ticket;
import be.helha.applicine.client.views.ClientAccountControllerView;
import be.helha.applicine.common.models.request.ClientEvent;
import be.helha.applicine.common.models.request.ErrorMessage;
import be.helha.applicine.common.models.request.GetTicketByClientRequest;
import be.helha.applicine.common.models.request.RequestVisitor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the main class for the client account interface application.
 */
public class ClientAccountApplication extends Application implements ClientAccountControllerView.ClientAccountListener, ServerRequestHandler.Listener, RequestVisitor {

    private final FXMLLoader fxmlLoader = new FXMLLoader(ClientAccountControllerView.getFXMLResource());

    private MasterApplication parentController;

    private ServerRequestHandler serverRequestHandler;


    public ClientAccountApplication(MasterApplication masterApplication) {
        try {
            this.serverRequestHandler = ServerRequestHandler.getInstance();
            this.serverRequestHandler.addListener(this);
            this.parentController = masterApplication;
        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
            AlertViewController.showErrorMessage("Problème de chargement de la page, veuillez réésayer plus tard. Contactez un administrateur si le problème se maintient.");
            parentController.closeAllWindows();
            parentController.toClient();
        }
    }


    /**
     * Permit to close the client account window and return to the client window.
     */
    @Override
    public void toClientSide() {
        parentController.toClient();
    }

    /**
     * Permit to close the client account window and return to the login window.
     */
    @Override
    public void toClientAccount() {
        parentController.toClientAccount();
    }

    /**
     * Permit to get the client account from the actual session.
     *
     * @return the client account.
     */
    @Override
    public Client getClientAccount() {
        Session session = parentController.getSession();
        return session.getCurrentClient();
    }

    /**
     * starts the client account window by setting the stage of the fxmlLoader and initializing the client account page.
     *
     * @param stage The stage of the application.
     */
    @Override
    public void start(Stage stage) {
        try {
            ClientAccountControllerView.setStageOf(fxmlLoader, this);
            ClientAccountControllerView clientAccountControllerView = fxmlLoader.getController();
            clientAccountControllerView.setListener(this);

            parentController.setCurrentWindow(clientAccountControllerView.getAccountWindow());

            //initialise la page du client account (affiche les tickets et les informations du client)
            clientAccountControllerView.initializeClientAccountPage(getClientAccount());
            sendRequestTicketByClient(getClientAccount().getId());
        } catch (Exception e) {
            System.out.println("Error handling client: " + e.getMessage());
            AlertViewController.showErrorMessage("Problème de chargement de la page, veuillez réésayer plus tard. Contactez un administrateur si le problème se maintient.");
            parentController.closeAllWindows();
            parentController.toClient();
        }
    }

    /**
     * Sends a request to the server to get the tickets of a client.
     *
     * @param id
     * @throws IOException
     */
    private void sendRequestTicketByClient(int id) throws IOException {
        serverRequestHandler.sendRequest(new GetTicketByClientRequest(id));
    }

    /**
     * adds tickets to the client account page.
     *
     * @param tickets                     The tickets to add.
     * @param clientAccountControllerView The view of the client account.
     */
    public void addTickets(List<Ticket> tickets, ClientAccountControllerView clientAccountControllerView) {
        List<Ticket> ticketsWithNullSession = new ArrayList<>();
        for (Ticket ticket : tickets) {
            try {
                clientAccountControllerView.addTicket(ticket);
            } catch (Exception e) {
                ticketsWithNullSession.add(ticket);
                System.out.println("Error adding ticket: " + e.getMessage());
            }
        }
    }

    /**
     * Apply the dispatcher and  to the response received.
     *
     * @param response
     */
    @Override
    public void onResponseReceive(ClientEvent response) {
        //en fonction du type de requete, on va réaliser des actions spécifiques
        response.dispatchOn(this);
    }

    /**
     * Called when the connection is lost.
     */
    @Override
    public void onConnectionLost() {
        AlertViewController.showErrorMessage("Connexion perdue avec le serveur, veuillez réessayer plus tard.");
        parentController.closeAllWindows();
        parentController.toLogin();
    }

    /**
     * Send a getTicketByClientRequest to the server.
     *
     * @param request
     */
    @Override
    public void visit(GetTicketByClientRequest request) {
        List<Ticket> tickets = request.getTickets();
        System.out.println("tickets: " + tickets);
        Platform.runLater(() -> addTickets(tickets, fxmlLoader.getController()));
    }

    @Override
    public void visit(ErrorMessage errorMessage) {
        Platform.runLater(() -> {
            AlertViewController.showErrorMessage(errorMessage.getMessage());
        });
    }
}
