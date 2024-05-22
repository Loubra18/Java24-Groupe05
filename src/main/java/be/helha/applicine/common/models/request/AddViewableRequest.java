package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Viewable;

public class AddViewableRequest extends ClientEvent {
    private Viewable viewable;
    private boolean success;

    public AddViewableRequest(Viewable viewable) {
        this.viewable = viewable;
    }

    public Viewable getViewable() {
        return viewable;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) {
        requestVisitor.visit(this);
    }

    public void setSuccess(boolean b) {
        this.success = b;
    }

    public boolean getSuccess() {
        return success;
    }
}
