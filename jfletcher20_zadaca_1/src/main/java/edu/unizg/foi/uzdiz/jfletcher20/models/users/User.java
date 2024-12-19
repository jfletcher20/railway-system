package edu.unizg.foi.uzdiz.jfletcher20.models.users;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IObserver;
import edu.unizg.foi.uzdiz.jfletcher20.system.ChatMediator;
import edu.unizg.foi.uzdiz.jfletcher20.system.CommandSystemSingleton;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

import java.util.Random;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComplaintHandler;
import edu.unizg.foi.uzdiz.jfletcher20.handlers.complaints.*;

public record User(
        String name, // Ime korisnika
        String lastName // Prezime korisnika
) implements IObserver {
    public User {
        if (name == null || lastName == null) {
            throw new IllegalArgumentException("Ime i prezime korisnika ne smiju biti null");
        } else if (name.isBlank()) {
            throw new IllegalArgumentException("Ime korisnika ne smije biti prazno");
        } else if (lastName.isBlank()) {
            throw new IllegalArgumentException("Prezime korisnika ne smije biti prazno");
        }
    }

    public User(String fullName) {
        this(fullName.split(" ", -1)[0], fullName.split(" ", -1)[1]);
    }

    @Override
    public void update(String trainID, String stationName) {
        Logs.voyage(this, trainID, stationName);
        chat().broadcast(trainID, this,
                "Vlak " + trainID + " stigao je na stanicu " + stationName, true);
        complain(trainID, stationName);
    }

    @Override
    public String toString() {
        return name.substring(0, 1).toUpperCase() + ". " + lastName;
    }

    private ChatMediator chat() {
        return CommandSystemSingleton.getInstance().getUserChat();
    }

    private void complain(String trainID, String stationName) {
        int severity = new Random().nextInt(11);

        IComplaintHandler level1 = new Level1ComplaintHandler();
        IComplaintHandler level2 = new Level2ComplaintHandler();
        IComplaintHandler level3 = new Level3ComplaintHandler();
        IComplaintHandler level4 = new Level4ComplaintHandler();

        level1.setNext(level2);
        level2.setNext(level3);
        level3.setNext(level4);

        boolean shouldComplain = new Random().nextBoolean();
        if (shouldComplain)
            level1.handleComplaint(severity, this, trainID, stationName);
    }

}
