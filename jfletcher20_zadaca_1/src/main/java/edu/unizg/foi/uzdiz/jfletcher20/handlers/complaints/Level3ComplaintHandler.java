package edu.unizg.foi.uzdiz.jfletcher20.handlers.complaints;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;

public class Level3ComplaintHandler extends AbstractComplaintHandler {
    private final List<String> getComplaints(User user) {
        return Arrays.asList(
                "Zašto se zovem " + user.name() + "? Možda bih trebao/la promijeniti ime.",
                "Ovo je već apsurdno... i još moram UZDIZ zadaću odraditi kad dođem doma.",
                "Osjećam se kao da se vlak sprda sa mnom.");
    }

    @Override
    public void handleComplaint(int severity, User user, String trainID, String stationName) {
        if (!shouldPassToNext(severity)) {
            String complaint = getComplaints(user).get(new Random().nextInt(getComplaints(user).size()));
            broadcastComplaint(user, complaint, trainID);
        }
        super.handleComplaint(severity, user, trainID, stationName);
    }

    @Override
    protected boolean shouldPassToNext(int severity) {
        return severity > 8;
    }
}