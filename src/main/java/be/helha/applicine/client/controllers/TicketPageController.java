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

    /**
     * Starts the TicketPageController. Get the sessions for the movie and display them in the view.
     * @param stage the stage to display the view in.
     */
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
            AlertViewController.showErrorMessage("Erreur lors de l'affichage de vos tickets : " + e.getMessage());
        } catch (SQLException e) {
            AlertViewController.showErrorMessage("Erreur lors de la connection à la base de donnée : " + e.getMessage());
            parentController.toClient();
        }
    }

    /**
     * Constructor for the TicketPageController class.
     * @param masterApplication the parent controller (MasterApplication).
     */
    public TicketPageController(MasterApplication masterApplication) {
        this.parentController = masterApplication;
        Session currentSession = parentController.getSession();
        Client client = currentSession.getCurrentClient();
        this.clientID = client.getId();
    }

    /**
     * Creates x tickets for the selected session.
     * @param numberOfTickets the number of tickets to create.
     * @param ticketType the type of ticket to create.
     */
    public void createTickets(int numberOfTickets, String ticketType) {
        for (int i = 0; i < numberOfTickets; i++) {
            Session currentSession = parentController.getSession();
            Client client = currentSession.getCurrentClient();
            clientID = client.getId();
            Ticket ticket = new Ticket(ticketType,selectedSession,client);
            Object response = null;
            if (response.equals("TICKET_CREATED")) {
                System.out.println("Ticket created successfully");
            } else {
                System.out.println("Error creating ticket: " + response);
            }
        }
    }

    /**
     * Creates tickets for the selected session based on the number of tickets and the type of the ticket.
     * @param sessionId the ID of the selected session.
     * @param normalTickets the number of normal tickets to create.
     * @param seniorTickets the number of senior tickets to create.
     * @param minorTickets the number of minor tickets to create.
     * @param studentTickets the number of student tickets to create.
     */
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

    /**
     * Gets the session with the given ID.
     * @param sessionId the ID of the session to get.
     */
    @Override
    public void onSessionSelected(String sessionId) {
        // Assume sessionId is a valid integer string that represents the ID of the session
        try {
            int id = Integer.parseInt(sessionId);
            GetSessionByIdRequest request = new GetSessionByIdRequest(id);
            selectedSession = null;
        } catch (NumberFormatException e) {
            AlertViewController.showInfoMessage("Invalid session ID: " + sessionId);
        }
    }

    /**
     * Sets the movie to display in the view.
     * @param viewable the movie to display.
     */
    public void setViewable(Viewable viewable) {
        this.viewable = viewable;
    }

    /**
     * Redirects the user to the client page.
     */
    public void closeWindow() {
        parentController.toClient();

    }

    /**
     * Gets the sessions for the given movie.
     * @param movie the movie to get the sessions for.
     * @return a list of sessions for the movie.
     */
    public List<MovieSession> getSessionsForMovie(Viewable movie) throws SQLException {
        GetSessionByMovieId request = new GetSessionByMovieId(movie.getId());
        try {
            return null;
        } catch (Exception e) {
            System.out.println("Error getting sessions for movie: " + e.getMessage());
            return null;
        }
    }
}