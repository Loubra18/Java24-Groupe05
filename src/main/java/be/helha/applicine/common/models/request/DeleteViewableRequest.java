package be.helha.applicine.common.models.request;

public class DeleteViewableRequest extends ClientEvent {
    private int viewableId;
    private boolean success;

    private String message;

    public DeleteViewableRequest(int viewableId) {
        this.viewableId = viewableId;
    }

    public int getViewableId() {
        return viewableId;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
