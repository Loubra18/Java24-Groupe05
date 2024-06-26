package database;

import be.helha.applicine.common.models.exceptions.DaoException;
import be.helha.applicine.server.dao.MovieDAO;
import be.helha.applicine.server.dao.impl.MovieDAOImpl;
import be.helha.applicine.common.models.Movie;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MovieDAOTest extends DatabaseConnectionTest {
    private static MovieDAO movieDAO;

    @BeforeAll
    public static void setup() {
        Connection connection = DatabaseConnectionTest.getConnection();
        movieDAO = new MovieDAOImpl(connection);
    }

    @AfterAll
    public static void tearDown() {
        movieDAO.deleteAll();
    }

    @Test
    public void testGetAllMovies() throws SQLException {
        try {
            List<Movie> movies = movieDAO.getAll();
            for(Movie movie : movies) {
                System.out.println(movie);
            }
            assertNotNull(movies);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testCreateMovie() throws SQLException {
        try {
            byte[] image = new byte[10];
            Movie newMovie = new Movie("Title", "Genre", "Director", 120, "Synopsis", image, "imagePath");
            int initialSize = movieDAO.getAll().size();
            movieDAO.create(newMovie);
            int finalSize = movieDAO.getAll().size();
            assertEquals(initialSize + 1, finalSize);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateMovie() throws IOException {
        try {
            byte[] image = Files.readAllBytes(Paths.get("src/test/java/database/téléchargement.jpg"));
            Movie movie = new Movie("TestBebou", "Genre", "Director", 120, "Synopsis", image, "file:src/test/java/database/téléchargement.jpg");
            movieDAO.create(movie);
            Movie movie2 = movieDAO.get(1);
            movie2.setTitle("New Title");
            movieDAO.update(movie2);
            Movie updatedMovie = movieDAO.get(1);
            assertEquals("New Title", updatedMovie.getTitle());
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteMovie() throws Exception {
        try {
            Movie movie = new Movie("TestBebou", "Genre", "Director", 120, "Synopsis", null, "imagePath");
            movieDAO.create(movie);
            int initialSize = movieDAO.getAll().size();
            movieDAO.delete(1);
            int finalSize = movieDAO.getAll().size();
            assertEquals(initialSize - 1, finalSize);
        } catch (DaoException e) {
            e.printStackTrace();
        }
    }
}