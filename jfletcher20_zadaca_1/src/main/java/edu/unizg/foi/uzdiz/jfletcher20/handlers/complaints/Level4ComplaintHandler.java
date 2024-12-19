package edu.unizg.foi.uzdiz.jfletcher20.handlers.complaints;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;

public class Level4ComplaintHandler extends AbstractComplaintHandler {
    private final List<String> getComplaints(String stationName, String trainID) {
        return Arrays.asList(
                "Vidio/la sam NLO kod stanice " + stationName + "!",
                "Čini mi se da gori dio vlaka " + trainID + "!!",
                "Ova simulacija se pretvara u horor film.");
    }

    @Override
    public void handleComplaint(int severity, User user, String trainID, String stationName) {
        String complaint = getComplaints(stationName, trainID)
                .get(new Random().nextInt(getComplaints(stationName, trainID).size()));
        broadcastComplaint(user, complaint, trainID);
    }
}