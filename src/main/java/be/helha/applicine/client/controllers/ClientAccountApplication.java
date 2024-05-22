package be.helha.applicine.client.controllers;

import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.Client;
import be.helha.applicine.common.models.Session;
import be.helha.applicine.common.models.Ticket;
import be.helha.applicine.client.views.ClientAccountControllerView;
import be.helha.applicine.common.models.request.ClientEvent;
import be.helha.applicine.common.models.request.GetTicketByClientRequest;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The ClientAccountApplication class is the controller of the client account window.
 * It allows the client to see his account information and his tickets.
 * It also allows the client to return to the client window or to the login window.
 * It communicates with the server to get the tickets of the client.
 * It uses the ClientAccountControllerView to display the client account window.
 * It uses the ServerRequestHandler to communicate with the server.
 * It uses the MasterApplication to change the window and the controller of the view.
 * It implements the ClientAccountControllerView.ClientAccountListener to listen to the client account view.
 *
 * @implSpec The ClientAccountApplication class is a subclass of Application.
 */
public class ClientAccountApplication extends Application implements ClientAccountControllerView.ClientAccountListener, ServerRequestHandler.OnClientEventReceived {
    //renvoie le fichier FXML de la vue ClientAccount
    private final FXMLLoader fxmlLoader = new FXMLLoader(ClientAccountControllerView.getFXMLResource());

    //permet de communiquer avec le parentController (MasterApplication) pour changer de fenêtre et de contrôleur de vue.
    private MasterApplication parentController;

    private ServerRequestHandler serverRequestHandler;

    /**
     * Constructor of the ClientAccountApplication
     *
     * @param masterApplication the parent controller (MasterApplication).
     * @throws IOException if the server request handler can't be created.
     */
    public ClientAccountApplication(MasterApplication masterApplication) throws IOException {
        this.parentController = masterApplication;
        this.serverRequestHandler = ServerRequestHandler.getInstance();
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
     * @return the client account logged in.
     */
    @Override
    public Client getClientAccount() {
        Session session = parentController.getSession();
        return session.getCurrentClient();
    }

    /**
     * Starts the client account window by setting the stage of the fxmlLoader and initializing the client account page.
     *
     * @param stage the stage of the client account window.
     */
    @Override
    public void start(Stage stage) {
        try {
            ClientAccountControllerView.setStageOf(fxmlLoader, this);
            ClientAccountControllerView clientAccountControllerView = fxmlLoader.getController();
            clientAccountControllerView.setListener(this);

            parentController.setCurrentWindow(clientAccountControllerView.getAccountWindow());

            clientAccountControllerView.initializeClientAccountPage(getClientAccount());
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Problème de chargement de la page, veuillez réessayer plus tard. Contactez un administrateur si le problème se maintient.");
            parentController.toClient();
        }
    }

    /**
     * Gets the tickets of a client by his id.
     *
     * @param id the id of the client.
     * @return the list of tickets of the client.
     */
    private void getTicketsByClient(int id) {
        try {
            serverRequestHandler.sendRequest(new GetTicketByClientRequest(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds tickets to the client account page.
     *
     * @param tickets the list of tickets to add.
     */
    public void addTickets(List<Ticket> tickets, ClientAccountControllerView clientAccountControllerView) {
        List<Ticket> ticketsWithNullSession = new ArrayList<>();
        for (Ticket ticket : tickets) {
            try {
                clientAccountControllerView.addTicket(ticket);
            } catch (IOException e) {
                ticketsWithNullSession.add(ticket);
            }
        }
    }

    @Override
    public void onClientEvenReceived(ClientEvent clientEvent) {
        if (clientEvent instanceof GetTicketByClientRequest) {
            GetTicketByClientRequest getTicketByClientRequest = (GetTicketByClientRequest) clientEvent;
            List<Ticket> tickets = getTicketByClientRequest.getTickets();
            ClientAccountControllerView clientAccountControllerView = fxmlLoader.getController();
            addTickets(tickets, clientAccountControllerView);
        }
    }
}
