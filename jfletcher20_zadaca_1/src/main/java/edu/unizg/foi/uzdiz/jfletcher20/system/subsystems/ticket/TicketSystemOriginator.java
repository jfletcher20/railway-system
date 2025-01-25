package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

import edu.unizg.foi.uzdiz.jfletcher20.models.tickets.Ticket;

public class TicketSystemOriginator {
    private Ticket state;

    public void setState(Ticket state) {
        this.state = state;
    }

    public Ticket getState() {
        return state;
    }

    public TicketMemento saveState() {
        return new TicketMemento(state);
    }

    public void getStateFromMemento(TicketMemento memento) {
        state = memento.getState();
    }
    
}
