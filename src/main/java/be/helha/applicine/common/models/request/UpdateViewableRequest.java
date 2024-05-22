package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Saga;

import java.io.IOException;

public class UpdateViewableRequest extends ClientEvent {
    private Saga saga;
    private boolean success;

    public UpdateViewableRequest(Saga saga) {
        this.saga = saga;
    }

    public Saga getSaga() {
        return saga;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException {
        requestVisitor.visit(this);
    }

    public void setSuccess(boolean b) {
        this.success = b;
    }

    public boolean getSuccess() {
        return success;
    }
}
