package be.helha.applicine.client.controllers.managercontrollers;

import be.helha.applicine.client.controllers.MasterApplication;
import be.helha.applicine.client.controllers.ServerRequestHandler;
import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.Movie;
import be.helha.applicine.common.models.Saga;
import be.helha.applicine.common.models.Viewable;
import be.helha.applicine.common.models.exceptions.InvalideFieldsExceptions;
import be.helha.applicine.client.views.managerviews.SpecialViewableViewController;
import be.helha.applicine.common.models.request.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SpecialViewableController extends ManagerController implements SpecialViewableViewController.SpecialViewableListener, Observable, InvalidationListener {

    private ManagerController parentController;
    public FXMLLoader specialViewableFxmlLoader;
    public SpecialViewableViewController specialViewableViewController;
    protected List<String> movieTitleList = new ArrayList<>();
    private Movie selectedMovies = null;
    public Viewable selectedSaga = null;
    private List<Movie> addedMovies = new ArrayList<>();
    private String currentEditType = "add";
    private InvalidationListener specialViewablesChangeListener;
    private ServerRequestHandler serverRequestHandler;

    /**
     * Constructeur de la classe SpecialViewableController qui permet de gérer les vues des sagas.
     * @param parentController le controller parent (ManagerController).
     * @throws SQLException si une erreur SQL survient.
     * @throws IOException si une erreur d'entrée/sortie survient.
     * @throws ClassNotFoundException si une classe n'est pas trouvée.
     */
    public SpecialViewableController(MasterApplication parentController) throws SQLException, IOException, ClassNotFoundException {
        super(parentController);
        this.serverRequestHandler = ServerRequestHandler.getInstance();
        specialViewableFxmlLoader = new FXMLLoader(SpecialViewableViewController.getFXMLResource());
        specialViewableFxmlLoader.load();
        specialViewableViewController = specialViewableFxmlLoader.getController();
        specialViewableViewController.setListener(this);
        specialViewableViewController.init();
    }

    /**
     * Setter pour le controller parent.
     * @param parentController le controller parent (ManagerController).
     */
    public void setParentController(ManagerController parentController) {
        this.parentController = parentController;
    }

    /**
     * Démarre la vue des sagas.
     * @param adminPage la scène de la vue.
     * @throws SQLException si une erreur SQL survient.
     */
    @Override
    public void start(Stage adminPage) throws SQLException {
        specialViewableFxmlLoader = parentController.getSpecialViewableFXML();
        specialViewableViewController = specialViewableFxmlLoader.getController();
        specialViewableViewController.setListener(this);
        specialViewableViewController.init();
    }

    /**
     * Ajoute le film sélectionné à la saga.
     */
    @Override
    public void onAddMovieButtonClick() {
        if (selectedMovies != null && !addedMovies.contains(selectedMovies)) {
            addedMovies.add(selectedMovies);
            specialViewableViewController.fillAddedMovieChoice(getAddedViewablesTitles(), getTotalDuration());
        }
    }

    /**
     * Récupère les titres des films ajoutés.
     * @return la liste des titres des films ajoutés.
     */
    List<String> getAddedViewablesTitles() {
        List<String> addedViewablesTitles = new ArrayList<>();
        for (Viewable viewable : addedMovies) {
            addedViewablesTitles.add(viewable.getTitle());
        }
        return addedViewablesTitles;
    }

    /**
     * Supprime le film sélectionné de la saga.
     */
    @Override
    public void onRemoveMovieButtonClick() {
        if (selectedMovies != null && addedMovies.contains(selectedMovies)) {
            addedMovies.remove(selectedMovies);
        } else {
            try {
                addedMovies.removeLast();
            } catch (NoSuchElementException e) {
                AlertViewController.showErrorMessage("Aucun film à supprimer");
            }
        }
        specialViewableViewController.fillAddedMovieChoice(getAddedViewablesTitles(), getTotalDuration());
    }

    /**
     * Affiche tous les films.
     * @return la liste des titres des films.
     */
    @Override
    public ArrayList<String> displayAllMovies() {
        movieTitleList = new ArrayList<>();
        movieList = serverRequestHandler.sendRequest(new GetMoviesRequest());
        for (Movie movie : movieList) {
            movieTitleList.add(movie.getTitle());
        }
        return (ArrayList<String>) movieTitleList;
    }

    /**
     * Permet de choisir un film.
     * @param selectedIndex l'index du film sélectionné.
     */
    @Override
    public void onMovieChoising(int selectedIndex) {
        selectedMovies = movieList.get(selectedIndex);
    }

    /**
     * Valide les champs de la saga.
     * @param name le nom de la saga.
     * @throws SQLException si une erreur SQL survient.
     */
    @Override
    public void onValidateButtonClick(String name) throws SQLException {
        try {
            validateFields(name);
            if (this.currentEditType.equals("add"))
                addSagaIntoDB(name, "Saga", getAddedMoviesIds());
            else
                modifySagaInDB(this.selectedSaga.getId(), name, "Saga", getAddedMoviesIds());
            AlertViewController.showInfoMessage("La saga a été ajoutée/modifiée avec succès");
        } catch (InvalideFieldsExceptions e) {
            AlertViewController.showErrorMessage("Certains champs n'ont pas été remplis correctement: " + e.getMessage());
        }
        specialViewableViewController.refresh();
        notifyListeners(); //Permettra aux sessions de disposer des nouvelles sagas/ supprimer les anciennes
    }

    /**
     * Modifie la saga dans la base de données.
     * @param id l'identifiant de la saga.
     * @param name le nom de la saga.
     * @param type le type de la saga.
     * @param addedMoviesIds la liste des identifiants des films ajoutés.
     */
    private void modifySagaInDB(int id, String name, String type, ArrayList<Integer> addedMoviesIds) {
        ArrayList<Movie> movies = getMoviesByIDs(addedMoviesIds);
        serverRequestHandler.sendRequest(new UpdateViewableRequest(new Saga(id, name, null, null, getTotalDuration(), null, null, null, movies)));
    }

    /**
     * Récupère les identifiants des films ajoutés.
     * @return la liste des identifiants des films ajoutés.
     */
    ArrayList<Integer> getAddedMoviesIds() {
        ArrayList<Integer> addedMoviesIds = new ArrayList<>();
        for (Movie movie : addedMovies) {
            addedMoviesIds.add(movie.getId());
        }
        return addedMoviesIds;
    }

    /**
     * Ajoute la saga dans la base de données.
     * @param name le nom de la saga.
     * @param type le type de la saga.
     * @param addedMoviesIds la liste des identifiants des films ajoutés.
     */
    private void addSagaIntoDB(String name, String type, ArrayList<Integer> addedMoviesIds) {
        ArrayList<Movie> movies = getMoviesByIDs(addedMoviesIds);
        serverRequestHandler.sendRequest(new AddViewableRequest(new Saga(0, name, null, null, getTotalDuration(), null, null, null, movies)));
    }

    /**
     * Récupère les films par plusieurs identifiants.
     * @param addedMoviesIds la liste des identifiants des films ajoutés.
     * @return la liste des films par rapport aux identifiants.
     */
    @NotNull
    private ArrayList<Movie> getMoviesByIDs(ArrayList<Integer> addedMoviesIds) {
        ArrayList<Movie> movies = new ArrayList<>();
        for (int id : addedMoviesIds) {
            Movie movie = serverRequestHandler.sendRequest(new GetMovieByIdRequest(id));
            movies.add(movie);
        }
        return movies;
    }

    /**
     * Valide les champs de la saga.
     * @param name le nom de la saga.
     * @throws InvalideFieldsExceptions si les champs ne sont pas valides.
     */
    private void validateFields(String name) throws InvalideFieldsExceptions {
        if (addedMovies.size() < 2 || name.isEmpty()) {
            throw new InvalideFieldsExceptions("Certains champs n'ont pas été remplis correctement");
        }
    }

    /**
     * Gère l'annulation de la saga. Si confirmé, la saga est avortée.
     */
    @Override
    public void onCancelButtonClick() {
        boolean confirm = AlertViewController.showConfirmationMessage("Voulez-vous vraiment quitter ?");
        if (confirm) {
            specialViewableViewController.onCancelConfirm();
        }
    }

    /**
     * Affiche les sagas.
     */
    @Override
    public void displaySagas() {
        ArrayList<Viewable> viewables = serverRequestHandler.sendRequest(new GetViewablesRequest());
        for (Viewable viewable : viewables) {
            //si le type dynamique d'un objet viewable est une saga, on l'affiche dans le list view
            if (viewable instanceof Saga) {
                System.out.println(viewable.getTitle());
                specialViewableViewController.displaySaga(viewable);
            }
        }
        specialViewableViewController.addAddButton();
    }

    /**
     * Modifie la saga.
     * @param viewable la vue.
     */
    @Override
    public void onSagaDisplayButtonClick(Viewable viewable) {
        this.selectedSaga = viewable;
        currentEditType = "modify";
        addedMovies = new ArrayList<>();
        addedMovies.addAll(((Saga) viewable).getMovies());
        movieTitleList = new ArrayList<>();
        specialViewableViewController.showSagaForEdit(viewable.getTitle(), getAddedViewablesTitles(), getTotalDuration());
    }

    /**
     * Ajoute une saga dans la base de données.
     */
    @Override
    public void onAddSagaButtonClick() {
        this.currentEditType = "add";
        addedMovies = new ArrayList<>();
        specialViewableViewController.prepareForAddSaga();
    }

    /**
     * Supprime la saga en cours.
     */
    @Override
    public void onSagaDeleteButtonClick() throws SQLException {
        boolean confirm = AlertViewController.showConfirmationMessage("Voulez vous vraiment supprimer cette saga ?");
        if (confirm) {
            String request;
            request = serverRequestHandler.sendRequest(new DeleteViewableRequest(selectedSaga.getId()));
            if (request.equals("VIEWABLE_NOT_DELETED")) {
                AlertViewController.showErrorMessage("Impossible de supprimer cette saga car des séances y sont liées");
            } else {
                specialViewableViewController.refresh();
                notifyListeners(); //Permettra aux sessions de disposer des nouvelles sagas/ supprimer les anciennes
            }
        }
    }

    /**
     * Récupère la durée totale des films ajoutés.
     * @return la durée totale des films ajoutés.
     */
    private Integer getTotalDuration() {
        int totalDuration = 0;
        for (Movie movie : addedMovies) {
            totalDuration += movie.getDuration();
        }
        return totalDuration;
    }

    /**
     * Ajoute un écouteur, qui sera notifié lorsqu'un changement est effectué.
     * @param invalidationListener l'écouteur.
     */
    @Override
    public void addListener(InvalidationListener invalidationListener) {
        specialViewablesChangeListener = invalidationListener;
    }

    /**
     * Supprime un écouteur.
     * @param invalidationListener l'écouteur.
     */
    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        specialViewablesChangeListener = null;
    }

    /**
     * Notifie les écouteurs.
     */
    private void notifyListeners() {
        if (specialViewablesChangeListener != null) {
            specialViewablesChangeListener.invalidated(this);
        }
    }

    /**
     * Notifie les écouteurs.
     * @param observable l'observable.
     */
    @Override
    public void invalidated(Observable observable) {
        try {
            specialViewableViewController.fillMovieChoice();
        } catch (SQLException error) {
            AlertViewController.showErrorMessage("Erreur lors de la récupération des films. Essaie de la connection au serveur.");
            try {
                serverRequestHandler = ServerRequestHandler.getInstance();
            } catch (IOException testConnection) {
                AlertViewController.showErrorMessage("Erreur lors de la connection au serveur. Veuillez redémarrer le serveur ou contactez un administrateur réseaux.");
            }
        }
    }
}