package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history;

import java.util.Stack;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICommand;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class UndoAllCommand implements ICommand {
    private final Stack<ICommand> history;

    public UndoAllCommand(Stack<ICommand> history) {
        this.history = history;
    }

    @Override
    public void execute() {
        if (history.isEmpty()) {
            Logs.e("Nema naredbi za poništiti.");
        }
        while (!history.isEmpty()) {
            ICommand cmd = history.pop();
            cmd.undo();
        }
    }

    @Override
    public void undo() {
        // no undo for this command
    }
}