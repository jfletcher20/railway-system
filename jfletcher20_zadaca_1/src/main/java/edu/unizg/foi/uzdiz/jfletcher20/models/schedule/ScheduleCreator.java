package edu.unizg.foi.uzdiz.jfletcher20.models.schedule;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class ScheduleCreator implements ICreator {

    private static int columnCount = 9;

    public ScheduleCreator() {
    }

    @Override
    public Schedule factoryMethod(String data, int row) {
        if (data == null || data.isEmpty()) {
            Logs.w(row, "ScheduleCreator Prazan redak.");
            return null;
        } else if (data.split(";").length != columnCount) {
            Logs.w(row, columnCountError(data.split(";").length));
            return null;
        }

        String[] parts = data.split(";");
        return new Schedule(
                parts[0], // trackID
                TraversalDirection.fromString(parts[1]), // direction
                parts[2], // departure
                parts[3], // destination
                parts[4], // scheduledTrainID
                TrainType.fromString(parts[5]), // trainType
                parts[6], // departureTime
                parts[7], // travelTime
                RailwaySingleton.getInstance().getScheduleDays(parts[8]).days() // days
        );
    }

    private String columnCountError(int counts) {
        return "ScheduleCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
    }

}

/*
 * 
 * 
 * 
 * Kod voznog reda obavezna je oznaka pruge, smjer (N ili O), oznaka vlaka,
 * vrijeme
 * polaska. Kada nije upisana polazna željeznička stanica tada se uzima prva
 * stanica pruge s
 * obzirom na smjer. Kada nije odredišna željeznička stanica tada se uzima
 * zadnja željeznička
 * stanica pruge s obzirom na smjer.
 * 
 * 
 * Oznaka pruge;Smjer;Polazna stanica;Odredišna stanica;Oznaka vlaka;Vrsta
 * vlaka;Vrijeme polaska;Trajanje vožnje;Oznaka dana
 * M501;N;;Čakovec;3301;;4:11;0:35;4
 * M501;N;;Čakovec;3007;;5:43;0:47;
 * M501;N;Čakovec;;3901;;6:34;0:12;3
 * M501;N;;Čakovec;3303;;6:40;0:48;3
 * M501;N;;Čakovec;3305;;8:10;0:54;
 * 
 * public record Schedule(
 * String trackID, // Oznaka pruge
 * TraversalDirection direction, // Smjer
 * String departure, // Polazna stanica
 * String destination, // Odredišna stanica
 * String scheduledTrainID, // Oznaka vlaka
 * TrainType trainType, // Vrsta vlaka
 * String departureTime, // Vrijeme polaska
 * String travelTime, // Trajanje vožnje
 * Weekday days // Oznaka dana
 * ) implements IProduct {
 * 
 * public Schedule(
 * String trackID, // Oznaka pruge: obavezna
 * TraversalDirection direction, // Smjer: obavezna
 * String departure, // Polazna stanica
 * String destination, // Odredišna stanica
 * String scheduledTrainID, // Oznaka vlaka: obavezna
 * TrainType trainType, // Vrsta vlaka
 * String departureTime, // Vrijeme polaska: obavezna // Format: HH:mm
 * String travelTime, // Trajanje vožnje
 * Weekday days // Oznaka dana
 * ) {
 * if (trackID == null || trackID.isEmpty()) {
 * throw new IllegalArgumentException("Oznaka pruge je obavezna.");
 * } else if (direction == null) {
 * throw new IllegalArgumentException("Smjer je obavezan.");
 * } else if (scheduledTrainID == null || scheduledTrainID.isEmpty()) {
 * throw new IllegalArgumentException("Oznaka vlaka je obavezna.");
 * } else if (departureTime == null || departureTime.isEmpty()) {
 * throw new IllegalArgumentException("Vrijeme polaska je obavezno.");
 * }
 * this.trackID = trackID;
 * this.direction = direction;
 * this.departure = departure;
 * this.destination = destination;
 * this.scheduledTrainID = scheduledTrainID;
 * this.trainType = trainType;
 * this.departureTime = departureTime;
 * this.travelTime = travelTime;
 * this.days = days;
 * }
 * 
 * }
 * 
 */