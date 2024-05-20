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

    private ServerRequestHandler() throws IOException {
        clientSocket = new Socket(ServerConstants.HOST, ServerConstants.PORT);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public <T> T sendRequest(Object request) {
        try {
            out.writeObject(request);
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            AlertViewController.showErrorMessage("Erreur lors de la connexion au serveur, veuillez réessayer plus tard.");
            return null;

        }
    }
    public static ServerRequestHandler getInstance() throws IOException {
        if (instance == null) {
            instance = new ServerRequestHandler();
        }
        return instance;
    }

    public void close() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}