package be.helha.applicine.common.models.request;

import be.helha.applicine.server.ClientHandler;

import java.io.IOException;
import java.sql.SQLException;

public class GetSessionByMovieId extends ClientEvent{
    private int movieID;
    public GetSessionByMovieId(int movieID) {
        this.movieID = movieID;
    }
    @Override
    public void dispatchOn(ClientHandler clientHandler) throws IOException, SQLException {
        clientHandler.handleGetSessionsByMovie(this.movieID);
    }
}
