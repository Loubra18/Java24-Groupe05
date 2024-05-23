package be.helha.applicine.client.views.managerviews;

import be.helha.applicine.common.models.Viewable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialViewableViewController {
    private SpecialViewableListener listener;
    private ArrayList<String> moviesTitleToChoose;

    @FXML
    private Button addMovieButton;

    @FXML
    private ListView<Label> movieList;

    @FXML
    private ComboBox<String> movieChoice;

    @FXML
    private Button removeMovieButton;
    @FXML
    private Button validateButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label totalDurationLabel;

    @FXML
    private Label genreLabel;

    @FXML
    private TextField sagaNameField;

    @FXML
    private ScrollPane sagaList;
    @FXML
    private Button deleteButton;

    @FXML
    private AnchorPane editPane;


    VBox VboxToDisplay = new VBox();

    public static URL getFXMLResource() {
        return SpecialViewableViewController.class.getResource("SpecialViewableView.fxml");
    }

    @FXML
    void onAddMovieButtonClick(ActionEvent event) {
        listener.onAddMovieButtonClick();
    }

    //fonction qui permet de récupérer le film choisi dans le combobox
    @FXML
    void onMovieChoising(ActionEvent event) {
        if (movieChoice.getSelectionModel().getSelectedItem() != null) {
            System.out.println("Film s'électionné: " + movieChoice.getSelectionModel().getSelectedItem());
            System.out.println("Index du film choisi: " + movieChoice.getSelectionModel().getSelectedIndex());
            listener.onMovieChoising(movieChoice.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    void onRemoveMovieButtonClick(ActionEvent event) {
        listener.onRemoveMovieButtonClick();
    }

    @FXML
    void onValidateButtonClick(ActionEvent event) throws SQLException {
        listener.onValidateButtonClick(sagaNameField.getText());
    }


    @FXML
    void onCancelButtonClick(ActionEvent event) {
        listener.onCancelButtonClick();
    }


    //methode d'initialisation de la vue (remplissage des listes, des combobox, etc)
    public void init() throws SQLException {
        VboxToDisplay.prefWidthProperty().bind(sagaList.widthProperty());
        displaySagas();
    }


    public void fillMovieChoice(List<String> moviesTitleToChoose) {
        System.out.println("fillMovieChoice");
        movieChoice.getItems().clear();
        for (String title : moviesTitleToChoose) {
            movieChoice.getItems().add(title);
        }
    }

    public void displaySagas() {
        VboxToDisplay.getChildren().clear();
        listener.displaySagas();
    }

    public void addAddButton() {
        if (!VboxToDisplay.getChildren().isEmpty()) {
            if (VboxToDisplay.getChildren().getLast().getStyleClass().contains("addButton")) {
                VboxToDisplay.getChildren().removeLast();
            }
        }
        Button button = new Button("+");
        button.getStyleClass().set(0, "addButton");
        button.prefWidthProperty().bind(VboxToDisplay.widthProperty());
        button.setOnAction(e -> {
            listener.onAddSagaButtonClick();
        });
        VboxToDisplay.getChildren().add(button);
        sagaList.setContent(VboxToDisplay);
    }


    public void fillAddedMovieChoice(List<String> addedViewablesTitles, Integer totalDuration) {
        movieList.getItems().clear();
        for (String title : addedViewablesTitles) {
            Label label = new Label(title);
            movieList.getItems().add(label);
        }
        setTotalDuration(totalDuration);
    }

    private void setTotalDuration(Integer totalDuration) {
        totalDurationLabel.setText("Durée totale: " + convertToDurationString(totalDuration) + " minutes");
    }

    private String convertToDurationString(Integer totalDuration) {
        int hours = totalDuration / 60;
        int minutes = totalDuration % 60;
        return hours + "h" + minutes;
    }

    public void onCancelConfirm() {
        sagaNameField.clear();
        movieList.getItems().clear();
        totalDurationLabel.setText("");
        this.editPane.setVisible(false);

    }


    public void displaySaga(Viewable viewable) {
        Button button = new Button(viewable.getTitle());
        button.getStyleClass().set(0, "buttonS");
        button.prefWidthProperty().bind(VboxToDisplay.widthProperty());
        button.setOnAction(e -> {
            onSagaDisplayButtonClick(viewable, button);
        });
        VboxToDisplay.getChildren().add(button);
        sagaList.setContent(VboxToDisplay);

    }

    private void onSagaDisplayButtonClick(Viewable viewable, Button button) {
        showSelectedButton(button);
        listener.onSagaDisplayButtonClick(viewable);
    }

    private void showSelectedButton(Button button) {
        setInitialStyleForAllButtons();
        button.getStyleClass().add("Selected");
    }

    private void setInitialStyleForAllButtons() {
        for (int i = 0; i < VboxToDisplay.getChildren().size(); i++) {
            Button button = (Button) VboxToDisplay.getChildren().get(i);
            if (!button.getStyleClass().contains("addButton")) {
                button.getStyleClass().set(0, "buttonS");
                button.getStyleClass().remove("Selected");
            }
        }
    }

    public void showSagaForEdit(String name, List<String> addedViewablesTitles, Integer totalDuration) {
        sagaNameField.setText(name);
        fillAddedMovieChoice(addedViewablesTitles, totalDuration);
        this.editPane.setVisible(true);
        this.deleteButton.setVisible(true);
    }

    public void prepareForAddSaga() {
        sagaNameField.clear();
        totalDurationLabel.setText("");
        movieList.getItems().clear();
        setInitialStyleForAllButtons();
        this.editPane.setVisible(true);
        this.deleteButton.setVisible(false);
    }

    public void onDeleteSagaButtonClick(ActionEvent event) throws SQLException {
        try {
            listener.onSagaDeleteButtonClick();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public interface SpecialViewableListener {
        void onAddMovieButtonClick();

        void onRemoveMovieButtonClick();

        ArrayList<String> displayAllMovies() throws SQLException;

        void onMovieChoising(int selectedIndex);


        void onValidateButtonClick(String name) throws SQLException;

        void onCancelButtonClick();

        void displaySagas();

        void onSagaDisplayButtonClick(Viewable viewable);

        void onAddSagaButtonClick();

        void onSagaDeleteButtonClick() throws SQLException, IOException;
    }

    public void refresh() throws SQLException {
        displaySagas();
    }

    public void setListener(SpecialViewableListener listener) {
        this.listener = listener;
    }
}
