package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CinemaController {
    Cinema cinema = new Cinema(9, 9);
    List<Seat> seats = cinema.availableSeats;
    List<Ticket> soldTickets = new ArrayList<>();

    @GetMapping("/seats")
    public Cinema getSeats() {
        return cinema;
    }

    @PostMapping("/purchase")
    ResponseEntity<?> buyTicket(@RequestBody Seat seatToSell) {
        if (invalidRange(seatToSell)) {
            return new ResponseEntity<>(new Error("The number of a row or a column is out of bounds!"),
                    HttpStatus.BAD_REQUEST);
        }
        for (Seat seat: seats) {
            if (seat.getRow() == seatToSell.getRow()
                    && seat.getColumn() == seatToSell.getColumn()
                    && !seat.isAvailable()) {
                return new ResponseEntity<>(new Error("The ticket has been already purchased!"),
                        HttpStatus.BAD_REQUEST);
            } else if (seat.getRow() == seatToSell.getRow()
                    && seat.getColumn() == seatToSell.getColumn()
                    && seat.isAvailable()) {
                seat.setAvailable(false);
                Ticket ticket = new Ticket(UUID.randomUUID(), seat);
                soldTickets.add(ticket);
                return new ResponseEntity<>(ticket, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Error("Unknown error"), HttpStatus.BAD_REQUEST);
    }

    private boolean invalidRange(Seat seatToSell) {
        return cinema.getTotalRows() <= seatToSell.getRow()
                || cinema.getTotalColumns() < seatToSell.getColumn()
                || seatToSell.getRow() <= 0
                || seatToSell.getColumn() <= 0;
    }

    @PostMapping("/return")
    ResponseEntity<?> returnedTicket(@RequestBody Ticket ticket) {
        for (Ticket soldTicket: soldTickets) {
            if (ticket.getToken().equals(soldTicket.getToken())) {
                ReturnedTicket returnedTicket = new ReturnedTicket(soldTicket.getSeat());
                for (Seat seat: seats) {
                    if (seat.getRow() == returnedTicket.seat.getRow()
                            && seat.getColumn() == returnedTicket.seat.getColumn()) {
                        seat.setAvailable(true);
                    }
                }
                soldTickets.remove(soldTicket);
                return new ResponseEntity<>(returnedTicket, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Error("Wrong token!"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/stats")
    ResponseEntity<?> getStats(@RequestParam(required = false, defaultValue = "password", value="password") String p) {
        System.out.println(p);
        ResponseEntity<?> response = null;
        int currentIncome = 0;

        if (p.equalsIgnoreCase("super_secret")) {
            for (Ticket t : soldTickets) {
                int seatPrice = t.seat.getPrice();
                currentIncome += seatPrice;
            }
            Map<String, Integer> r = new HashMap<>();
            r.put("current_income", currentIncome);
            r.put("number_of_purchased_tickets", soldTickets.size());
            r.put("number_of_available_seats", getSeats().getAvailableSeats().size() - soldTickets.size());

            response = new ResponseEntity<>(r, HttpStatus.OK);

        } else {
            response = new ResponseEntity<>(new Error("The password is wrong!"), HttpStatus.UNAUTHORIZED);
        }

        return response;
    }
}