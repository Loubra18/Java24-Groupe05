package be.helha.applicine.client.controllers.managercontrollers;

import be.helha.applicine.client.controllers.MasterApplication;
import be.helha.applicine.client.network.ServerRequestHandler;
import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.Movie;
import be.helha.applicine.common.models.MovieSession;
import be.helha.applicine.common.models.Room;
import be.helha.applicine.common.models.Viewable;
import be.helha.applicine.common.models.request.ClientEvent;
import be.helha.applicine.common.models.request.GetMoviesRequest;
import be.helha.applicine.common.models.request.PingServer;
import be.helha.applicine.common.models.request.RequestVisitor;
import be.helha.applicine.server.database.DatabaseConnection;
import be.helha.applicine.client.views.managerviews.MainManagerViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ManagerApplication class is the controller class for the Manager view.
 * It is responsible for managing the movies, the sessions and the special viewables (sagas for example)
 * Only the manager can access this view and manage the movies and the sessions.
 */
public class ManagerController extends Application implements ServerRequestHandler.Listener, RequestVisitor {
    private final FXMLLoader mainFxmlLoader = new FXMLLoader(MainManagerViewController.getFXMLResource());

    /**
     * parentController is useful to say Master which window is currently open.
     */
    private MasterApplication parentController;
    protected List<Movie> movieList = new ArrayList<>();

    protected List<Room> roomList = new ArrayList<>();

    protected List<MovieSession> movieSessionList = new ArrayList<>();

    protected List<Viewable> viewableList =new ArrayList<>();

    private MainManagerViewController mainManagerViewController;

    private final ServerRequestHandler serverRequestHandler;

    /**
     * It fetches all the movies from the database to movieList.
     * It follows the DAO design pattern https://www.digitalocean.com/community/tutorials/dao-design-pattern.
     */
    public ManagerController(MasterApplication parentController) throws SQLException, IOException, ClassNotFoundException {
        this.parentController = parentController;
        serverRequestHandler = ServerRequestHandler.getInstance();
        serverRequestHandler.addListener(this);
    }

    public ManagerController() throws IOException, ClassNotFoundException {
        serverRequestHandler = ServerRequestHandler.getInstance();
        serverRequestHandler.addListener(this);
    }

    /**
     * Starts the Manager view.
     *
     * This method initializes the MainManagerViewController, sets the current window of the parent controller,
     * and creates instances of MovieManagerApp, SessionManagerApp, and SpecialViewableController.
     * It also sets up listeners for these instances and starts them.
     * Finally, it sets up a close request handler for the adminPage stage to close the database connection when the stage is closed.
     *
     * @param adminPage the stage of the view.
     * @throws IOException  if there is an error with the fxml file.
     * @throws SQLException if there is an error with the database connection.
     */
    @Override
    public void start(Stage adminPage) throws IOException, SQLException, ClassNotFoundException {

        MainManagerViewController.setStageOf(mainFxmlLoader);

        parentController.setCurrentWindow(MainManagerViewController.getStage());

        mainManagerViewController = mainFxmlLoader.getController();


        MovieManagerApp movieManagerApp = new MovieManagerApp(parentController);
        movieManagerApp.setParentController(this);


        SessionManagerApp sessionManagerApp = new SessionManagerApp(parentController);
        sessionManagerApp.setParentController(this);

        SpecialViewableController specialViewableController = new SpecialViewableController(parentController);
        specialViewableController.setParentController(this);


        movieManagerApp.addListener(sessionManagerApp);
        movieManagerApp.addSpecialViewablesListener(specialViewableController);

        specialViewableController.addListener(sessionManagerApp);

        movieManagerApp.start(adminPage);
        sessionManagerApp.start(adminPage);
        specialViewableController.start(adminPage);
        adminPage.setOnCloseRequest(e -> {
            DatabaseConnection.closeConnection();
        });
    }

    /**
     * Launches the Manager view.
     *
     * @param args the arguments of the main method.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * It returns a movie to the movieList at index.
     * @param index the index of the movie in the movieList.
     * @return movieList
     */


    /**
     * Redirects to the login view and disconnect the user.
     */
    public void toLogin() {
        parentController.toLogin();
    }

    /**
     * It returns the full movie list from the database.
     *
     * @return List of Visionable objects which contains all the movies from the database.
     */
    public void fullFieldMovieListFromDB(){
        try {
            GetMoviesRequest request = new GetMoviesRequest();
            serverRequestHandler.sendRequest(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * It returns the fxmlLoader of the movieManager.
     *
     * @return
     */

    protected FXMLLoader getMovieManagerFXML() {
        return mainManagerViewController.getMovieManagerFXML();
    }

    /**
     * It returns the fxmlLoader of the sessionManager.
     *
     * @return
     */

    protected FXMLLoader getSessionManagerFXML() {
        return mainManagerViewController.getSessionManagerFXML();
    }

    /**
     * It returns the fxmlLoader of the specialViewable.
     * @return
     */
    protected FXMLLoader getSpecialViewableFXML() {
        return mainManagerViewController.getSpecialViewableFXML();
    }

    /**
     * It returns the movie from an index.
     * @param id
     * @return
     */
    public Movie getMovieFrom(int id) {
        //return serverRequestHandler.sendRequest(new GetMovieByIdRequest(id));
        return movieList.get(id);
    }

    /**
     * Apply the dispatch depending on the response
     * @param response
     */

    @Override
    public void onResponseReceive(ClientEvent response) {
        response.dispatchOn(this);
    }

    /**
     * It tries to make a request if the connection is lost
     */
    @Override
    public void onConnectionLost() {
        AlertViewController.showErrorMessage("Connection perdue avec le serveur. Reconnexion en cours...");
        try {
            serverRequestHandler.sendRequest(new PingServer());
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Impossible de se reconnecter au serveur. Tentez de redémarrer l'application.");
            Platform.exit();
        }
    }
}
