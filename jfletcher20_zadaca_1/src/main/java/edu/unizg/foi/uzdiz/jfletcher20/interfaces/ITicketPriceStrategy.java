package edu.unizg.foi.uzdiz.jfletcher20.interfaces;

import edu.unizg.foi.uzdiz.jfletcher20.models.tickets.Ticket;

public interface ITicketPriceStrategy {
    public double calculateTicketPrice(Ticket ticket);
    public double calculateOriginalTicketPrice(Ticket ticket);
}
