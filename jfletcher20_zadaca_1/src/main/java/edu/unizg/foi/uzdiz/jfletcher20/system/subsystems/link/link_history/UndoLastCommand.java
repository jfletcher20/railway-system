package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history;

import java.util.Stack;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICommand;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class UndoLastCommand implements ICommand {
    private final Stack<ICommand> history;

    public UndoLastCommand(Stack<ICommand> history) {
        this.history = history;
    }

    @Override
    public void execute() {
        if (!history.isEmpty()) {
            history.pop().undo();
        } else {
            Logs.e("Nema naredbi za poništiti.");
        }
    }

    @Override
    public void undo() {
        // no undo for this command
    }
}