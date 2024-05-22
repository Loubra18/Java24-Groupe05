package be.helha.applicine.common.models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class represents a film session.
 */
public class MovieSession implements Serializable {
    /**
     * The id of the session.
     */
    public int id;
    /**
     * The viewable of the session.
     */
    private Viewable viewable;
    /**
     * The date and the time of the session.
     */
    private String time;
    /**
     * The room of the session.
     */
    private Room room;
    /**
     * The room id of the session.
     */
    private int roomid;
    /**
     * The version of the session.
     */
    private final String version;

    /**
     * Constructor of the class.
     *
     * @param id       The id of the session.
     * @param viewable The viewable of the session.
     * @param time     The date and the time of the session.
     * @param room     The room of the session.
     * @param version  The version of the session.
     */
    public MovieSession(int id, Viewable viewable, String time, Room room, String version) {
        this.id = id;
        this.viewable = viewable;
        this.time = time;
        this.room = room;
        this.version = version;
    }

    public MovieSession(Integer sessionId, Viewable viewable, String convertedDateTime, int roomById, String version) {
        this.id = sessionId;
        this.viewable = viewable;
        this.time = convertedDateTime;
        this.roomid = roomById;
        this.version = version;
    }

    /**
     * Get the viewable of the session.
     *
     * @return The viewable of the session.
     */
    public Viewable getViewable() {
        return viewable;
    }

    /**
     * Get the id of the session.
     *
     * @return The id of the session.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the date and the time of the session.
     *
     * @return The date and the time of the session.
     */
    public String getTime() {
        return time;
    }

    /**
     * Get the room of the session.
     *
     * @return The room of the session.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Set the viewable of the session.
     *
     * @param viewable The viewable of the session.
     */
    public void setViewable(Movie viewable) {
        this.viewable = viewable;
    }

    /**
     * Set the date and the time of the session.
     *
     * @param time The date and the time of the session.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Set the room of the session.
     *
     * @param room The room of the session.
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Get the version of the session.
     *
     * @return The version of the session.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the date of the session.
     *
     * @return The date of the session.
     */
    public LocalDate getDate() {
        String strDate = time.split(" ")[0];
        LocalDate date = LocalDate.parse(strDate);
        return date;
    }

    /**
     * Get the hour of the session.
     *
     * @return The hour of the session.
     */
    public String getHourFromTime() {
        return time.split(" ")[1].split(":")[0];
    }

    /**
     * Get the minute of the session.
     *
     * @return The minute of the session.
     */
    public String getMinuteFromTime() {
        return time.split(" ")[1].split(":")[1];
    }
}
