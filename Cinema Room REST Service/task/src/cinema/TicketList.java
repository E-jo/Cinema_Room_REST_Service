package cinema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketList {
    private List<Ticket> tickets;

    public TicketList() {
        this.tickets = new ArrayList<>();
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void addTicket(Ticket t) {
        tickets.add(t);
    }

    public void removeTicket(UUID token) {
        tickets.removeIf(t -> t.getToken().equals(token));
    }
}

