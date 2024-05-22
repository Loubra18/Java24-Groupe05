package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.MovieSession;

public class AddSessionRequest extends ClientEvent {
    private MovieSession session;
    private boolean success;

    public AddSessionRequest(MovieSession session) {
        this.session = session;
    }

    public MovieSession getSession() {
        return session;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) {
        requestVisitor.visit(this);
    }
}
