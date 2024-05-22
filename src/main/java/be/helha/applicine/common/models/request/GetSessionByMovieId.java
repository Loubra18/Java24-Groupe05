package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.MovieSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GetSessionByMovieId extends ClientEvent{
    private int movieID;
    private List<MovieSession> sessions;
    public GetSessionByMovieId(int movieID) {
        this.movieID = movieID;
    }
    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        requestVisitor.visit(this);
    }

    public int getMovieId() {
        return movieID;
    }

    public void setSessions(List<MovieSession> sessions) {
        this.sessions = sessions;
    }
}
