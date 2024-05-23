package be.helha.applicine.client.controllers;

import be.helha.applicine.common.models.request.*;
import be.helha.applicine.common.network.ServerConstants;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.sql.SQLException;

public class ServerRequestHandler extends Thread implements RequestVisitor {
    private static ServerRequestHandler instance;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private OnClientEventReceived listener;


    /**
     * (Singleton) Constructor for the ServerRequestHandler class.
     *
     * @throws IOException if an I/O error occurs when creating the socket.
     */
    private ServerRequestHandler() throws IOException {
        clientSocket = new Socket(ServerConstants.HOST, ServerConstants.PORT);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        this.setDaemon(true);
    }

    public void setOnViewablesReceivedListener(OnClientEventReceived listener) {
        this.listener = listener;
    }

    /**
     * @return the instance of the ServerRequestHandler class.
     * @throws IOException if an I/O error occurs when creating the socket.
     */
    public static ServerRequestHandler getInstance() throws IOException {
        if (instance == null) {
            synchronized (ServerRequestHandler.class) {
                if (instance == null) {
                    instance = new ServerRequestHandler();
                    instance.start();
                }
            }
        }
        return instance;
    }

    /**
     * Sends a request to the server.
     *
     * @param clientEvent the request to send.
     * @throws IOException if an I/O error occurs when sending the request.
     */
    public void sendRequest(ClientEvent clientEvent) throws IOException {
            out.writeObject(clientEvent);
    }

    @Override
    public synchronized void run() {
        try {
            ClientEvent clientEvent;
            while ((clientEvent = (ClientEvent) in.readObject()) != null) {
                System.out.println("Received: " + clientEvent.getClass().getSimpleName());
                clientEvent.dispatchOn(this);
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleClientEvent(ClientEvent clientEvent) {
        Platform.runLater(() -> {
            try {
                listener.onClientEvenReceived(clientEvent);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void visit(CheckLoginRequest checkLoginRequest) {
        handleClientEvent(checkLoginRequest);
    }

    @Override
    public void visit(ClientRegistrationRequest clientRegistrationRequest) throws IOException {
        handleClientEvent(clientRegistrationRequest);
    }

    @Override
    public void visit(DeleteMoviesRequest deleteMoviesRequest) {
        handleClientEvent(deleteMoviesRequest);
    }

    @Override
    public void visit(GetAllSessionRequest getAllSessionRequest) {
        handleClientEvent(getAllSessionRequest);
    }

    @Override
    public void visit(GetMovieByIdRequest getMovieByIdRequest) {
        handleClientEvent(getMovieByIdRequest);
    }

    @Override
    public void visit(GetMoviesRequest getMoviesRequest) {
        handleClientEvent(getMoviesRequest);
    }

    @Override
    public void visit(GetSessionByIdRequest getSessionByIdRequest) {
        handleClientEvent(getSessionByIdRequest);
    }

    @Override
    public void visit(GetSessionByMovieId getSessionByMovieId) {
        handleClientEvent(getSessionByMovieId);
    }

    @Override
    public void visit(GetTicketByClientRequest getTicketByClientRequest) {
        handleClientEvent(getTicketByClientRequest);
    }

    @Override
    public void visit(CreateTicketRequest createTicketRequest) throws IOException {
        handleClientEvent(createTicketRequest);
    }

    @Override
    public void visit(DeleteSessionRequest deleteSessionRequest) throws IOException, SQLException {
        handleClientEvent(deleteSessionRequest);
    }

    @Override
    public void visit(CreateMovieRequest createMovieRequest) throws IOException {
        handleClientEvent(createMovieRequest);
    }

    @Override
    public void visit(GetSessionsLinkedToMovieRequest getSessionsLinkedToMovieRequest) {
        handleClientEvent(getSessionsLinkedToMovieRequest);
    }

    @Override
    public void visit(UpdateViewableRequest updateViewableRequest) throws IOException {
        handleClientEvent(updateViewableRequest);
    }

    @Override
    public void visit(AddViewableRequest addViewableRequest) {
        handleClientEvent(addViewableRequest);
    }

    @Override
    public void visit(GetViewablesRequest getViewablesRequest) throws IOException {
        handleClientEvent(getViewablesRequest);
    }

    @Override
    public void visit(DeleteViewableRequest deleteViewableRequest) {
        handleClientEvent(deleteViewableRequest);
    }

    @Override
    public void visit(GetRoomsRequest getRoomsRequest) {
        handleClientEvent(getRoomsRequest);
    }

    @Override
    public void visit(UpdateSessionRequest updateSessionRequest) {
        handleClientEvent(updateSessionRequest);
    }

    @Override
    public void visit(AddSessionRequest addSessionRequest) {
        handleClientEvent(addSessionRequest);
    }

    @Override
    public void visit(GetRoomByIdRequest getRoomByIdRequest) {
        handleClientEvent(getRoomByIdRequest);
    }

    @Override
    public void visit(UpdateMovieRequest updateMovieRequest) {
        handleClientEvent(updateMovieRequest);
    }

    @Override
    public void visit(GetSagasLinkedToMovieRequest getSagasLinkedToMovieRequest) {
        handleClientEvent(getSagasLinkedToMovieRequest);
    }

    public interface OnClientEventReceived {
        void onClientEvenReceived(ClientEvent clientEvent) throws IOException, ClassNotFoundException;
    }
}