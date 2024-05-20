package be.helha.applicine.server.dao.impl;

import be.helha.applicine.server.FileManager;
import be.helha.applicine.server.dao.ViewableDAO;
import be.helha.applicine.server.database.DatabaseConnection;
import be.helha.applicine.common.models.Movie;
import be.helha.applicine.common.models.Saga;
import be.helha.applicine.common.models.Viewable;

import java.io.IOException;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ViewableDAOImpl implements ViewableDAO {
    private Connection connection;

    public ViewableDAOImpl() {
        this.connection = DatabaseConnection.getConnection();
    }


    public void addViewableWithOneMovie(String title, String singleMovie, int id) {
        try {
            String query = "INSERT INTO viewables (name, type) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, singleMovie);
            preparedStatement.executeUpdate();

            addMovieToViewable(preparedStatement.getGeneratedKeys().getInt(1), id);

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idViewable = generatedKeys.getInt(1);
                    System.out.println("Inserted viewable ID: " + idViewable);
                } else {
                    throw new SQLException("Creating viewable failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addViewable(String name, String type, ArrayList<Integer> movieIDs) {
        try {
            String query = "INSERT INTO viewables (name, type) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, type);
            preparedStatement.executeUpdate();

            for (Integer movieID : movieIDs) {
                addMovieToViewable(preparedStatement.getGeneratedKeys().getInt(1), movieID);
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    System.out.println("Inserted viewable ID: " + id);
                } else {
                    throw new SQLException("Creating viewable failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeViewable(Integer id) {
        if (!getSeancesLinkedToViewable(id).isEmpty()) {
            return false;
        }
        try {
            connection.createStatement().executeUpdate("DELETE FROM viewables WHERE id = " + id);
            removeViewableMovieCorrespondance(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public void removeViewableFromMovie(int movieId) {
        try {
            int viewableId = 0;
            ResultSet rs = connection.createStatement().executeQuery("SELECT viewableid FROM viewablecontains WHERE movieid = " + movieId);
            if (rs.next()) {
                viewableId = rs.getInt("viewableid");
            }
            removeViewable(viewableId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getViewableIdByMovieId(int id) {
        try{
            ResultSet rs = connection.createStatement().executeQuery("SELECT viewableid FROM viewablecontains WHERE movieid = " + id);
            if(rs.next()){
                return rs.getInt("viewableid");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }


    public void updateViewable(Integer id, String name, String type, ArrayList<Integer> movieIDs) {
        try {
            connection.createStatement().executeUpdate("UPDATE viewables SET name = '" + name + "', type = '" + type + "' WHERE id = " + id);
            updateViewableMovieCorrespondance(id, movieIDs);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void addMovieToViewable(Integer viewableID, Integer movieID) {
        try {
            connection.createStatement().executeUpdate("INSERT INTO viewablecontains (viewableid, movieid) VALUES (" + viewableID + ", " + movieID + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateViewableMovieCorrespondance(Integer viewableID, ArrayList<Integer> movieIDs) {
        removeViewableMovieCorrespondance(viewableID);
        for (Integer movieID : movieIDs) {
            addMovieToViewable(viewableID, movieID);
        }
    }

    public void removeViewableMovieCorrespondance(Integer viewableID) {
        try {
            connection.createStatement().executeUpdate("DELETE FROM viewablecontains WHERE viewableid = " + viewableID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Movie> getMoviesFromViewable(Integer viewableID) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM viewablecontains WHERE viewableid = " + viewableID);
            while (rs.next()) {
                Movie movie = new MovieDAOImpl().get(rs.getInt("movieid"));
                movies.add(movie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movies;
    }

    public ArrayList<Viewable> getAllViewables() {
        ArrayList<Viewable> array = new ArrayList<Viewable>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM viewables");
            while (rs.next()) {
                Viewable viewable = null;
                ArrayList<Movie> moviesForViewable = getMoviesFromViewable(rs.getInt("id"));
                if (moviesForViewable.size() == 1) {
                    viewable = new Movie(rs.getInt("id"), rs.getString("name"), moviesForViewable.getFirst().getGenre(), moviesForViewable.getFirst().getDirector(), moviesForViewable.getFirst().getDuration(), moviesForViewable.getFirst().getSynopsis(), moviesForViewable.getFirst().getImage(), moviesForViewable.getFirst().getImagePath());
                } else {
                    Movie refMovie = moviesForViewable.getFirst();
                    viewable = new Saga(rs.getInt("id"), rs.getString("name"), refMovie.getGenre(), refMovie.getDirector(), getTotalDurationFromMovies(moviesForViewable), "moviesForViewable", refMovie.getImage(), refMovie.getImagePath(), moviesForViewable);
                }
                if (viewable.getImage() == null) {
                    try {
                        viewable.setImage(FileManager.getImageAsBytes(viewable.getImagePath()));
                    } catch (IOException e) {
                        throw new RuntimeException("Error while getting image as bytes");
                    }
                }
                array.add(viewable);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return array;
    }

    public int getTotalDurationFromMovies(ArrayList<Movie> movies) {
        int totalDuration = 0;
        for (Movie movie : movies) {
            totalDuration += movie.getDuration();
        }
        return totalDuration;
    }

    public Viewable getViewableById(int id) {
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM viewables WHERE id = " + id);
            if (rs.next()) {
                Viewable viewable = null;
                ArrayList<Movie> moviesForViewable = getMoviesFromViewable(rs.getInt("id"));
                if (moviesForViewable.size() == 1) {
                    viewable = new Movie(rs.getInt("id"), rs.getString("name"), moviesForViewable.getFirst().getGenre(), moviesForViewable.getFirst().getDirector(), moviesForViewable.getFirst().getDuration(), moviesForViewable.getFirst().getSynopsis(), moviesForViewable.getFirst().getImage(), moviesForViewable.getFirst().getImagePath());
                } else {
                    Movie refMovie = moviesForViewable.getFirst();
                    if (rs.getString("type").equals(("Saga"))) {
                        viewable = new Saga(rs.getInt("id"), rs.getString("name"), refMovie.getGenre(), refMovie.getDirector(), getTotalDurationFromMovies(moviesForViewable), "moviesForViewable", refMovie.getImage(), refMovie.getImagePath(), moviesForViewable);
                    }
                }
                return viewable;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public ArrayList<Integer> getSeancesLinkedToViewable(int id) {
        ArrayList<Integer> seances = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM seances WHERE viewableid = " + id);
            while (rs.next()) {
                seances.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return seances;
    }

    @Override
    public int sagasLinkedToMovie(int movieId) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT count(*) FROM viewables v JOIN viewablecontains vc ON v.id = vc.viewableid WHERE v.type = 'Saga' AND vc.movieid = ?")) {
            pstmt.setInt(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du nombre de sagas liées au film : " + e.getMessage());
        }
        return 0;
    }



}
