package edu.unizg.foi.uzdiz.jfletcher20.handlers.complaints;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;

public class Level1ComplaintHandler extends AbstractComplaintHandler {
    private final List<String> complaints = Arrays.asList(
            "Nisam siguran/a trebam li izaći na ovoj stanici...",
            "Možda bih trebao/la pričekati još jednu stanicu...",
            "Ova stanica izgleda poznato, ali nisam siguran/a.");

    @Override
    public void handleComplaint(int severity, User user, String trainID, String stationName) {
        if (!shouldPassToNext(severity)) {
            String complaint = complaints.get(new Random().nextInt(complaints.size()));
            broadcastComplaint(user, complaint, trainID);
        } else
            super.handleComplaint(severity, user, trainID, stationName);
    }

    @Override
    protected boolean shouldPassToNext(int severity) {
        return severity > 3;
    }
}