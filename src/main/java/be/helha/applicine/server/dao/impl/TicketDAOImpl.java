package be.helha.applicine.server.dao.impl;

import be.helha.applicine.common.models.Client;
import be.helha.applicine.server.dao.ClientsDAO;
import be.helha.applicine.server.dao.TicketDAO;
import be.helha.applicine.server.database.DatabaseConnection;
import be.helha.applicine.common.models.MovieSession;
import be.helha.applicine.common.models.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAOImpl implements TicketDAO {
    private Connection connection;
    private SessionDAOImpl sessionDAO;

    public TicketDAOImpl() {
        this.connection = DatabaseConnection.getConnection();
        this.sessionDAO = new SessionDAOImpl();
    }

    /**
     * This method adds a ticket to the database.
     * @param ticket The ticket to add.
     */

    @Override
    public boolean create(Ticket ticket) {
        int clientId = ticket.getClientLinked().getId();
        int sessionId = ticket.getMovieSessionLinked().getId();
        String ticketType = ticket.getType();
        String seatCode = ticket.getSeat();
        double price = ticket.getPrice();
        String verificationCode = ticket.getTicketVerificationCode();
        try {
            String query = "INSERT INTO tickets (clientid, seanceid, seatcode, price, clienttype, verificationcode) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, clientId);
            preparedStatement.setInt(2, sessionId);
            preparedStatement.setString(3, seatCode);
            preparedStatement.setDouble(4, price);
            preparedStatement.setString(5, ticketType);
            preparedStatement.setString(6, verificationCode);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Inserted ticket ID: " + id);
                    return true;
                } else {
                    throw new SQLException("Creating ticket failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Ticket> getTicketsByClient(int clientId) {
        List<Ticket> tickets = new ArrayList<>();
        ClientsDAOImpl clientsDAO = new ClientsDAOImpl();
        Client client;
        try {
            client = clientsDAO.get(clientId);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Client not found");
        }
        String query = "SELECT * FROM tickets WHERE clientid = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, clientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                MovieSession movieSession = sessionDAO.get(resultSet.getInt("seanceid"));
                String seatCode = resultSet.getString("seatcode");
                double price = resultSet.getDouble("price");
                String ticketType = resultSet.getString("clienttype");
                String verificationCode = resultSet.getString("verificationcode");
                Ticket ticket = new Ticket(id, client, movieSession, ticketType, seatCode, price, verificationCode);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.out.println("erreur laissée, changera avec serveur");
        }
        return tickets;
    }

    @Override
    public void delete(Integer ticketId) {
        String query = "DELETE FROM tickets WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, ticketId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("erreur laissée, changera avec serveur");
        }
    }
}
