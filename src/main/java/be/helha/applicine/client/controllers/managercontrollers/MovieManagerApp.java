package be.helha.applicine.client.controllers.managercontrollers;

import be.helha.applicine.client.controllers.MasterApplication;
import be.helha.applicine.client.network.ServerRequestHandler;
import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.request.*;
import be.helha.applicine.server.FileManager;
import be.helha.applicine.common.models.Movie;
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

/**
 * MovieManagerApp class is the controller class for the MovieManager view.
 * It is responsible for managing the movies.
 * Only the manager can access this view and manage the movies.
 *
 * It implements the Observable interface to notify the listeners when the movie list changes.
 * MovieManagerApp is an observable for the SessionApp class and the SpecialViewablesApp class.
 *
 * It implements the RequestVisitor interface to handle the requests.
 * It extends the ManagerController class.
 */
public class MovieManagerApp extends ManagerController implements MovieManagerViewController.ManagerViewListener, Observable {

    private ManagerController parentController;

    private MovieManagerViewController movieManagerViewController;

    private InvalidationListener movieChangeListener;

    private InvalidationListener specialViewablesChangeListener;

    private ServerRequestHandler serverRequestHandler;



    /**
     * It adds itself to listener list of the serverRequestHandler to be notified when the request is responded.
     *
     */
    public MovieManagerApp(MasterApplication parentController) throws SQLException, IOException, ClassNotFoundException {
        super(parentController);
        serverRequestHandler = ServerRequestHandler.getInstance();
        serverRequestHandler.addListener(this);
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
    public void start(Stage adminPage) {
        FXMLLoader movieManagerFxmlLoader = parentController.getMovieManagerFXML();
        movieManagerViewController = movieManagerFxmlLoader.getController();
        movieManagerViewController.setListener(this);
        for (Movie movie : movieList) {
            movieManagerViewController.displayMovie(movie);
            System.out.println(movie.getId());
        }

        movieManagerViewController.hideEditPane();
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
        System.out.println("avant le trycatch Le chemin de l'image est " + image);

        try {
            validateFields(title, genre, director, duration, synopsis, image);
        } catch (InvalideFieldsExceptions e) {
            AlertViewController.showErrorMessage("Champs invalides" + e.getMessage());
            return;
        }
        Movie movie;
        ClientEvent clientEvent;
        if (editType.equals("add")) {
            movie = new Movie(title, genre, director, Integer.parseInt(duration), synopsis, image, null);
            clientEvent = new CreateMovieRequest(movie);
            try {
                serverRequestHandler.sendRequest(clientEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (editType.equals("modify")) {
            movie = new Movie(movieID, title, genre, director, Integer.parseInt(duration), synopsis, image, null);
            clientEvent = new UpdateMovieRequest(movie);
            try {
                serverRequestHandler.sendRequest(clientEvent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
        try {
            File imageFile = new File(selectedFile.getAbsolutePath());
            byte[] image = FileManager.fileToByteArray(imageFile);
            movieManagerViewController.displayImage(image);
        } catch (IOException e) {
            AlertViewController.showErrorMessage("Fichier introuvable, veuillez vérifier l'endroit où il se trouve.");
            System.out.println(e.getMessage());
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
        //Affiche une alerte de confirmation pour la suppression
        boolean confirmed = AlertViewController.showConfirmationMessage("Voulez-vous vraiment supprimer ce film ?");
        if (confirmed) {
            try {
                serverRequestHandler.sendRequest(new DeleteMoviesRequest(movieId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    /**
     * It updates the movie list when the request is responded.
     * We use Platform.runLater to apply the changes on the JavaFX thread.
     * @param getMoviesRequest
     */
    @Override
    public void visit(GetMoviesRequest getMoviesRequest) {
        this.movieList = getMoviesRequest.getMovieList();
        Platform.runLater(this::refreshMovieManager);
    }

    /**
     * It updates the movie list when the movie is added in the database.
     * @param createMovieRequest
     */
    @Override
    public void visit(CreateMovieRequest createMovieRequest) {

        if (createMovieRequest.getStatus()) {
            Platform.runLater(() -> {
                fullFieldMovieListFromDB();
                notifyListeners();
            });
        } else {
            AlertViewController.showErrorMessage("Le film n'a pas pu être ajouté");
        }
    }

    /**
     * It updates the movie list when the movie is modified in the database.
     * @param updateMovieRequest
     */
    @Override
    public void visit(UpdateMovieRequest updateMovieRequest) {
        if (updateMovieRequest.getStatus()) {
            fullFieldMovieListFromDB();
            Platform.runLater(() -> {
                movieManagerViewController.refreshAfterEdit();
                notifyListeners();
            });
        } else {
            AlertViewController.showErrorMessage("Le film n'a pas pu être modifié ");
        }

    }


    /**
     * It updates the movie list when the movie is deleted from the database.
     * @param deleteMoviesRequest
     */
    @Override
    public void visit(DeleteMoviesRequest deleteMoviesRequest) {
        if (deleteMoviesRequest.getStatus()) {
            Platform.runLater(() -> {
                this.refreshMovieManager();
                movieManagerViewController.deletionConfirmed();
                notifyListeners();
            });
        } else {
            Platform.runLater(() -> {
                AlertViewController.showErrorMessage(deleteMoviesRequest.getMessage());
            });
        }
    }

    @Override
    public void visit(ErrorMessage errorMessage) {
        Platform.runLater(() -> {
            AlertViewController.showErrorMessage(errorMessage.getMessage());
        });
    }
}
