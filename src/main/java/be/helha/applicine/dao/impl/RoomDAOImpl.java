package be.helha.applicine.dao.impl;


import be.helha.applicine.dao.RoomDAO;
import be.helha.applicine.database.DatabaseConnection;
import be.helha.applicine.models.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAOImpl implements RoomDAO {


    private Connection connection;

    private static final String SELECT_ALL_ROOMS = "SELECT * FROM rooms";
    private static final String SELECT_ROOM_BY_ID = "SELECT * FROM rooms WHERE id = ?";

    private static final String ADD_ROOM = "INSERT INTO rooms (seatsnumber) VALUES (?)";

    private static final String UPDATE_ROOM = "UPDATE rooms SET seatsnumber = ? WHERE id = ?";

    private static final String DELETE_ROOM = "DELETE FROM rooms WHERE id = ?";

    public RoomDAOImpl() {
        this.connection = DatabaseConnection.getConnection();
    }


    /**
     * return a list with all rooms
     * @return
     * @throws SQLException
     */

    @Override
    public List<Room> getAllRooms() throws SQLException {

        List<Room> rooms = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_ROOMS);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(rs.getInt("id"), rs.getInt("seatsnumber")));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la liste des salles : " + e.getMessage());
            if (e.getMessage().contains("missing database")) {
                System.out.println("La base de données n'existe pas");
            }
        }
        return rooms;
    }

    /**
     * return a room by id
     * @param id
     * @return
     * @throws SQLException
     */
    @Override
    public Room getRoomById(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_ROOM_BY_ID)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Room(rs.getInt("id"), rs.getInt("seatsnumber"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la salle : " + e.getMessage());
            if (e.getMessage().contains("missing database")) {
                System.out.println("La base de données n'existe pas");
            }
        }
        return null;
    }

    /**
     * add a room
     * @param roomCapacity
     */
    @Override
    public void addRoom(Integer roomCapacity) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(ADD_ROOM);
            pstmt.setInt(1, roomCapacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la salle : " + e.getMessage());
            if (e.getMessage().contains("missing database")) {
                System.out.println("La base de données n'existe pas");
            }
        }
    }

    /**
     * update a room
     * @param roomID
     * @param roomCapacity
     */
    @Override
    public void updateRoom(Integer roomID, Integer roomCapacity) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(UPDATE_ROOM);
            pstmt.setInt(1, roomID);
            pstmt.setInt(2, roomCapacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la salle : " + e.getMessage());
            if (e.getMessage().contains("missing database")) {
                System.out.println("La base de données n'existe pas");
            }
        }
    }

    /**
     * remove a room
     * @param id
     * @throws Exception
     */
    @Override
    public void removeRoom(int id) throws Exception {
        try {
            PreparedStatement pstmt = connection.prepareStatement(DELETE_ROOM);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la salle : " + e.getMessage());
            if (e.getMessage().contains("missing database")) {
                System.out.println("La base de données n'existe pas");
            }
        }

    }

    public boolean isRoomTableEmpty() {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM rooms");
            ResultSet rs = pstmt.executeQuery();
            return !rs.next();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de la table des salles : " + e.getMessage());
            if (e.getMessage().contains("missing database")) {
                System.out.println("La base de données n'existe pas");
            }
        }
        return true;
    }

    public void fillRoomTable() {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO rooms (seatsnumber) VALUES\n" +
                    "(100),\n" +
                    "(80),\n" +
                    "(120),\n" +
                    "(150),\n" +
                    "(70),\n" +
                    "(90),\n" +
                    "(110),\n" +
                    "(85),\n" +
                    "(130),\n" +
                    "(60);");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la salle : " + e.getMessage());
            if (e.getMessage().contains("missing database")) {
                System.out.println("La base de données n'existe pas");
            }
        }
    }

}
