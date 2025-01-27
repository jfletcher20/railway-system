package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history;

import java.util.Stack;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICommand;

public class LinkInvoker {
    private final Stack<ICommand> history = new Stack<>();

    public void executeCommand(ICommand command) {
        if (command instanceof LinkCommand || command instanceof UnlinkCommand) {
            command.execute();
            if (command instanceof LinkCommand) {
                if (((LinkCommand) command).getSuccess())
                    history.push(command);
            } else if (command instanceof UnlinkCommand) {
                if (((UnlinkCommand) command).getSuccess())
                    history.push(command);
            }
        }
    }

    public void showHistory() {
        new LinkHistoryCommand(history).execute();
    }

    public void undoLast() {
        new UndoLastCommand(history).execute();
    }

    public void undoAll() {
        new UndoAllCommand(history).execute();
    }
}
