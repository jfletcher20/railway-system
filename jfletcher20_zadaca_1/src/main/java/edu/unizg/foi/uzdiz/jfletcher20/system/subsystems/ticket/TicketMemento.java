package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

import edu.unizg.foi.uzdiz.jfletcher20.models.tickets.Ticket;

public class TicketMemento {
    private Ticket state;

    public TicketMemento(Ticket state2) {
        this.state = state2;
    }

    public Ticket getState() {
        return state;
    }

    @Override
    public String toString() {
        return "TicketSystemMemento{" +
                "state=" + state +
                '}';
    }
    
}
