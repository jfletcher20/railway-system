package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.link_history;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICommand;
import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;

public class UnlinkCommand implements ICommand {
    private final LinkReceiver receiver;
    private final String groupId;
    private final User user;
    private LinkCommand undoCommand = null;

    public UnlinkCommand(LinkReceiver receiver, String groupId, User user) {
        this.receiver = receiver;
        this.groupId = groupId;
        this.user = user;
    }

    @Override
    public void execute() {
        boolean success = receiver.unlink(groupId, user);
        if (success)
            undoCommand = new LinkCommand(receiver, groupId, user);
    }

    @Override
    public void undo() {
        if (undoCommand != null) {
            undoCommand.execute();
        }
    }

    public boolean getSuccess() {
        return undoCommand != null;
    }

    @Override
    public String toString() {
        return user + " više nije u grupi " + groupId;
    }
}