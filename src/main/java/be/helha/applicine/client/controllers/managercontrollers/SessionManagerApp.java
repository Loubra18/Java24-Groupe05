package be.helha.applicine.client.controllers.managercontrollers;

import be.helha.applicine.client.controllers.MasterApplication;
import be.helha.applicine.client.controllers.ServerRequestHandler;
import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.request.*;
import be.helha.applicine.common.models.Room;
import be.helha.applicine.common.models.MovieSession;
import be.helha.applicine.common.models.Viewable;
import be.helha.applicine.common.models.exceptions.InvalideFieldsExceptions;
import be.helha.applicine.common.models.exceptions.TimeConflictException;
import be.helha.applicine.client.views.managerviews.SessionManagerViewController;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

//ecoute les changements de la liste de films et de la liste de séances de l'app MovieManagerApp

public class SessionManagerApp extends ManagerController implements SessionManagerViewController.SessionManagerViewListener, InvalidationListener {

    private ManagerController parentController;

    private FXMLLoader sessionManagerFxmlLoader;

    private SessionManagerViewController sessionManagerViewController;

    private ServerRequestHandler serverRequestHandler;

    /**
     * Constructor, super calls ManagerController constructor which initializes the movieDAO and fetches all the movies from the database.
     * It also fetches all the rooms and all the sessions from the database.
     * @throws SQLException if there is an error with the database connection, created in ManagerController.
     */
    public SessionManagerApp(MasterApplication parentController) throws SQLException, IOException, ClassNotFoundException {
        super(parentController);

    }

    /**
     * Constructor, super calls ManagerController constructor which initializes the movieDAO and fetches all the movies from the database.
     * @throws IOException if there is an error with the database connection, created in ManagerController.
     * @throws ClassNotFoundException if there is an error with the database connection, created in ManagerController.
     */
    public SessionManagerApp() throws IOException, ClassNotFoundException, SQLException {
        super();
    }

