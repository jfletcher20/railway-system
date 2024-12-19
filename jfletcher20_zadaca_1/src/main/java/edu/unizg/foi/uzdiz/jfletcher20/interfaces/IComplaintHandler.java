package edu.unizg.foi.uzdiz.jfletcher20.interfaces;

import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;

public interface IComplaintHandler {
    void setNext(IComplaintHandler next);
    void handleComplaint(int severity, User user, String trainID, String stationName);
}