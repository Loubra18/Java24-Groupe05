package com.example.applicine.views;

import com.example.applicine.controllers.MasterApplication;
import com.example.applicine.dao.MovieDAO;
import com.example.applicine.dao.impl.MovieDAOImpl;
import com.example.applicine.database.ApiRequest;
import com.example.applicine.models.Movie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class ControllerClient {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane filmsContainer;
    @FXML
    private Button rightButton;
    @FXML
    private Button leftButton;

    private MovieDAO movieDAO = new MovieDAOImpl();
    private List<Movie> moviesList;
    //attribute to keep track of the index of the first movie to be displayed
    int offsetIndex = 0;
    private ClientViewListener listener;
    private final MasterApplication parentController = new MasterApplication();
    private static Stage clientWindow;

    public static Stage getClientWindow() {
        return clientWindow;
    }

    public void setListener(ClientViewListener listener) {
        this.listener = listener;
    }

    public void initialize() {
        parentController.setCurrentWindow(clientWindow);
        movieDAO.adaptAllImagePathInDataBase();
        moviesList = movieDAO.getAllMovies();

        if (moviesList.isEmpty()) {
            JFrame frame = null;
            try {
                frame = getWaitingWindow();

                ApiRequest.main(null);
                moviesList = movieDAO.getAllMovies();
                frame.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            frame.dispose();
        }

        showMovies();
    }

    /**
     * This method creates a JFrame that will be displayed while the database is being filled.
     *
     * @return The JFrame that will be displayed.
     */
    private JFrame getWaitingWindow() {
        JFrame frame = new JFrame();
        frame.setSize(500, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Veuillez patienter pendant que la base de données se remplit...");
        frame.add(label);
        frame.setVisible(true);
        return frame;
    }

    /**
     * This method sets the stage of the client interface.
     *
     * @param fxmlLoader
     * @throws IOException
     */
    public static void setStageOf(FXMLLoader fxmlLoader) throws IOException {
        clientWindow = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 1000, 750);
        clientWindow.setTitle("Client Interface");
        clientWindow.setScene(scene);
        clientWindow.show();
    }

    /**
     * This method displays the movies in the database.
     * We get the MoviePane fxml file and set the movie in the controller.
     * We then add the pane to the filmsContainer.
     *
     */
    public void showMovies() {
        filmsContainer.getChildren().clear();
        for (int i = 0; i < moviesList.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MoviePane.fxml"));
                Pane pane = loader.load();
                MoviePaneController controller = loader.getController();
                controller.setMovie(moviesList.get(i));
                filmsContainer.getChildren().add(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to deconnect the user to make him return on the login page.
     * @throws Exception
     */
    public void toLoginPage() throws Exception {
        parentController.toLogin();
    }


    /**
     * This inner interface will be used to listen to the events in the client interface.
     *
     */
    public interface ClientViewListener {
        //We will keep this empty for now
    }


    /**
     * This method returns the URL of the fxml file of the client interface.
     * @return
     */
    public static URL getFXMLResource() {
        return ControllerClient.class.getResource("clientSide.fxml");
    }
}