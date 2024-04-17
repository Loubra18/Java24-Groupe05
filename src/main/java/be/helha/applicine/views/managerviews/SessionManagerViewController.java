package be.helha.applicine.views.managerviews;

import be.helha.applicine.models.Movie;
import be.helha.applicine.models.MovieSession;
import be.helha.applicine.models.exceptions.InvalideFieldsExceptions;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SessionManagerViewController {


    @FXML
    private DatePicker DateSelector;

    @FXML
    private ChoiceBox<String> hourSelector;

    @FXML
    private ChoiceBox<String> minuteSelector;

    @FXML
    private ComboBox<String> movieSelector;

    @FXML
    private ChoiceBox<Integer> roomSelector;

    @FXML
    private ScrollPane sessionsList;

    @FXML
    private AnchorPane sessionEditPane;

    @FXML
    private Label timeShowLabel;

    @FXML
    private Label freePlacesLabel;

    @FXML
    private Button validateButton;

    @FXML
    private Button cancelButton;
    @FXML
    private Label editTypeInfoLabel;


    @FXML
    private ChoiceBox<String> versionSelector;

    @FXML
    private Button deleteButton;

    //Room manager fxml elements
    @FXML
    private Button validateRoomButton;

    @FXML
    private Button cancelRoomButton;


    @FXML
    private TextField freePlacesSetter;

    @FXML
    private ComboBox <String> roomToEditSelector;

    //End of Room manager fxml elements


    VBox vBoxToDisplay = new VBox();


    private List<Button> sessionButtons = new ArrayList<Button>();
    private SessionManagerViewListener listener;

    private String currentEditionType;
    private Integer currentMovieSelection = -1;

    private Integer currentRoomSelection = -1;

    private int currentSessionID = -1;
    private String currentRoomEditionType ="add";

    /**
     * Initializes the controller class.
     */

    public void intialize() {
        vBoxToDisplay.prefWidthProperty().bind(sessionsList.widthProperty());
        setHourSelectorPossibilities();
        setMinuteSelectorPossibilities();
        setVersionSelectorPossibilities();
        setPossibleMovies();
        setPossibleRooms();
        this.currentEditionType = "";
    }

    /**
     * Adds a session add button to the list of sessions.
     *
     * @return
     */

    private Button addButton() {
        Button button = new Button("+");
        button.getStyleClass().add("addButton");
        button.prefWidthProperty().bind(vBoxToDisplay.widthProperty());
        button.setOnAction(event -> {
            onAddButtonClick();
        });
        return button;
    }

    /**
     * Sets the current edition type to "add" and displays the session edit pane.
     */
    private void onAddButtonClick() {
        setInitialStyleButtons();
        this.currentEditionType = "add";
        sessionEditPane.setVisible(true);
        this.deleteButton.setVisible(false);
        this.editTypeInfoLabel.setText("Ajouter une séance");
        clearFields();
        this.sessionEditPane.setVisible(true);
    }

    /**
     * Returns the URL of the FXML resource.
     *
     * @return
     */
    public static URL getFXMLResource() {
        return SessionManagerViewController.class.getResource("sessionManagerView.fxml");
    }


    /**
     * Sets the possibilities for the hour selector.
     */
    public void setHourSelectorPossibilities() {
        for (Integer i = 0; i < 24; i++) {
            if (i < 10)
                hourSelector.getItems().add("0" + i);
            else
                hourSelector.getItems().add(i.toString());
        }
    }

    /**
     * Sets the possibilities for the minute selector.
     */

    public void setMinuteSelectorPossibilities() {
        for (Integer i = 0; i < 60; i += 10) {
            if (i < 10)
                minuteSelector.getItems().add("0" + i);
            else
                minuteSelector.getItems().add(i.toString());
        }
    }

    /**
     * Sets the possibilities for the version selector.
     */

    public void setVersionSelectorPossibilities() {
        versionSelector.getItems().add("2D");
        versionSelector.getItems().add("3D");
    }

    /**
     * Validates the session and sends the data to the listener.
     *
     * @param event
     * @throws SQLException
     * @throws InvalideFieldsExceptions
     */

    public void onValidateButtonClick(ActionEvent event) throws SQLException, InvalideFieldsExceptions {
        if (currentEditionType.equals("add")) {
            try {
                listener.onValidateButtonClick(0, getMovie(currentMovieSelection).getId(), roomSelector.getValue(), versionSelector.getValue(), converDateAndHourToDateTime(), this.currentEditionType);
            } catch (IndexOutOfBoundsException e) {
                listener.onValidateButtonClick(0, -1, roomSelector.getValue(), versionSelector.getValue(), converDateAndHourToDateTime(), this.currentEditionType);
            }
        } else if (currentEditionType.equals("modify")) {
            listener.onValidateButtonClick(currentSessionID, getMovie(currentMovieSelection).getId(), roomSelector.getValue(), versionSelector.getValue(), converDateAndHourToDateTime(), this.currentEditionType);
        }
    }

    /**
     * Sets the listener.
     *
     * @param listener
     */
    public void setListener(SessionManagerViewListener listener) {
        this.listener = listener;
    }

    /**
     * Adds a possible name to the movie selector.
     *
     * @param name
     */
    public void addPossibleName(String name) {
        movieSelector.getItems().add(name);
    }

    /**
     * Adds a possible room to the room selector.
     *
     * @param number
     */

    public void addPossibleRoom(int number) {
        roomSelector.getItems().add(number);
    }

    /**
     * Sets the current room selection and sends it to the listener.
     *
     * @param event
     * @throws SQLException
     */

    public void onRoomSelectedEvent(ActionEvent event) throws SQLException {
        currentRoomSelection = roomSelector.getValue();
        listener.onRoomSelectedEvent(currentRoomSelection);
    }

    /**
     * Sets the room capacity.
     *
     * @param capacity
     */

    public void setRoomCapacity(int capacity) {
        freePlacesLabel.setText(capacity + " places libres");
    }

    /**
     * Cancels the session edition.
     *
     * @param event
     */
    public void onCancelButtonClick(ActionEvent event) {
        this.sessionEditPane.setVisible(false);
        refreshAfterEdit();
    }

    /**
     * Displays a session in the sessions list.
     * Set the button on click event to display the session edition pane.
     *
     * @param movieSession
     */

    public void createDisplaySessionButton(MovieSession movieSession) {
        Button button = new Button(movieSession.getMovie().getTitle() + " " + movieSession.getTime() + " " + movieSession.getRoom().getNumber());
        button.prefWidthProperty().bind(vBoxToDisplay.widthProperty());

        button.onMouseClickedProperty().set((event -> {
            onSessionButtonClick(movieSession);
            setSelection(button);

        }));
        sessionButtons.add(button);
    }

    /**
     * Sets the current session ID and the current edition type to "modify".
     *
     * @param movieSession
     */
    private void onSessionButtonClick(MovieSession movieSession) {
        this.currentSessionID = movieSession.getId();
        System.out.println("l'ID de la session est " + currentSessionID);
        this.currentEditionType = "modify";
        sessionEditPane.setVisible(true);
        this.deleteButton.setVisible(true);
        this.editTypeInfoLabel.setText("Modifier une séance");
        this.sessionEditPane.setVisible(true);

        setSessionFields(movieSession);
    }

    /**
     * Sets the fields of the session edition pane.
     *
     * @param movieSession
     */

    private void setSessionFields(MovieSession movieSession) {
        DateSelector.setValue(movieSession.getDate());
        hourSelector.setValue(movieSession.getHourFromTime());
        minuteSelector.setValue(movieSession.getMinuteFromTime());
        movieSelector.setValue(movieSession.getMovie().getTitle());
        roomSelector.setValue(movieSession.getRoom().getNumber());
        versionSelector.setValue(movieSession.getVersion());
    }

    /**
     * Clears the fields of the session edition pane.
     */

    private void clearFields() {
        DateSelector.setValue(null);
        hourSelector.setValue(null);
        minuteSelector.setValue(null);
        movieSelector.setValue(null);
        roomSelector.setValue(null);
        versionSelector.setValue(null);
        timeShowLabel.setText("...");
        freePlacesLabel.setText("");
    }

    /**
     * Clears the sessions list.
     */

    public void clearSessions() {
        sessionsList.setContent(null);
        sessionButtons.clear();
    }

    /**
     * Refreshes the session edition pane after an edit.
     */

    public void refreshAfterEdit() {
        clearFields();
        setInitialStyleButtons();
        this.currentEditionType = "";
        this.currentSessionID = -1;
        sessionEditPane.setVisible(false);
    }

    /**
     * Sends the current session ID to the listener to delete it.
     */

    public void onDeleteButtonClick(ActionEvent event) {
        listener.onDeleteButtonClick(currentSessionID);
    }

    public void displaySessions() {
        vBoxToDisplay.getChildren().clear();
        sessionButtons.add(addButton());
        vBoxToDisplay.getChildren().addAll(sessionButtons);
        sessionsList.setContent(vBoxToDisplay);
        setInitialStyleButtons();
    }


    public void highlightConflictingSessions(List<Integer> conflictingSessionsIds) {
        for (Button button : sessionButtons) {
            try {
                int buttonIndex = sessionButtons.indexOf(button);
                if (conflictingSessionsIds.contains(getMovieSessionById(buttonIndex).getId())) {
                    button.getStyleClass().add("conflict");
                }
            }catch (IndexOutOfBoundsException ignored){
            }
        }
    }

    public void onRoomIDChoiced(ActionEvent event) {
        if(roomToEditSelector.getValue().equals("+")) {
            currentRoomEditionType = "add";
        }else {
            currentRoomEditionType = "modify";
        }
    }

    /**
     * Interface for the session manager view listener.
     */

    public interface SessionManagerViewListener {
        void onValidateButtonClick(Integer sessionId, Integer movieId, Integer roomId, String version, String convertedDateTime, String currentEditType) throws SQLException, InvalideFieldsExceptions;

        void setPossibleMovies();

        Integer getMovieDuration(int id);

        void setPossibleRooms();

        Movie getMovieFrom(Integer currentSelection);

        void onRoomSelectedEvent(Integer value) throws SQLException;


        void onDeleteButtonClick(int currentSessionID);

        MovieSession getMovieSessionById(int id);

        void onRoomValidateButtonClick(Integer roomNumber, Integer freePlaces, String currentRoomEditionType);

        void onRoomCancelButtonClick();
    }

    /**
     * Sets the possible movies names that can be selected in the view.
     */

    public void setPossibleMovies() {
        listener.setPossibleMovies();
    }

    /**
     * Sets the possible rooms that can be selected in the view.
     */

    public void setPossibleRooms() {
        listener.setPossibleRooms();

    }

    /**
     * Display the time of the session if a movie and a time are selected.
     *
     * @return
     */

    public void onHourSelectedEvent(ActionEvent e) {
        if (!(minuteSelector.getValue() == null) && movieSetted() && !(hourSelector.getValue() == null)) {
            setTimeShowLabel();
        }
    }


    /**
     * Display the time of the session if a movie and a time are selected.
     *
     * @param e
     */
    public void onMinuteSelectedEvent(ActionEvent e) {
        if (!(hourSelector.getValue() == null) && movieSetted() && !(minuteSelector.getValue() == null)) {
            setTimeShowLabel();
        }
    }

    /**
     * Sets the time show label with the beginning and the end of the session.
     */

    private void setTimeShowLabel() {
        Integer hour = Integer.parseInt(hourSelector.getValue());
        Integer minute = Integer.parseInt(minuteSelector.getValue());
        int movieId = getMovie(currentMovieSelection).getId();
        Integer duration = getMovieDuration(movieId);
        LocalTime time = LocalTime.of(hour, minute);
        LocalTime time1 = time.plusMinutes(duration);
        timeShowLabel.setText(time + " -> " + time1);
    }

    /**
     * Sends an id to the listener to get the movie from it.
     *
     * @param id
     * @return
     */

    private Movie getMovie(int id) {
        return listener.getMovieFrom(id);
    }

    /**
     * Sends an id to the listener to get the duration of the movie from it.
     *
     * @param id
     * @return
     */

    private Integer getMovieDuration(int id) {
        return listener.getMovieDuration(id);
    }

    /**
     * Sets the current movie selection.
     *
     * @param e
     */

    @FXML
    private void onMovieSelectedEvent(ActionEvent e) {
        currentMovieSelection = movieSelector.getSelectionModel().getSelectedIndex();
        if (timeSetted()) {
            setTimeShowLabel();
        }
        System.out.println(currentMovieSelection);
    }

    /**
     * Returns true if the time is setted.
     *
     * @return
     */

    private boolean timeSetted() {
        return !(hourSelector.getValue() == null || minuteSelector.getValue() == null);
    }

    /**
     * Returns true if the movie is setted.
     *
     * @return
     */

    private boolean movieSetted() {
        return !(movieSelector.getValue() == null);
    }

    /**
     * Converts the date and the hour to a date time format.
     *
     * @return
     */
    private String converDateAndHourToDateTime() {
        try {
            return DateSelector.getValue().toString() + " " + hourSelector.getValue() + ":" + minuteSelector.getValue();
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Sets the initial style of the buttons.
     */

    private void setInitialStyleButtons() {
        for (int i = 0; i < sessionButtons.size() - 1; i++) {
            sessionButtons.get(i).getStyleClass().set(0, "buttonS");
            sessionButtons.get(i).getStyleClass().remove("Selected");
        }
    }

    /**
     * Sets the selection style of the selected button.
     *
     * @param button
     */

    public void setSelection(Button button) {
        try {
            setInitialStyleButtons();
            button.getStyleClass().add("Selected");
        } catch (IndexOutOfBoundsException e) {

        }
    }

    /**
     * Clears the possible names of the movie selector.
     */

    public void clearPossibleNames() {
        movieSelector.getItems().clear();
    }

    public MovieSession getMovieSessionById(int id) {
        return listener.getMovieSessionById(id);
    }
    
    //Specific methods for the room manager
    
    public void onRoomValidateButtonClick(ActionEvent event) {
        listener.onRoomValidateButtonClick(Integer.parseInt(roomToEditSelector.getValue()), Integer.parseInt(freePlacesSetter.getText()), currentRoomEditionType);
    }
    
    public void onRoomCancelButtonClick(ActionEvent event) {
        listener.onRoomCancelButtonClick();
    }
    
    public void setRoomToEditSelector(List<String> roomNumbers) {
        roomToEditSelector.getItems().clear();
        for (String i : roomNumbers) {
            roomToEditSelector.getItems().add(i);
        }
    }

    public void clearRooms() {
        roomToEditSelector.getItems().clear();
        freePlacesSetter.clear();
    }
    
    


}
