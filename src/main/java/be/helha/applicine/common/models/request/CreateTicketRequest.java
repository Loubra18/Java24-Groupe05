package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Ticket;

import java.io.IOException;
import java.sql.SQLException;

public class CreateTicketRequest extends ClientEvent {
    private Ticket ticket;
    private boolean status;
    public CreateTicketRequest(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }
    @Override
    public void dispatchOn(RequestVisitor requestVisitor) throws IOException, SQLException {
        requestVisitor.visit(this);
    }

    public void setStatus(boolean b) {
        this.status = b;
    }
}
