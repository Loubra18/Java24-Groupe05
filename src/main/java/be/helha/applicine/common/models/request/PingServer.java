package be.helha.applicine.common.models.request;

public class PingServer extends ClientEvent{
    @Override
    public void dispatchOn(RequestVisitor requestVisitor) {
        requestVisitor.visit(this);
    }
}