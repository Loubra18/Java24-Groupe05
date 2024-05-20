package be.helha.applicine.client.views;

import be.helha.applicine.common.models.Client;
import be.helha.applicine.common.models.Ticket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;



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

    public void setListener(ClientAccountListener listener) {
        this.listener = listener;
    }

    public Window getAccountWindow() {
        return accountWindow;
    }

    public static URL getFXMLResource() {
        return ClientAccountControllerView.class.getResource("ClientAccount.fxml");
    }

    //utilisée pour initialiser et afficher une nouvelle fenêtre (ou "stage") dans une application JavaFX
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
    public void onCloseButtonClicked(ActionEvent actionEvent) {
        //TO DO j'informe le client que ses modifications ne seront pas enregistrées
        listener.toClientSide();
    }

    public void addTicket(Ticket ticket) throws Exception{
        FXMLLoader ticketPane = new FXMLLoader(TicketPaneViewController.getFXMLResource());
        System.out.println("ticketPane: " + ticketPane);
        HBox pane = ticketPane.load();
        System.out.println("pane: " + pane);
        TicketPaneViewController controller = ticketPane.getController();
        controller.setTicket(ticket);
        ticketContainer.getItems().add(new HBox(pane));
    }

    public void fillLabels(Client client) {
        LabelNom.setText(client.getName());
        LabelEmail.setText(client.getEmail());
        LabelPseudo.setText(client.getUsername());
    }

    public void initializeClientAccountPage(Client client) {
        try {
            fillLabels(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ClientAccountListener {

        //je retourne à la fenêtre du client
        void toClientSide();

        void toClientAccount() throws Exception;

        Client getClientAccount() throws Exception;
    }


}

