package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

public class TicketSystemOriginator {
    private TicketSystem state;

    public void setState(TicketSystem state) {
        this.state = state;
    }

    public TicketSystem getState() {
        return state;
    }

    public TicketSystemMemento saveState() {
        return new TicketSystemMemento(state);
    }

    public void getStateFromMemento(TicketSystemMemento memento) {
        state = memento.getState();
    }
    
}
