package be.helha.applicine.dao.impl;

import be.helha.applicine.dao.RoomDAO;
import be.helha.applicine.models.Room;
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
        List<Room> rooms = roomDAO.getAllRooms();
        assertNotNull(rooms, "Rooms list should not be null");
    }



    @Test
    void addRoom() throws Exception {
        roomDAO.removeRoom(50);
        Room room = new Room(50, 100);
        roomDAO.addRoom(room.getCapacity());
        Room addedRoom = roomDAO.getRoomById(50);
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
        roomDAO.updateRoom(room.getNumber(), room.getCapacity());
        Room updatedRoom = roomDAO.getRoomById(50);
        assertEquals(room, updatedRoom, "Updated room should be equal to the retrieved room");
    }

    @Test
    void removeRoom() throws Exception {
        Room room = new Room(50, 100);
        roomDAO.addRoom(room.getCapacity());
        roomDAO.removeRoom(50);
        Room removedRoom = roomDAO.getRoomById(50);
        assertNull(removedRoom, "Removed room should be null");
    }
}