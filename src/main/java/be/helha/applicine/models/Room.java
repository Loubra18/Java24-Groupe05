package be.helha.applicine.models;

/**
 * This class represents a room.
 */
public class Room {
    private int number;
    private int capacity;

    /**
     * Constructor for the room.
     * @param number The number of the room.
     * @param capacity The capacity of the room.
     */
    public Room(int number, int capacity) {
        this.number = number;
        this.capacity = capacity;
    }

    public int getNumber() {
        return number;
    }

    public Integer getCapacity() {
        return capacity;
    }

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
