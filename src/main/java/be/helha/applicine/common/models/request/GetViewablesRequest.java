package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Viewable;

import java.io.IOException;
import java.util.List;

public class GetViewablesRequest extends ClientEvent {
    private List<Viewable> viewables;

    public GetViewablesRequest() {
    }

    public GetViewablesRequest(List<Viewable> viewables) {
        this.viewables = viewables;
    }

    public List<Viewable> getViewables() {
        return viewables;
    }

    public void setViewables(List<Viewable> viewables) {
        this.viewables = viewables;
    }

    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException {
        requestVisitor.visit(this);
    }
}
