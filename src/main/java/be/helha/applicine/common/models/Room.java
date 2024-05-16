package be.helha.applicine.common.models;

import java.io.Serializable;

/**
 * This class represents a room.
 */
public class Room implements Serializable {
    /**
     * The number of the room.
     */
    private Integer number;
    /**
     * The capacity of the room.
     */
    private Integer capacity;

    /**
     * Constructor for the room.
     *
     * @param number   The number of the room.
     * @param capacity The capacity of the room.
     */
    public Room(int number, int capacity) {
        this.number = number;
        this.capacity = capacity;
    }

    /**
     * Get the number of the room.
     *
     * @return The number of the room.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the capacity of the room.
     *
     * @return The capacity of the room.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Check if the room is equal to another object.
     *
     * @param obj The object to compare to.
     * @return True if the room is equal to the object, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Room)) {
            return false;
        }
        Room room = (Room) obj;
        return room.getNumber() == number && room.getCapacity() == capacity;
    }
}
