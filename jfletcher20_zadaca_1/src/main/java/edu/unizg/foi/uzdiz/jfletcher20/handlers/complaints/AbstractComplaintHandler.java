package edu.unizg.foi.uzdiz.jfletcher20.handlers.complaints;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComplaintHandler;
import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.command.CommandSystemSingleton;

public abstract class AbstractComplaintHandler implements IComplaintHandler {
    protected IComplaintHandler next;

    @Override
    public void setNext(IComplaintHandler next) {
        this.next = next;
    }

    @Override
    public void handleComplaint(int severity, User user, String trainID, String stationName) {
        if (next != null && shouldPassToNext(severity)) {
            next.handleComplaint(severity, user, trainID, stationName);
        }
    }

    protected boolean shouldPassToNext(int severity) {
        return false;
    }

    protected void broadcastComplaint(User user, String complaint, String groupID) {
        CommandSystemSingleton.getInstance()
                .getUserChat()
                .broadcast(groupID, user, complaint, true);
    }
}