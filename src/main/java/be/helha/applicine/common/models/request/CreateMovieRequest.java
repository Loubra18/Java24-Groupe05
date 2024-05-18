package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Movie;

import java.io.IOException;
import java.sql.SQLException;

public class CreateMovieRequest extends ClientEvent {
    private Movie movie;

    public CreateMovieRequest(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }
    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        requestVisitor.visit(this);
    }
}
