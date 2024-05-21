package be.helha.applicine.client.controllers;

import be.helha.applicine.client.views.AlertViewController;
import be.helha.applicine.common.network.ServerConstants;
import kotlin.reflect.KParameter;

import java.io.*;
import java.net.*;

public class ServerRequestHandler {
    private static ServerRequestHandler instance;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * (Singleton) Constructor for the ServerRequestHandler class.
     * @throws IOException if an I/O error occurs when creating the socket.
     */
    private ServerRequestHandler() throws IOException {
        clientSocket = new Socket(ServerConstants.HOST, ServerConstants.PORT);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Sends a request to the server.
     * @param request the request to send to the server.
     * @param <T> the type of the response.
     * @return the response from the server.
     */
    public <T> T sendRequest(Object request) {
        try {
            out.writeObject(request);
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            AlertViewController.showErrorMessage("Erreur lors de la connexion au serveur, veuillez réessayer plus tard.");
            return null;
        }
    }

    /**
     * @return the instance of the ServerRequestHandler class.
     * @throws IOException if an I/O error occurs when creating the socket.
     */
    public static ServerRequestHandler getInstance() throws IOException {
        if (instance == null) {
            instance = new ServerRequestHandler();
        }
        return instance;
    }

    /**
     * Closes the input and output streams and the client socket.
     * @throws IOException if an I/O error occurs when closing the streams or the socket.
     */
    public void close() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}