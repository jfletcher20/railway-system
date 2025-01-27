package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history;

import java.util.Stack;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICommand;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class LinkHistoryCommand implements ICommand {
    private final Stack<ICommand> history;

    public LinkHistoryCommand(Stack<ICommand> history) {
        this.history = history;
    }

    @Override
    public void execute() {
        if (history.isEmpty()) {
            Logs.e("Povijest je prazna.");
        } else {
            Logs.o("Povijest naredbi:");
            for (ICommand cmd : history) {
                Logs.o(" - " + cmd.getClass().getSimpleName() + ":\t" + cmd.toString(), false);
            }
        }
    }

    @Override
    public void undo() {
        // no undo for listing history
    }
}
