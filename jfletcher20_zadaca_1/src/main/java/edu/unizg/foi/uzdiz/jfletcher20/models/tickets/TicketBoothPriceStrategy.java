package edu.unizg.foi.uzdiz.jfletcher20.models.tickets;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITicketPriceStrategy;

public class TicketBoothPriceStrategy implements ITicketPriceStrategy {

    @Override
    public double calculateTicketPrice(Ticket ticket) {
        return this.calculateOriginalTicketPrice(ticket) * (ticket.isOnWeekend() ?
                ticket.ticketCostParameters().discount(ticket.ticketCostParameters().getWeekendTicketDiscount())
                : 1);
    }

    @Override
    public double calculateOriginalTicketPrice(Ticket ticket) {
        double distance = ticket.distance();
        double pricePerKm = ticket.pricePerKm(ticket.getTrain().getTrainType());
        return distance * pricePerKm;
    }

}
