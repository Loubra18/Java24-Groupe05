package be.helha.applicine.server;

import be.helha.applicine.common.models.Client;
import be.helha.applicine.common.network.ObjectSocket;
import be.helha.applicine.common.network.ServerConstants;
import be.helha.applicine.server.dao.ClientsDAO;
import be.helha.applicine.server.dao.MovieDAO;
import be.helha.applicine.server.dao.RoomDAO;
import be.helha.applicine.server.dao.impl.ClientsDAOImpl;
import be.helha.applicine.server.dao.impl.MovieDAOImpl;
import be.helha.applicine.server.dao.impl.RoomDAOImpl;
import be.helha.applicine.server.database.ApiRequest;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {

    //liste qui contient le nombre de clients connectés
    protected List<ClientHandler> clientsConnected = new ArrayList<>();

    public static void main(String[] args) {
        try {
            initializeAppdata();
            Server server = new Server();
            server.go();
        } catch (IOException e) {
            System.out.println("Error while starting server");
            System.out.println(e.getMessage());
        }
    }

    private void go() throws IOException {
        System.out.println("Starting server...");

        ServerSocket serverSocket = new ServerSocket(ServerConstants.PORT);
        System.out.println("Server started on port " + ServerConstants.PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New connection from " + socket.getInetAddress());
            ObjectSocket objectSocket = new ObjectSocket(socket);
            ClientHandler thread = new ClientHandler(objectSocket, this);
            this.clientsConnected.add(thread);
            System.out.println("Number of clients connected: " + clientsConnected.size());
            thread.start();
        }
    }

    public List<ClientHandler> getClientsConnected() {
        return clientsConnected;
    }

    private static void initializeAppdata() {
        try {
            FileManager.createDataFolder();
        } catch (IOException e) {
            System.out.println("Error while creating data folder");
            System.out.println(e.getMessage());
        }
        MovieDAO movieDAO = new MovieDAOImpl();

        ClientsDAO clientsDAO = new ClientsDAOImpl();

        if (movieDAO.isMovieTableEmpty()) {
            ApiRequest apiRequest = new ApiRequest();
            try {
                apiRequest.fillDatabase();
            } catch (SQLException e) {
                System.out.println("Error while filling database");
                System.out.println(e.getMessage());
            }
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