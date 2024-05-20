package be.helha.applicine.client.controllers;

import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.*;
import be.helha.applicine.common.models.request.CreateTicketRequest;
import be.helha.applicine.common.models.request.GetSessionByIdRequest;
import be.helha.applicine.common.models.request.GetSessionByMovieId;
import be.helha.applicine.server.dao.SessionDAO;
import be.helha.applicine.client.views.TicketShoppingViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TicketPageController extends Application implements TicketShoppingViewController.TicketViewListener {

    private final MasterApplication parentController;
    private int clientID;
    private Viewable viewable;
    private SessionDAO sessionDAO;
    private MovieSession selectedSession;

    private ServerRequestHandler serverRequestHandler;

    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TicketShoppingViewController.class.getResource("TicketShoppingView.fxml"));
            Scene scene;
            serverRequestHandler = ServerRequestHandler.getInstance();
            scene = new Scene(fxmlLoader.load());
            stage.setTitle("Ticket Shopping");
            stage.setScene(scene);
            stage.show();
            TicketShoppingViewController controller = fxmlLoader.getController();
            controller.setListener(this);
            controller.setMovie(viewable);
            System.out.println("Viewable: " + viewable + " ID: " + viewable.getId() + " Title: " + viewable.getTitle());

            // Récupérer les séances du film et les définir dans la vue.
            List<MovieSession> sessions = getSessionsForMovie(viewable);
            if (sessions.isEmpty()) {
                // Afficher un message à l'utilisateur
                AlertViewController.showInfoMessage("No sessions available for this movie.");
                stage.close();
            } else {
                controller.setSessions(sessions);
            }
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Error loading ticket shopping view: " + e.getMessage());
        } catch (SQLException e) {
            AlertViewController.showErrorMessage("Error getting sessions for movie: " + e.getMessage());
            parentController.closeAllWindows();
            parentController.toClient();
        }
    }

    public TicketPageController(MasterApplication masterApplication) {
        this.parentController = masterApplication;
        Session currentSession = parentController.getSession();
        Client client = currentSession.getCurrentClient();
        this.clientID = client.getId();
    }

    public void createTickets(int numberOfTickets, String ticketType) {
        for (int i = 0; i < numberOfTickets; i++) {
            Session currentSession = parentController.getSession();
            Client client = currentSession.getCurrentClient();
            clientID = client.getId();
            Ticket ticket = new Ticket(ticketType,selectedSession,client);
            Object response = serverRequestHandler.sendRequest(new CreateTicketRequest(ticket));
            if (response.equals("TICKET_CREATED")) {
                System.out.println("Ticket created successfully");
            } else {
                System.out.println("Error creating ticket: " + response);
            }
        }
    }

    @Override
    public void buyTickets(String sessionId, int normalTickets, int seniorTickets, int minorTickets, int studentTickets) {
        onSessionSelected(sessionId); //Session jamais null sinon le prog plante dans la vue déjà ==> supp fonction??
        if (selectedSession == null) {
            System.out.println("No session selected");
            return;
        }
        createTickets(normalTickets, "normal");
        createTickets(seniorTickets, "senior");
        createTickets(minorTickets, "child");
        createTickets(studentTickets, "student");
    }

    @Override
    public void onSessionSelected(String sessionId) {
        // Assume sessionId is a valid integer string that represents the ID of the session
        try {
            int id = Integer.parseInt(sessionId);
            GetSessionByIdRequest request = new GetSessionByIdRequest(id);
            selectedSession = serverRequestHandler.sendRequest(request);
        } catch (NumberFormatException e) {
            System.out.println("Invalid session ID: " + sessionId);
        }
    }

    public void setViewable(Viewable viewable) {
        this.viewable = viewable;
    }

    public void closeWindow() {
        parentController.toClient();
    }

    public List<MovieSession> getSessionsForMovie(Viewable movie) throws SQLException {
        System.out.println("Getting sessions for movie: " + movie.getId());
        GetSessionByMovieId request = new GetSessionByMovieId(movie.getId());
        try {
            return serverRequestHandler.sendRequest(request);
        } catch (Exception e) {
            System.out.println("Error getting sessions for movie: " + e.getMessage());
            return null;
        }
    }
}