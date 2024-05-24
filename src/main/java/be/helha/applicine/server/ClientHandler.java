package be.helha.applicine.server;

import be.helha.applicine.common.models.*;
import be.helha.applicine.common.models.request.*;
import be.helha.applicine.common.network.ObjectSocket;
import be.helha.applicine.server.dao.*;
import be.helha.applicine.server.dao.impl.*;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for handling the requests from the client to the server.
 */
public class ClientHandler extends Thread implements RequestVisitor {
    private final ObjectSocket objectSocket;
    private final MovieDAO movieDAO;
    private final ClientsDAO clientsDAO;
    private final TicketDAO ticketDAO;
    private final RoomDAO roomDAO;
    private final ViewableDAO viewableDAO;
    private final SessionDAO sessionDAO;
    private final Server server;

    public ClientHandler(ObjectSocket socket, Server server) {
        this.server = server;
        this.objectSocket = socket;
        this.movieDAO = new MovieDAOImpl();
        this.clientsDAO = new ClientsDAOImpl();
        this.roomDAO = new RoomDAOImpl();
        this.sessionDAO = new SessionDAOImpl();
        this.ticketDAO = new TicketDAOImpl();
        this.viewableDAO = new ViewableDAOImpl();
    }

    public void run() {
        try {
            ClientEvent event;
            while ((event = objectSocket.read()) != null) {
                event.dispatchOn(this);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error handling client: " + e.getMessage());
            System.out.println("Client disconnected");
            server.getClientsConnected().remove(this);
        }
    }

    public void writeToClient(Object object) {
        try {
            objectSocket.write(object);
        } catch (IOException e) {
            System.out.println("Error writing to client: " + e.getMessage());
        }
    }

    public void broadcast(Object object) {
        for (ClientHandler client : server.getClientsConnected()) {
            client.writeToClient(object);
        }
    }

    public void sendViewableListToAllClients() {
        GetViewablesRequest request = new GetViewablesRequest();
        request.setViewables(viewableDAO.getAllViewables());
        broadcast(request);
    }

    @Override
    public void visit(AddSessionRequest addSessionRequest) {
        processSessionRequest(addSessionRequest, false);
    }

    @Override
    public void visit(UpdateSessionRequest updateSessionRequest) {
        processSessionRequest(updateSessionRequest, true);
    }

    private void processSessionRequest(SessionRequest sessionRequest, boolean isUpdate) {
        MovieSession session = sessionRequest.getSession();
        List<Integer> sessionsWithConflict;
        try {
            sessionsWithConflict = sessionDAO.checkTimeConflict(session.getId(), session.getRoom().getNumber(), session.getTime(), session.getViewable().getDuration());
        } catch (SQLException e) {
            sessionRequest.setSuccess(false);
            sessionRequest.setMessage("Erreur lors de la vérification des conflits de temps.");
            writeToClient(sessionRequest);
            throw new RuntimeException(e);
        }

        if (!sessionsWithConflict.isEmpty()) {
            sessionRequest.setSuccess(false);
            sessionRequest.setConflictedSessions(sessionsWithConflict);
            sessionRequest.setMessage("Conflit de temps avec des séances existantes.");
            writeToClient(sessionRequest);
            return;
        }

        try {
            if (isUpdate) {
                sessionDAO.update(session);
            } else {
                sessionDAO.create(session);
            }
            sessionRequest.setSuccess(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        writeToClient(sessionRequest);
    }


    @Override
    public void visit(GetRoomsRequest getRoomsRequest) {
        try {
            getRoomsRequest.setRooms(roomDAO.getAll());
            System.out.println("Rooms: " + getRoomsRequest.getRooms());
            writeToClient(getRoomsRequest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void visit(DeleteViewableRequest deleteViewableRequest) {

        int viewableId = deleteViewableRequest.getViewableId();

        ArrayList<Integer> sessionsLinkedToViewable = viewableDAO.getSeancesLinkedToViewable(viewableId);

        if (sessionsLinkedToViewable.size() > 0) {
            deleteViewableRequest.setSuccess(false);
            deleteViewableRequest.setMessage("Vous ne pouvez pas supprimer une saga si des séances lui sont attribuées.");
            writeToClient(deleteViewableRequest);
            return;
        }

        if (viewableDAO.removeViewable(viewableId)) {
            deleteViewableRequest.setSuccess(true);
            writeToClient(deleteViewableRequest);
        } else {
            deleteViewableRequest.setSuccess(false);
            deleteViewableRequest.setMessage("Erreur lors de la suppression de la saga.");
            writeToClient(deleteViewableRequest);
        }
        sendViewableListToAllClients();
    }

    @Override
    public void visit(GetViewablesRequest getViewablesRequest) {
        System.out.println("GetView request received");
        getViewablesRequest.setViewables(viewableDAO.getAllViewables());
        writeToClient(getViewablesRequest);
    }

    @Override
    public void visit(AddViewableRequest addViewableRequest) {
        Saga saga = (Saga) addViewableRequest.getViewable();
        ArrayList<Movie> viewables = saga.getMovies();
        ArrayList<Integer> ids = new ArrayList<>();
        for (Movie viewable : viewables) {
            ids.add(viewable.getId());
        }
        viewableDAO.addViewable(saga.getTitle(), "Saga", ids);
        addViewableRequest.setSuccess(true);
        writeToClient(addViewableRequest);
        sendViewableListToAllClients();
    }

    //Broadcast
    @Override
    public void visit(UpdateViewableRequest updateViewableRequest) {
        Saga saga = updateViewableRequest.getSaga();
        ArrayList<Movie> sagaMovies = saga.getMovies();
        ArrayList<Integer> ids = new ArrayList<>();
        for (Movie movie : sagaMovies) {
            ids.add(movie.getId());
        }
        viewableDAO.updateViewable(saga.getId(), saga.getTitle(), "Saga", ids);
        updateViewableRequest.setSuccess(true);
        writeToClient(updateViewableRequest);
        sendViewableListToAllClients();
    }

    @Override
    public void visit(CreateMovieRequest createMovieRequest) {
        try {
            Movie movie = createMovieRequest.getMovie();
            movie.setImagePath(FileManager.createPath(removeSpecialCharacters(movie.getTitle()) + ".jpg"));
            FileManager.createImageFromBytes(movie.getImage(), movie.getImagePath());
            movieDAO.create(movie);
            createMovieRequest.setStatus(true);
            writeToClient(createMovieRequest);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        sendViewableListToAllClients();
    }

    public static String removeSpecialCharacters(String str) {
        return str.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

    @Override
    public void visit(ClientRegistrationRequest clientRegistrationRequest) {
        Client client = clientRegistrationRequest.getClient();
        try {
            String hashedPassword = HashedPassword.getHashedPassword(client.getPassword());
            Client registeredClient = clientsDAO.create(new Client(client.getName(), client.getEmail(), client.getUsername(), hashedPassword));
            if (registeredClient != null) {
                clientRegistrationRequest.setSuccess(true);
            } else {
                clientRegistrationRequest.setSuccess(false);
            }
            writeToClient(clientRegistrationRequest);
        } catch (IOException e) {
            writeToClient("Error during registration: " + e.getMessage());
        }
    }

    @Override
    public void visit(DeleteMoviesRequest deleteMoviesRequest) {

        if (movieDAO.getSessionLinkedToMovie(deleteMoviesRequest.getId()) > 0 || viewableDAO.sagasLinkedToMovie(deleteMoviesRequest.getId()) > 0) {
            deleteMoviesRequest.setStatus(false);
            deleteMoviesRequest.setMessage("Vous ne pouvez pas supprimer un film si des séances ou de sagas lui sont attribués.");
            writeToClient(deleteMoviesRequest);
            return;
        }

        try {
            movieDAO.delete(deleteMoviesRequest.getId());
            deleteMoviesRequest.setStatus(true);
            writeToClient(deleteMoviesRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendViewableListToAllClients();
    }

    @Override
    public void visit(GetAllSessionRequest getAllSessionRequest) {
        try {
            List<MovieSession> sessions = sessionDAO.getAll();
            getAllSessionRequest.setSessions(sessions);
            writeToClient(getAllSessionRequest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(CheckLoginRequest checkLoginRequest) {
        String username = checkLoginRequest.getUsername();
        String password = checkLoginRequest.getPassword();
        Client client = clientsDAO.getClientByUsername(username);
        if (client != null && HashedPassword.checkPassword(password, client.getPassword())) {
            checkLoginRequest.setClient(client);
            writeToClient(checkLoginRequest);
        } else {
            checkLoginRequest.setClient(null);
            writeToClient(checkLoginRequest);
        }
    }

    @Override
    public void visit(GetMoviesRequest getMoviesRequest) {
        try {
            List<Movie> movies = movieDAO.getAll();
            for (Viewable movie : movies) {
                movie.setImage(FileManager.getImageAsBytes(movie.getImagePath()));
            }
            getMoviesRequest.setMovies(movies);
            writeToClient(getMoviesRequest);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetSessionByMovieId getSessionByMovieId) {
        int movieId = getSessionByMovieId.getMovieId();
        try {
            List<MovieSession> sessions = sessionDAO.getSessionsForMovie(viewableDAO.getViewableById(movieId));
            getSessionByMovieId.setSessions(sessions);
            writeToClient(getSessionByMovieId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetTicketByClientRequest getTicketByClientRequest) {
        int clientId = getTicketByClientRequest.getClientId();
        List<Ticket> tickets = ticketDAO.getTicketsByClient(clientId);
        getTicketByClientRequest.setTickets(tickets);
        writeToClient(getTicketByClientRequest);
    }

    @Override
    public void visit(GetSessionByIdRequest getSessionByIdRequest) {
        int sessionId = getSessionByIdRequest.getSessionId();
        try {
            MovieSession session = sessionDAO.get(sessionId);
            getSessionByIdRequest.setSession(session);
            writeToClient(getSessionByIdRequest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(CreateTicketRequest createTicketRequest) {
        Ticket ticket = createTicketRequest.getTicket();
        ticketDAO.create(ticket);
        createTicketRequest.setStatus(true);
        writeToClient(createTicketRequest);
    }

    @Override
    public void visit(DeleteSessionRequest deleteSessionRequest) {
        try {
            int sessionId = deleteSessionRequest.getSessionId();
            sessionDAO.delete(sessionId);
            deleteSessionRequest.setSuccess(true);
            writeToClient(deleteSessionRequest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test de broadcast. Au lancement le client envoie un ping vers le serveur qui le renvoie à tous les clients connectés (dont lui-même).
     *
     * @param pingServer le ping envoyé par le client
     */
    @Override
    public void visit(PingServer pingServer) {
        System.out.println("Ping received from client");
        writeToClient(pingServer);
        System.out.println("Clients connected: " + server.getClientsConnected());
        for (ClientHandler client : server.getClientsConnected()) {
            if (client != this) {
                client.writeToClient(pingServer);
            }
        }
    }

    @Override
    public void visit(UpdateMovieRequest updateMovieRequest) {
        Movie movie = updateMovieRequest.getMovie();
        try {
            if (movie.getImage() != null) {
                movie.setImagePath(FileManager.createPath(removeSpecialCharacters(movie.getTitle()) + ".jpg"));
                FileManager.createImageFromBytes(movie.getImage(), movie.getImagePath());
            }
            movieDAO.update(movie);
            updateMovieRequest.setStatus(true);
            writeToClient(updateMovieRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendViewableListToAllClients();
    }
}