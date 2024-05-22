package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.MovieSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GetAllSessionRequest extends ClientEvent{
    private List<MovieSession> sessions;
    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        requestVisitor.visit(this);
    }

    public void setSessions(List<MovieSession> sessions) {
        this.sessions = sessions;
    }

    public List<MovieSession> getSessions() {
        return sessions;
    }
}
