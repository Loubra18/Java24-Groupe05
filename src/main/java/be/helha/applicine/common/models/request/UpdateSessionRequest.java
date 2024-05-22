package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.MovieSession;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateSessionRequest extends ClientEvent{
    private MovieSession session;
    private boolean success;

    public MovieSession getSession() {
        return session;
    }

    public UpdateSessionRequest(MovieSession session) {
        this.session = session;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        requestVisitor.visit(this);
    }
}
