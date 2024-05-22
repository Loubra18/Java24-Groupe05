package be.helha.applicine.client.controllers.managercontrollers;

import be.helha.applicine.client.controllers.MasterApplication;
import be.helha.applicine.client.controllers.ServerRequestHandler;
import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.Session;
import be.helha.applicine.common.models.request.*;
import be.helha.applicine.server.FileManager;
import be.helha.applicine.common.models.Movie;
import be.helha.applicine.common.models.Viewable;
import be.helha.applicine.common.models.exceptions.InvalideFieldsExceptions;
import be.helha.applicine.client.views.managerviews.MovieManagerViewController;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

//notifiera les classes qui écoutent que la liste de films a changé

public class MovieManagerApp extends ManagerController implements MovieManagerViewController.ManagerViewListener, Observable, ServerRequestHandler.OnClientEventReceived {

    private ManagerController parentController;

    private FXMLLoader movieManagerFxmlLoader;

    private MovieManagerViewController movieManagerViewController;

    private InvalidationListener movieChangeListener;

    private InvalidationListener specialViewablesChangeListener;

    private ServerRequestHandler serverRequestHandler;

    SessionManagerApp sessionManagerApp;


    public MovieManagerApp(MasterApplication parentController) throws SQLException, IOException, ClassNotFoundException {
        super(parentController);
    }

    public MovieManagerApp() throws IOException, ClassNotFoundException {
        super();
    }

    /**
     * Starts the movie manager view.
     *
     * @param adminPage the stage of the view.
     */
    @Override
    public void start(Stage adminPage) throws IOException {
        serverRequestHandler = ServerRequestHandler.getInstance();
        movieManagerFxmlLoader = parentController.getMovieManagerFXML();
        movieManagerViewController = movieManagerFxmlLoader.getController();
        sessionManagerApp = parentController.getSessionManagerApp();
        movieManagerViewController.setListener(this);
        for (Movie movie : movieList) {
            movieManagerViewController.displayMovie(movie);
            System.out.println(movie.getId());
        }
        serverRequestHandler.setOnViewablesReceivedListener(this);
    }

    /**
     * It sets the parent controller. (MasterApplication type)
     *
     * @param parentController the parent controller to set.
     */

    public void setParentController(ManagerController parentController) {
        this.parentController = parentController;
    }


    /**
     * Adds a new movie to the database or modify the selected film.
     *
     * @param title    the title of the movie.
     * @param genre    the genre of the movie.
     * @param director the director of the movie.
     * @param duration the duration of the movie.
     * @param synopsis the synopsis of the movie.
     * @param image    the path of the image of the movie.
     * @param editType the type of the edit (add or modify).
     * @throws SQLException if there is an error with the database connection.
     */
    @Override
    public void onValidateButtonClick(int movieID, String title, String genre, String director, String duration, String synopsis, byte[] image, String editType) throws SQLException {

        try {
            validateFields(title, genre, director, duration, synopsis, image);
            Movie movie = null;
            ClientEvent clientEvent = null;
            if (editType.equals("add")) {
                movie = new Movie(title, genre, director, Integer.parseInt(duration), synopsis, image, null);
                clientEvent = new CreateMovieRequest(movie);
            } else if (editType.equals("modify")) {
                movie = (Movie) createMovieWithRawData(movieID, title, genre, director, duration, synopsis, image);
                clientEvent = new UpdateMovieRequest(movie);
            }
            serverRequestHandler.sendRequest(clientEvent);
        } catch (IOException e) {

        } catch (InvalideFieldsExceptions e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * @param movieID  the id of the movie to modify.
     * @param title    the title of the movie.
     * @param genre    the genre of the movie.
     * @param director the director of the movie.
     * @param duration the duration of the movie.
     * @param synopsis the synopsis of the movie.
     *                 We create a Movie object with data to use it to update database
     * @return the movie object with the new data inside.
     */
    private Viewable createMovieWithRawData(int movieID, String title, String genre, String director, String
            duration, String synopsis, byte[] image) {
        return new Movie(movieID, title, genre, director, Integer.parseInt(duration), synopsis, image, null);
    }

    /**
     * It opens a file chooser to choose an image.
     */
    @Override
    public void onImageChoiceButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        fileChooser.setTitle("Choisir une image");

        String appdata = System.getenv("APPDATA");
        String path = appdata + "/Applicine/images";

        File initialDirectory = new File(path);
        fileChooser.setInitialDirectory(initialDirectory);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            File imageFile = new File(selectedFile.getAbsolutePath());
            byte[] image = FileManager.fileToByteArray(imageFile);
            movieManagerViewController.displayImage(image);
        }
    }


