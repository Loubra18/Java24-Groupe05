package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Movie;

import java.io.IOException;
import java.sql.SQLException;

public class GetMovieByIdRequest extends ClientEvent{
    private int id;
    private Movie movie;
    public GetMovieByIdRequest(int id) {
        this.id = id;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        //je vais passer un objet de type Movie à la méthode handleGetMovieById
        requestVisitor.visit(this);
    }

    public int getMovieId() {
        return id;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