    /**
     * Starts the session manager view.
     * Movies on the side.
     * @param adminPage the stage of the view.
     */
    @Override
    public void start(Stage adminPage) {
        try {
            this.serverRequestHandler = ServerRequestHandler.getInstance();
            sessionManagerFxmlLoader = parentController.getSessionManagerFXML();
            sessionManagerViewController = sessionManagerFxmlLoader.getController();
            sessionManagerViewController.setListener(this);
            sessionManagerViewController.init();
            sessionManagerViewController.displaySessions();
            for (MovieSession movieSession : movieSessionList) {
                sessionManagerViewController.createDisplaySessionButton(movieSession);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            AlertViewController.showErrorMessage("Problème d'affichage, la séance n'existe pas. Tentez de vous reconnecter.");
            boolean confirmed = AlertViewController.showConfirmationMessage("Voulez-vous vous reconnecter ?");
            if (confirmed) {
                parentController.toLogin();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the parent controller
     * @param managerController the parent controller (ManagerController type)
     */
    public void setParentController(ManagerController managerController) {
        this.parentController = managerController;
    }

    /**
     * Adds a new session to the database or modify the selected session.
     * @param sessionId the id of the session.
     * @param movieId the id of the movie.
     * @param roomId the id of the room.
     * @param version the version of the movie.
     * @param convertedDateTime the date and time of the session.
     * @param currentEditType the type of the edit (add or modify).
     */
    @Override
    public void onValidateButtonClick(Integer sessionId, Integer movieId, Integer roomId, String version, String convertedDateTime, String currentEditType) {
        try {
            validateFields(sessionId, movieId, roomId, version, convertedDateTime);
            if (currentEditType.equals("add")) {
                serverRequestHandler.sendRequest(new AddSessionRequest(new MovieSession(sessionId, viewableList.get(movieId), convertedDateTime, getRoomById(roomId), version)));
            } else if (currentEditType.equals("modify")) {
                serverRequestHandler.sendRequest(new UpdateSessionRequest(new MovieSession(sessionId, viewableList.get(movieId), convertedDateTime, getRoomById(roomId), version)));
            }
            serverRequestHandler.sendRequest(new GetAllSessionRequest());
            refreshSessionManager();
        } catch (InvalideFieldsExceptions e) {
            AlertViewController.showErrorMessage("Champs invalides : " + e.getMessage());
        } catch (TimeConflictException e) {
            AlertViewController.showErrorMessage("Conflit d'horaire avec une autre séance.");
            sessionManagerViewController.highlightConflictingSessions(e.getConflictingSessionsIds());
        }catch (SQLException e){
            AlertViewController.showInfoMessage("Impossible de modifier la séance, erreur avec la base de données. Verification de la connection au serveur.");
            try {
                validateFields(sessionId, movieId, roomId, version, convertedDateTime);
            } catch (InvalideFieldsExceptions | SQLException | TimeConflictException ex) {
                AlertViewController.showErrorMessage("Problème de connection avec le serveur. Veuillez redémarrer l'application et le serveur.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ensure that all fields are filled and in the correct format.
     * @param sessionID the id of the session
     * @param viewableId the id of the movie
     * @param roomId the id of the room
     * @param version the version of the movie
     * @param convertedDateTime the date and time of the session
     * @throws InvalideFieldsExceptions if the fields are invalid (empty or wrong format)
     */
    public void validateFields(Integer sessionID, Integer viewableId, Integer roomId, String version, String convertedDateTime) throws InvalideFieldsExceptions, TimeConflictException, SQLException {
        if (viewableId == -1 || roomId == null || version == null || !(convertedDateTime.contains(":"))) {
            throw new InvalideFieldsExceptions("Tous les champs n'ont pas été remplis");
        }
    }

    /**
     * Sets the possible movies names that can be selected in the view.
     */
    @Override
    public void setPossibleMovies() {
        sessionManagerViewController.clearPossibleNames();
        viewableList = parentController.getViewableList();
        System.out.println("viewableList: " + viewableList.size());
        for (Viewable v : viewableList) {
            sessionManagerViewController.addPossibleName(v.getTitle());
        }
    }

    /**
     * Returns the duration of a movie from an id in the database.
     * @param id id from the view
     * @return the duration of the movie
     */
    @Override
    public Integer getMovieDuration(int id) {
        viewableList = parentController.getViewableList();
        Viewable v = viewableList.get(id);
        int duration = v.getTotalDuration();
        return duration;
    }

    /**
     * Sets the possible rooms that can be selected in the view.
     */
    @Override
    public void setPossibleRooms() {
        roomList = parentController.getRoomList();
        System.out.println("roomList: " + roomList.size());
        for (Room r : roomList) {
            sessionManagerViewController.addPossibleRoom(r.getNumber());
        }
    }

    /**
     * Returns the viewable from the current selection in the view.
     * @param currentSelection the current selection
     * @return the viewable from the current selection
     */
    public Viewable getViewableFrom(Integer currentSelection) {
        viewableList = parentController.getViewableList();
        return viewableList.get(currentSelection);
    }

    /**
     * Returns the room from the current selection in the view.
     * @param value the current selection
     */
    @Override
    public void onRoomSelectedEvent(Integer value) {
        if (value == null) {
            return;
        }
        Room room = getRoomFrom(value);
        int capacity = room.getCapacity();
        sessionManagerViewController.setRoomCapacity(capacity);
    }

    /**
     * Deletes a session from the database.
     * @param currentSessionID the id of the session to delete
     */
    @Override
    public void onDeleteButtonClick(int currentSessionID) {
        boolean confirmed = AlertViewController.showConfirmationMessage("Voulez-vous vraiment supprimer cette séance ?");
        if (confirmed) {
            try {
                serverRequestHandler.sendRequest(new DeleteSessionRequest(currentSessionID));
                serverRequestHandler.sendRequest(new GetAllSessionRequest());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            refreshSessionManager();
        }
    }

    /**
     * Returns the movie session from an id.
     * @param id the id of the movie session
     * @return the movie session from the id
     */
    @Override
    public MovieSession getMovieSessionById(int id) {
        movieSessionList = parentController.getMovieSessionList();
        return movieSessionList.get(id);
    }

    /**
     * Returns a room from an index.
     * @param index the index of the room in the list.
     * @return the room by the id.
     */
    public Room getRoomFrom(int index){
        roomList = parentController.getRoomList();
        System.out.println("roomList: " + roomList.size());
        System.out.println("index: " + index);
        return roomList.get(index);
    }

    /**
     * Refreshes the session manager view.
     */
    public void refreshSessionManager() {
        try {
            List<MovieSession> movieSessionList = parentController.getMovieSessionList();
            sessionManagerViewController.clearSessions();
            System.out.println("movieSessionList: " + movieSessionList.size());
            for (MovieSession movieSession : movieSessionList) {
                sessionManagerViewController.createDisplaySessionButton(movieSession);
            }
            sessionManagerViewController.displaySessions();
            sessionManagerViewController.refreshAfterEdit();
        } catch (NullPointerException e) {
            AlertViewController.showErrorMessage("Problème de rafraichissement de la page. Tentez de vous reconnecter ainsi de recharger la page.");
            boolean confirmed = AlertViewController.showConfirmationMessage("Voulez-vous vous reconnecter ?");
            if (confirmed) {
                parentController.toLogin();
            }
        }
    }

    /**
     * Refreshes the view after a modification.
     *
     * @param observable is the observable that has been modified ( here the movieManagerApp)
     */
    @Override
    public void invalidated(javafx.beans.Observable observable) {
        try {
            serverRequestHandler.sendRequest(new GetAllSessionRequest());
            serverRequestHandler.sendRequest(new GetViewablesRequest());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        refreshSessionManager();
        setPossibleMovies();
        System.out.println("SessionManagerApp invalidated");
    }

    /**
     * Returns the room from the id.
     * @param id the id of the room
     * @return the room from the id
     */
    public Room getRoomById(int id){
        roomList = parentController.getRoomList();
        return roomList.get(id);
    }
}