    /**
     * It deletes a movie from the database.
     * It checks if the movie is linked to a session and if the user wants to delete it.
     * If the user confirms, the movie is deleted.
     *
     * @param movieId the id of the movie to delete.
     * @throws SQLException if there is an error with the database connection.
     */
    public void onDeleteButtonClick(int movieId) throws SQLException {
        try {
            //Affiche une alerte de confirmation pour la suppression
            boolean confirmed = AlertViewController.showConfirmationMessage("Voulez-vous vraiment supprimer ce film ?");
            if (confirmed) {
                int sessionLinkedToMovie = 0;
                int sagasLinkedToMovie = 0;
                System.out.println(sessionLinkedToMovie);
                if (sessionLinkedToMovie > 0) {
                    boolean deleteDespiteSession = AlertViewController.showConfirmationMessage("Le film est lié à des séances, voulez-vous le supprimer malgré tout ?");
                    if (!deleteDespiteSession) {
                        return;
                    }
                }
                if (sagasLinkedToMovie > 0) {
                    AlertViewController.showErrorMessage("Impossible de supprimer ce film car il est lié à des sagas");
                    return;
                }

                serverRequestHandler.sendRequest(new DeleteMoviesRequest(movieId));
                this.refreshMovieManager();
                movieManagerViewController.deletionConfirmed();
                notifyListeners();
            }
        } catch (Exception e) {
            AlertViewController.showErrorMessage("Le film n'a pas pu être supprimé");
        }
    }

    /**
     * It validates the fields of the movie by checking if they are empty or if the duration is a number.
     *
     * @param title    the title of the movie.
     * @param genre    the genre of the movie.
     * @param director the director of the movie.
     * @param duration the duration of the movie.
     * @param synopsis the synopsis of the movie.
     * @param image    the path of the image of the movie.
     * @throws InvalideFieldsExceptions if the fields are empty or if the duration is not a number.
     */

    public void validateFields(String title, String genre, String director, String duration, String synopsis, byte[] image) throws InvalideFieldsExceptions {
        if (title.isEmpty() || genre.isEmpty() || director.isEmpty() || duration.isEmpty() || synopsis.isEmpty() || image == null) {
            throw new InvalideFieldsExceptions("Tous les champs doivent être remplis");
        }
        try {
            Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            throw new InvalideFieldsExceptions("La durée doit être un nombre entier");
        }
    }

