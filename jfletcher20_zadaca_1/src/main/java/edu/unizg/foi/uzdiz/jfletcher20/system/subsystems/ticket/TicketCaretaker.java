package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketCaretaker {

    private List<TicketMemento> mementos = new ArrayList<>();

    public void addMemento(TicketMemento memento) {
        mementos.add(memento);
    }

    public TicketMemento getMemento(int index) {
        return mementos.get(index);
    }
    
}
