package be.helha.applicine.server;

import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.models.Client;
import be.helha.applicine.common.network.ServerConstants;
import be.helha.applicine.server.dao.ClientsDAO;
import be.helha.applicine.server.dao.MovieDAO;
import be.helha.applicine.server.dao.RoomDAO;
import be.helha.applicine.server.dao.impl.ClientsDAOImpl;
import be.helha.applicine.server.dao.impl.MovieDAOImpl;
import be.helha.applicine.server.dao.impl.RoomDAOImpl;
import be.helha.applicine.server.database.ApiRequest;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.*;
import java.sql.SQLException;

public class Server {

    public static void main(String[] args) throws IOException {
        try {
            initializeAppdata();
            ServerSocket serverSocket = new ServerSocket(ServerConstants.PORT);
            System.out.println("Server is running on port " + ServerConstants.PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException | SQLException e) {
            //No need to show the error message to the user as it is a server error
            System.out.println("Server could not start");
            System.out.println(e.getMessage());
        }
    }

    private static void initializeAppdata() throws SQLException{
        FileManager.createDataFolder();
        MovieDAO movieDAO = new MovieDAOImpl();
        ClientsDAO clientsDAO = new ClientsDAOImpl();
        if (movieDAO.isMovieTableEmpty()) {
            ApiRequest apiRequest = new ApiRequest();
            apiRequest.fillDatabase();
        }
        if (clientsDAO.isClientTableEmpty()) {
            clientsDAO.create(new Client("John Doe", "john.doe@example.com", "johndoe", "motdepasse"));
        }
        RoomDAO roomDAO = new RoomDAOImpl();
        if (roomDAO.isRoomTableEmpty()) {
            roomDAO.fillRoomTable();
        }
    }
}