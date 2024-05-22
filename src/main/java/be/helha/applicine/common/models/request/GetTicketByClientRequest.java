package be.helha.applicine.common.models.request;

import be.helha.applicine.common.models.Ticket;

import java.util.List;

public class GetTicketByClientRequest extends ClientEvent {
    private int clientId;
    private List<Ticket> tickets;

    public GetTicketByClientRequest(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public void dispatchOn(RequestVisitor visitor) {
        visitor.visit(this);
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}
