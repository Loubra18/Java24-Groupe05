package be.helha.applicine.dao.impl;

import be.helha.applicine.server.dao.RoomDAO;
import be.helha.applicine.common.models.Room;
import be.helha.applicine.server.dao.impl.RoomDAOImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomDAOImplTest {

    private RoomDAO roomDAO;

    @BeforeEach
    void setUp() {
        roomDAO = new RoomDAOImpl();
    }

    @AfterEach
    void tearDown() {
        roomDAO = null;
    }

    @Test
    void getAllRooms() throws SQLException {
        List<Room> rooms = roomDAO.getAll();
        assertNotNull(rooms, "Rooms list should not be null");
    }



    @Test
    void addRoom() throws Exception {
        roomDAO.delete(50);
        Room room = new Room(50, 100);
        roomDAO.create(room);
        Room addedRoom = roomDAO.get(50);
        assertEquals(room, addedRoom, "Added room should be equal to the retrieved room");
    }
//    @Test
//    void getRoomById() throws SQLException {
//        Room room = roomDAO.getRoomById(50);
//        assertNotNull(room, "Room should not be null");
//    }

    @Test
    void updateRoom() throws SQLException {
        Room room = new Room(50, 50);
        roomDAO.update(room);
        Room updatedRoom = roomDAO.get(50);
        assertEquals(room, updatedRoom, "Updated room should be equal to the retrieved room");
    }

    @Test
    void removeRoom() throws Exception {
        Room room = new Room(50, 100);
        roomDAO.create(room);
        roomDAO.delete(50);
        Room removedRoom = roomDAO.get(50);
        assertNull(removedRoom, "Removed room should be null");
    }
}