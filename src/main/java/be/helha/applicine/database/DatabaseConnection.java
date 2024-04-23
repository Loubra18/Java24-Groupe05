package be.helha.applicine.database;

import java.sql.*;

/**
 * This class represents the connection to the database.
 */
public class DatabaseConnection {

    private static Connection connection;
    private static final String AppData = System.getenv("APPDATA");
    private static final String DbURL = "jdbc:sqlite:" + AppData + "/Applicine/CinemaTor.db";


    //private  static final String DbURL = "jdbc:sqlite:src/main/resources/com/example/applicine/views/database/CinemaTor.db";

    /**
     * This method get the connection to the database.
     *
     * @return The connection to the database.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DbURL);
            System.out.println("Connexion à la base de données établie");

            initializeDatabase();
        }
        return connection;
    }

    /**
     * This method closes the connection to the database.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connexion à la base de données fermée");
        }
    }

    /**
     * This method initializes the database.
     */
    public static void initializeDatabase() throws SQLException{
        Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS movies (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, genre TEXT, director TEXT, duration INTEGER, synopsis TEXT, imagePath TEXT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS movies (id INTEGER PRIMARY KEY, title TEXT, genre TEXT, director TEXT, duration INTEGER, synopsis TEXT, imagePath TEXT)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS rooms ( id integer PRIMARY KEY, seatsnumber integer not null)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clients ( id integer primary key, name text not null, email text not null, username text not null,hashedpassword text not null, CONSTRAINT uniqueusername UNIQUE (username), CONSTRAINT uniqueemail UNIQUE (email))");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS seances(id INTEGER PRIMARY KEY, movieid INTEGER NOT NULL, roomid INTEGER NOT NULL, version text NOT NULL, time DATETIME NOT NULL, CONSTRAINT movieidfk FOREIGN KEY (movieid) REFERENCES movies(id) CONSTRAINT roomidfk FOREIGN KEY (roomid) REFERENCES rooms(id))");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tickets ( id integer PRIMARY KEY, clientid integer NOT NULL, seatcode VARCHAR(4) NOT NULL, price double NOT NULL, clienttype text NOT NULL, verificationcode text NOT NULL, seanceid integer NOT NULL, CONSTRAINT clientidfk FOREIGN KEY (clientid) REFERENCES clients(id), CONSTRAINT seanceidfk FOREIGN KEY (seanceid) REFERENCES seances(id) )");

    }
}