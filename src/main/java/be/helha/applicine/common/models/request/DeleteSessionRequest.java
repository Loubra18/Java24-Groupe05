package be.helha.applicine.common.models.request;

import java.io.IOException;
import java.sql.SQLException;

public class DeleteSessionRequest extends ClientEvent {
    private int sessionId;
    private boolean status;

    public DeleteSessionRequest(int sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        requestVisitor.visit(this);
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSuccess(boolean b) {
        this.status = b;
    }

    public boolean getStatus() {
        return status;
    }
}
