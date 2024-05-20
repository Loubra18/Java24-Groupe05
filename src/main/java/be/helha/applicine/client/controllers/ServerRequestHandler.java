package be.helha.applicine.client.controllers;

import be.helha.applicine.common.models.event.Event;
import be.helha.applicine.common.models.event.EventListener;
import be.helha.applicine.common.network.ServerConstants;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerRequestHandler {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    //listeners est une liste d'objets qui implémentent l'interface EventListener, donc qui veulent écouter les events
    //C'est une liste d'objets qui implémentent l'interface EventListener (donc qui ont une méthode onEventReceived)
    private ArrayList<EventListener> listeners = new ArrayList<>();
    private boolean isWaitingForEvents = false;

    public ServerRequestHandler() throws IOException {
        clientSocket = new Socket(ServerConstants.HOST, ServerConstants.PORT);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        listenForEvents();
    }

    public Object sendRequest(Object request) throws IOException, ClassNotFoundException {
        isWaitingForEvents = false;
        System.out.println("Le client envoie une requête au serveur");
        out.writeObject(request);
        Object response = in.readObject();
        System.out.println("Le client a reçu une réponse du serveur, il attend maintenant des events");
        isWaitingForEvents = true;
        return response;
    }

    public void close() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    //addListener permet d'ajouter un objet qui implémente l'interface EventListener à la liste des listeners
    public void addEventListener(EventListener listener) {
        listeners.add(listener);
    }
    //removeListener permet de retirer un objet qui implémente l'interface EventListener de la liste des listeners
    public void removeEventListener(EventListener listener) {
        listeners.remove(listener);
    }

    //je lance un thread qui écoute en permanence les events envoyés par le serveur pour chaque client
    public void listenForEvents() {
        new Thread(() -> {
            while (true) {
                try {
                    if(isWaitingForEvents) {
                        Object obj = in.readObject();
                        System.out.println("Objet recu: " + obj);
                        if (obj instanceof Event event) {
                            for (EventListener listener : listeners) {
                                listener.onEventReceived(event);
                            }
                        } else {
                            System.out.println("Received object is not an event");
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}