    /**
     * It returns the file name from the path by checking the operating system.
     *
     * @param path
     * @return
     */
    public String getFileNameFrom(String path) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return path.substring(path.lastIndexOf("\\") + 1);
        } else {
            return path.substring(path.lastIndexOf("/") + 1);
        }
    }

    /**
     * It creates a valid path by checking if the path starts with "file:".
     * This is necessary for the image to be displayed in the view.
     * If the path does not start with "file:", it adds it.
     *
     * @param imagePath the path of the image.
     * @return the valid path to the image.
     */

    public String createValidPath(String imagePath) {
        if (imagePath.startsWith("file:")) {
            return imagePath;
        }
        return "file:" + imagePath;
    }

    /**
     * It refreshes the movie list by clearing the movies and adding them again when called.
     */
    public void refreshMovieManager() {
        movieManagerViewController.clearMovies();
        for (Movie movie : movieList) {
            movieManagerViewController.displayMovie(movie);
        }
        movieManagerViewController.setSelection();
        movieManagerViewController.refreshAfterEdit();
    }

    @Override
    public Movie getMovieFrom(int index) {
        return movieList.get(index);
    }

    /**
     * It logs out the user and returns to the login page.
     */
    public void toLogin() {
        parentController.toLogin();
    }

    /**
     * It sets the observable listener that will be notified when the movie list changes.
     *
     * @param movieChangeListener the listener to set.
     */
    @Override
    public void addListener(InvalidationListener movieChangeListener) {
        //On se sert de l'observable pour notifier les SessionApp que la liste de films a changé
        this.movieChangeListener = movieChangeListener;
    }

    public void addSpecialViewablesListener(InvalidationListener specialViewablesChangeListener) {
        this.specialViewablesChangeListener = specialViewablesChangeListener;
    }

    /**
     * It removes the listener.
     *
     * @param invalidationListener the listener to remove.
     */
    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        this.movieChangeListener = null;
    }


    /**
     * It notifies the listeners that the movie list has changed.
     */
    private void notifyListeners() {
        if (movieChangeListener != null) {
            movieChangeListener.invalidated(this);
        }
        if (specialViewablesChangeListener != null) {
            specialViewablesChangeListener.invalidated(this);
        }
    }

    public void fullFieldMovieListFromDB() {
        try {
            GetMoviesRequest request = new GetMoviesRequest();
            serverRequestHandler.sendRequest(request);
            GetAllSessionRequest request1 = new GetAllSessionRequest();
            serverRequestHandler.sendRequest(request1);
            GetRoomsRequest request2 = new GetRoomsRequest();
            serverRequestHandler.sendRequest(request2);
            GetViewablesRequest request3 = new GetViewablesRequest();
            serverRequestHandler.sendRequest(request3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClientEvenReceived(ClientEvent clientEvent) {
        Platform.runLater(() -> {
            if (clientEvent instanceof GetMoviesRequest request) {
                movieList = request.getMovies();
                System.out.println("Movie list: " + movieList.size());
                refreshMovieManager();
            } else if (clientEvent instanceof CreateMovieRequest) {
                CreateMovieRequest request = (CreateMovieRequest) clientEvent;
                if (request.getStatus()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof DeleteMoviesRequest) {
                DeleteMoviesRequest request = (DeleteMoviesRequest) clientEvent;
                if (request.getStatus()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof UpdateMovieRequest) {
                UpdateMovieRequest request = (UpdateMovieRequest) clientEvent;
                if (request.getStatus()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof GetViewablesRequest) {
                GetViewablesRequest request = (GetViewablesRequest) clientEvent;
                viewableList = request.getViewables();
            } else if (clientEvent instanceof AddViewableRequest) {
                AddViewableRequest request = (AddViewableRequest) clientEvent;
                if (request.getSuccess()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof DeleteViewableRequest) {
                DeleteViewableRequest request = (DeleteViewableRequest) clientEvent;
                if (request.getSuccess()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof UpdateViewableRequest) {
                UpdateViewableRequest request = (UpdateViewableRequest) clientEvent;
                if (request.getSuccess()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof GetAllSessionRequest) {
                GetAllSessionRequest request = (GetAllSessionRequest) clientEvent;
                movieSessionList = request.getSessions();
                System.out.println("Movie session list: " + movieSessionList.size());
                sessionManagerApp.refreshSessionManager(movieSessionList);
            } else if (clientEvent instanceof AddSessionRequest) {
                AddSessionRequest request = (AddSessionRequest) clientEvent;
                if (request.getSuccess()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof DeleteSessionRequest) {
                DeleteSessionRequest request = (DeleteSessionRequest) clientEvent;
                if (request.getStatus()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof UpdateSessionRequest) {
                UpdateSessionRequest request = (UpdateSessionRequest) clientEvent;
                if (request.getSuccess()) {
                    fullFieldMovieListFromDB();
                }
            } else if (clientEvent instanceof GetRoomsRequest) {
                GetRoomsRequest request = (GetRoomsRequest) clientEvent;
                roomList = request.getRooms();
            }
        });
    }
}
