package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketSystemCaretaker {

    private List<TicketSystemMemento> mementos = new ArrayList<>();

    public void addMemento(TicketSystemMemento memento) {
        mementos.add(memento);
    }

    public TicketSystemMemento getMemento(int index) {
        return mementos.get(index);
    }

    public TicketSystemMemento getLastMemento() {
        return mementos.get(mementos.size() - 1);
    }
    
}
