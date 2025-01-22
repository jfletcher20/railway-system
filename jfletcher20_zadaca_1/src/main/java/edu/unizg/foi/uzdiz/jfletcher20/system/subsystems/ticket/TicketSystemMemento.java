package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

public class TicketSystemMemento {
    private TicketSystem state;

    public TicketSystemMemento(TicketSystem state) {
        this.state = state;
    }

    public TicketSystem getState() {
        return state;
    }

    @Override
    public String toString() {
        return "TicketSystemMemento{" +
                "state=" + state +
                '}';
    }
    
}
