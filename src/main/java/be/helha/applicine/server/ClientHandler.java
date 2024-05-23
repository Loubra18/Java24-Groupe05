package be.helha.applicine.server;

import be.helha.applicine.common.models.*;
import be.helha.applicine.common.models.request.*;
import be.helha.applicine.server.dao.*;
import be.helha.applicine.server.dao.impl.*;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread implements RequestVisitor {

    private Socket clientSocket;
    private MovieDAO movieDAO;
    private ClientsDAO clientsDAO;
    private TicketDAO ticketDAO;
    private RoomDAO roomDAO;

    private ViewableDAO viewableDAO;
    private SessionDAO sessionDAO;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) throws IOException, SQLException {
        this.clientSocket = socket;
        this.movieDAO = new MovieDAOImpl();
        this.clientsDAO = new ClientsDAOImpl();
        this.roomDAO = new RoomDAOImpl();
        this.sessionDAO = new SessionDAOImpl();
        this.ticketDAO = new TicketDAOImpl();
        this.viewableDAO = new ViewableDAOImpl();
        this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        this.in = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void run() {
        try {
            ClientEvent event;
            while ((event = (ClientEvent) in.readObject()) != null) {
                event.dispatchOn(this);
            }
            clientSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEvent(ClientEvent event) {
        try {
            out.writeObject(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetSagasLinkedToMovieRequest getSagasLinkedToMovieRequest) {
        int movieId = getSagasLinkedToMovieRequest.getMovieId();
        int amountSagas = viewableDAO.sagasLinkedToMovie(movieId);
        getSagasLinkedToMovieRequest.setAmountSagas(amountSagas);
        try {
            out.writeObject(getSagasLinkedToMovieRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            out.writeObject(updateMovieRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendEvent(updateMovieRequest);
    }

    @Override
    public void visit(GetRoomByIdRequest getRoomByIdRequest) {
        int id = getRoomByIdRequest.getRoomId();
        try {
            Room room = roomDAO.get(id);
            getRoomByIdRequest.setRoom(room);
            out.writeObject(getRoomByIdRequest);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(AddSessionRequest addSessionRequest) {
        MovieSession session = addSessionRequest.getSession();
        try {
            sessionDAO.create(session);
            addSessionRequest.setSuccess(true);
            out.writeObject(addSessionRequest);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(UpdateSessionRequest updateSessionRequest) {
        MovieSession session = updateSessionRequest.getSession();
        try {
            sessionDAO.update(session);
            updateSessionRequest.setSuccess(true);
            out.writeObject(updateSessionRequest);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetRoomsRequest getRoomsRequest) {
        try {
            getRoomsRequest.setRooms(roomDAO.getAll());
            System.out.println("Rooms: " + getRoomsRequest.getRooms());
            out.writeObject(getRoomsRequest);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void visit(DeleteViewableRequest deleteViewableRequest) {
        int viewableId = deleteViewableRequest.getViewableId();
        if (viewableDAO.removeViewable(viewableId)) {
            try {
                deleteViewableRequest.setSuccess(true);
                out.writeObject(deleteViewableRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                deleteViewableRequest.setSuccess(false);
                out.writeObject(deleteViewableRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void visit(GetViewablesRequest getViewablesRequest) throws IOException {
        try {
            List<Viewable> viewables = viewableDAO.getAllViewables();
            getViewablesRequest.setViewables(viewables);
            out.writeObject(getViewablesRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            addViewableRequest.setSuccess(true);
            out.writeObject(addViewableRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(UpdateViewableRequest updateViewableRequest) {
        Saga saga = updateViewableRequest.getSaga();
        ArrayList<Movie> sagaMovies = saga.getMovies();
        ArrayList<Integer> ids = new ArrayList<>();
        for (Movie movie : sagaMovies) {
            ids.add(movie.getId());
        }
        viewableDAO.updateViewable(saga.getId(), saga.getTitle(), "Saga", ids);
        try {
            updateViewableRequest.setSuccess(true);
            out.writeObject(updateViewableRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetSessionsLinkedToMovieRequest getSessionsLinkedToMovieRequest) {
        int movieId = getSessionsLinkedToMovieRequest.getMovieId();
        int amountSessions = movieDAO.getSessionLinkedToMovie(movieId);
        try {
            getSessionsLinkedToMovieRequest.setAmountSessions(amountSessions);
            out.writeObject(getSessionsLinkedToMovieRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(CreateMovieRequest createMovieRequest) throws IOException {
        Movie movie = createMovieRequest.getMovie();
        movie.setImagePath(FileManager.createPath(removeSpecialCharacters(movie.getTitle()) + ".jpg"));
        FileManager.createImageFromBytes(movie.getImage(), movie.getImagePath());
        movieDAO.create(movie);
        createMovieRequest.setStatus(true);
        out.writeObject(createMovieRequest);
    }

    public static String removeSpecialCharacters(String str) {
        return str.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

    @Override
    public void visit(ClientRegistrationRequest clientRegistrationRequest) throws IOException {
        Client client = clientRegistrationRequest.getClient();
        try {
            String hashedPassword = HashedPassword.getHashedPassword(client.getPassword());
            Client registeredClient = clientsDAO.create(new Client(client.getName(), client.getEmail(), client.getUsername(), hashedPassword));
            if (registeredClient != null) {
                clientRegistrationRequest.setSuccess(true);
            } else {
                clientRegistrationRequest.setSuccess(false);
            }
            out.writeObject(clientRegistrationRequest);
        } catch (IOException e) {
            out.writeObject("Error during registration: " + e.getMessage());
        }
    }

    @Override
    public void visit(DeleteMoviesRequest deleteMoviesRequest) {
        try {
            movieDAO.delete(deleteMoviesRequest.getId());
            deleteMoviesRequest.setStatus(true);
            out.writeObject(deleteMoviesRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetAllSessionRequest getAllSessionRequest) {
        try {
            List<MovieSession> sessions = sessionDAO.getAll();
            getAllSessionRequest.setSessions(sessions);
            out.writeObject(getAllSessionRequest);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(CheckLoginRequest checkLoginRequest) {
        String username = checkLoginRequest.getUsername();
        String password = checkLoginRequest.getPassword();
        Client client = clientsDAO.getClientByUsername(username);
        if (client != null && HashedPassword.checkPassword(password, client.getPassword())) {
            try {
                checkLoginRequest.setClient(client);
                out.writeObject(checkLoginRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                checkLoginRequest.setClient(null);
                out.writeObject(checkLoginRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            out.writeObject(getMoviesRequest);
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
            out.writeObject(getSessionByMovieId);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetTicketByClientRequest getTicketByClientRequest) {
        int clientId = getTicketByClientRequest.getClientId();
        try {
            List<Ticket> tickets = ticketDAO.getTicketsByClient(clientId);
            getTicketByClientRequest.setTickets(tickets);
            out.writeObject(getTicketByClientRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetSessionByIdRequest getSessionByIdRequest) {
        int sessionId = getSessionByIdRequest.getSessionId();
        try {
            MovieSession session = sessionDAO.get(sessionId);
            getSessionByIdRequest.setSession(session);
            out.writeObject(getSessionByIdRequest);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(GetMovieByIdRequest getMovieByIdRequest) {
        int id = getMovieByIdRequest.getMovieId();
        try {
            Movie movie = movieDAO.get(id);
            movie.setImage(FileManager.getImageAsBytes(movie.getImagePath()));
            getMovieByIdRequest.setMovie(movie);
            out.writeObject(getMovieByIdRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(CreateTicketRequest createTicketRequest) throws IOException {
        Ticket ticket = createTicketRequest.getTicket();
        ticketDAO.create(ticket);
        createTicketRequest.setStatus(true);
        out.writeObject(createTicketRequest);
    }

    @Override
    public void visit(DeleteSessionRequest deleteSessionRequest) throws IOException, SQLException {
        int sessionId = deleteSessionRequest.getSessionId();
        sessionDAO.delete(sessionId);
        deleteSessionRequest.setSuccess(true);
        out.writeObject(deleteSessionRequest);
    }
}