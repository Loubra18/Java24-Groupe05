package be.helha.applicine.client.views;

import be.helha.applicine.common.models.Movie;
import be.helha.applicine.common.models.Saga;
import be.helha.applicine.common.models.Viewable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Controller for the movie pane.
 */
public class MoviePaneViewController {
    @FXML
    public VBox imageVbox;
    @FXML
    public Button moreInfoButton;
    @FXML
    public Button lessInfoButton;
    @FXML
    public Label infoMovie;
    @FXML
    public ScrollPane movieInfoScrollPane;
    public AnchorPane anchorPane;

    @FXML
    private VBox root;
    @FXML
    private ImageView imageView;
    @FXML
    public Label titleLabel;
    @FXML
    public Button leftButton;
    @FXML
    public Button rightButton;
    @FXML
    private Label sagaLabel;
    private MoviePaneViewListener listener;

    /**
     * Sets the listener for the movie pane.
     * @param listener the listener to set.
     */
    public void setListener(MoviePaneViewListener listener) {
        this.listener = listener;
    }

    private Viewable movie;

    private ArrayList<Movie> movies;

    private int currentMovieIndex = 0;

    /**
     * Gets the FXML resource of the movie pane.
     * @return the FXML resource of the movie pane.
     */
    public static URL getFXMLResource() {
        return MoviePaneViewController.class.getResource("/be/helha/applicine/client/views/components/MoviePane.fxml");
    }

    /**
     * Sets the movie of the movie pane.
     * @param movie the movie to set.
     */
    public void setMovie(Viewable movie) throws IllegalArgumentException {
        if (movie instanceof Movie) {
            System.out.println("Viewable");
            this.movie = movie;
            titleLabel.setText(movie.getTitle());
            imageView.setImage(new Image(new ByteArrayInputStream(movie.getImage())));
            infoMovie.setText(movie.getSynopsis());
        }
        else if(movie instanceof Saga){
            movies = ((Saga) movie).getMovies();
            this.movie = movie;
            currentMovieIndex = 0;
            sagaLabel.setVisible(true);
            titleLabel.setText(movie.getTitle());
            imageView.setImage(new Image(new ByteArrayInputStream(movie.getImage())));
            infoMovie.setText(movie.getSynopsis());
            leftButton.setVisible(true);
            rightButton.setVisible(true);
        }
        else {
            throw new IllegalArgumentException("The movie must be an instance of Viewable.");
        }
    }

    /**
     * Gets the root of the movie pane.
     *
     * @return the root of the movie pane.
     */
    public Pane getRoot() {
        return root;
    }

    public void toBuyTicketPage(ActionEvent actionEvent) {
        try {
            listener.onBuyTicketClicked(movie);
        } catch (Exception e) {
            AlertViewController.showErrorMessage("Erreur lors de l'achat du ticket. Veuillez redémarrer l'application.");
        }
    }

    /**
     * Handles the more info button.
     * @param mouseEvent the mouse event.
     */
    public void moreInfoHandling(MouseEvent mouseEvent) {
        imageView.setFitHeight(imageView.getFitHeight() / 3);
        imageVbox.setAlignment(Pos.TOP_CENTER);
        moreInfoButton.setVisible(false);
        lessInfoButton.setVisible(true);
        movieInfoScrollPane.setPrefHeight(200);
        int size = countLines(infoMovie) * 20;
        if (size < 400) {
            size = 400;
        }
        System.out.println(size);
        anchorPane.setPrefHeight((double) size / 2);
        infoMovie.setPrefHeight((double) size / 2);
    }

    /**
     * Handles the less info button.
     * @param mouseEvent the mouse event.
     */
    public void lessInfoHandling(MouseEvent mouseEvent) {
        imageView.setFitHeight(imageView.getFitHeight() * 3);
        imageVbox.setAlignment(Pos.TOP_CENTER);
        moreInfoButton.setVisible(true);
        lessInfoButton.setVisible(false);
        movieInfoScrollPane.setPrefHeight(40);
        anchorPane.setPrefHeight(movieInfoScrollPane.getPrefHeight() - 2);
        infoMovie.setPrefHeight(0);
    }

    /**
     * Counts the number of lines of a label.
     * @param label the label.
     * @return the number of lines of the label.
     */
    public int countLines(Label label) {
        anchorPane.setPrefHeight(1000);
        label.setPrefHeight(1000);
        String text = label.getText();
        double labelWidth = label.getWidth();
        double characterWidth = new Text("W").getLayoutBounds().getWidth();
        System.out.println(characterWidth);
        double charactersPerLine = Math.floor(labelWidth / characterWidth);
        return (int) Math.ceil(text.length() / charactersPerLine);
    }

    /**
     * Shows the next movie of the saga
     * @param actionEvent the action event.
     */
    public void showNextMovie(ActionEvent actionEvent) {
        sagaLabel.setVisible(true);
        currentMovieIndex++;
        if (currentMovieIndex == movies.size()) {
            currentMovieIndex = 0;
        }
        imageView.setImage(new Image(new ByteArrayInputStream(movies.get(currentMovieIndex).getImage())));
    }

    /**
     * Shows the previous movie of the saga.
     * @param actionEvent the action event.
     */
    public void showPreviousMovie(ActionEvent actionEvent) {
        sagaLabel.setVisible(true);
        currentMovieIndex--;
        if (currentMovieIndex < 0) {
            currentMovieIndex = movies.size() - 1;
        }
        imageView.setImage(new Image(new ByteArrayInputStream(movies.get(currentMovieIndex).getImage())));
    }

    public interface MoviePaneViewListener {
        void onBuyTicketClicked(Viewable movie) throws Exception;
    }


}