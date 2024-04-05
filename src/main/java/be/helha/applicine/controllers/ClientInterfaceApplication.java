package be.helha.applicine.controllers;
import be.helha.applicine.dao.MovieDAO;
import be.helha.applicine.dao.impl.MovieDAOImpl;
import be.helha.applicine.views.ClientControllerView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

/**
 * This class is the main class for the client interface application.
 */
public class ClientInterfaceApplication extends Application implements ClientControllerView.ClientViewListener {
    MovieDAO movieDao = new MovieDAOImpl();
    @Override
    public void start(Stage clientWindow) throws Exception {
        FXMLLoader clientFXML = new FXMLLoader(ClientControllerView.getFXMLResource());
        ClientControllerView.setStageOf(clientFXML);
        ClientControllerView controller = clientFXML.getController();
        controller.setListener(this);
    }
    public static void main(String[] args) {
        launch();
    }



}