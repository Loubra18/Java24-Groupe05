package be.helha.applicine.client.views;

import be.helha.applicine.common.models.Client;
import be.helha.applicine.common.models.Ticket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;

/**
 * Classe qui gère la vue de la page du compte client, affiche les tickets du client et permet de retourner à la page client.
 */
public class ClientAccountControllerView {

    @FXML
    private Label LabelNom;
    @FXML
    private Label LabelPseudo;
    @FXML
    private Label LabelEmail;
    @FXML
    private Label LabelPassword;

    private static Stage accountWindow;
    private ClientAccountListener listener;
    @FXML
    private ListView<HBox> ticketContainer;

    /**
     * Permet de définir le listener de la page du compte client.
     * @param listener le listener de la page du compte client.
     */
    public void setListener(ClientAccountListener listener) {
        this.listener = listener;
    }

    /**
     * Permet de récupérer la fenêtre de la page du compte client.
     * @return la fenêtre de la page du compte client.
     */
    public Window getAccountWindow() {
        return accountWindow;
    }

    /**
     * Permet de récupérer l'URL du fichier FXML de la page du compte client.
     * @return l'URL du fichier FXML de la page du compte client.
     */
    public static URL getFXMLResource() {
        return ClientAccountControllerView.class.getResource("ClientAccount.fxml");
    }

    /**
     * Permet de définir la fenêtre de la page du compte client et de créer une nouvelle scène pour la page du compte client.
     * @param fxmlLoader le chargeur FXML de la page du compte client.
     * @param listener le listener de la page du compte client.
     * @throws IOException si une erreur survient lors de la création de la fenêtre de la page du compte client.
     */
    public static void setStageOf(FXMLLoader fxmlLoader, ClientAccountListener listener) throws IOException {
        accountWindow = new Stage(); //crée une nouvelle fenêtre
        accountWindow.setOnCloseRequest(event -> {
            try {
                AlertViewController.showInfoMessage("Vos données n'ont pas étés enregistrées.");
                listener.toClientSide();
            } catch (Exception e) {
                AlertViewController.showErrorMessage("Erreur lors de la fermeture de la fenêtre du compte client.");
            }
        });
        Scene scene = new Scene(fxmlLoader.load()); //charge le fichier FXML et crée une nouvelle scène en définissant sa taille
        accountWindow.setScene(scene); //définit la scène de la fenêtre
        accountWindow.setTitle("Client Account"); //définit le titre de la fenêtre
        accountWindow.show();
    }

    /**
     * Gère l'événement du clic sur le bouton "Retour" de la page du compte client.
     * @param actionEvent le listener de la page du compte client.
     */
    public void onCloseButtonClicked(ActionEvent actionEvent) {
        listener.toClientSide();
    }

    /**
     * Permet d'ajouter un ticket à la liste des tickets du client.
     * @param ticket le ticket à ajouter.
     * @throws IOException relève une erreur survient lors de l'ajout du ticket.
     */
    public void addTicket(Ticket ticket) throws IOException{
        FXMLLoader ticketPane = new FXMLLoader(TicketPaneViewController.getFXMLResource());
        System.out.println("ticketPane: " + ticketPane);
        HBox pane = ticketPane.load();
        System.out.println("pane: " + pane);
        TicketPaneViewController controller = ticketPane.getController();
        controller.setTicket(ticket);
        ticketContainer.getItems().add(new HBox(pane));
    }

    /**
     * Permet de remplir les labels de la page du compte client.
     * @param client le client dont les informations doivent être affichées.
     */
    public void fillLabels(Client client) {
        LabelNom.setText(client.getName());
        LabelEmail.setText(client.getEmail());
        LabelPseudo.setText(client.getUsername());
    }

    /**
     * Permet d'initialiser la page du compte client.
     * @param client le client dont les informations doivent être affichées.
     */
    public void initializeClientAccountPage(Client client) {
        fillLabels(client);
    }

    /**
     * Interface qui définit les méthodes du listener de la page du compte client.
     * Permet de retourner à la fenêtre du client, de retourner à la page du compte client et de récupérer le client du compte.
     */
    public interface ClientAccountListener {

        //je retourne à la fenêtre du client
        void toClientSide();

        void toClientAccount();

        Client getClientAccount();
    }
}

