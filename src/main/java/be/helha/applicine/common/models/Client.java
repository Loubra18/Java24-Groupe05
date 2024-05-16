package be.helha.applicine.common.models;

import java.io.Serializable; /**
 * This class represents a client.
 */
public class Client implements Serializable {
    private Integer id;
    private String name;
    private String email;
    private String username;
    private String password;

    /**
     * Constructor for the client.
     * @param name The name of the client.
     * @param email The email of the client.
     */
    public Client(int id, String name, String email, String username, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    public Client(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    /**
     * Getter for the name of the client.
     * @return The name of the client.
     */
    public String getName() {
        return name;
    }
    /**
     * Getter for the email of the client.
     * @return The email of the client.
     */
    public String getEmail() {
        return email;
    }
    /**
     * Getter for the phone number of the client.
     * @return The phone number of the client.
     */
    public String getUsername() {
        return username;
    }
    /**
     * Getter for the address of the client.
     * @return The address of the client.
     */
    public String getPassword() {
        return password;
    }


    public int getId() {
        return id;
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }
}