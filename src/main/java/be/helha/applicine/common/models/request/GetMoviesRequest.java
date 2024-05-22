package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Movie;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetMoviesRequest extends ClientEvent {
    private List<Movie> movieList;
    public GetMoviesRequest() {
        this.movieList = new ArrayList<>();
    }
    public List<Movie> getMovieList() {
        return movieList;
    }
    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }
    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        requestVisitor.visit(this);
    }

    public void setMovies(List<Movie> movies) {
        this.movieList = movies;
    }

    public List<Movie> getMovies() {
        return movieList;
    }
}
