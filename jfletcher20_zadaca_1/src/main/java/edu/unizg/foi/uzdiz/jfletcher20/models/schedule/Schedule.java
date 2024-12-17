package edu.unizg.foi.uzdiz.jfletcher20.models.schedule;

import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

/*

    Kod voznog reda obavezna je oznaka pruge, smjer (N ili O), oznaka vlaka, vrijeme 
polaska. Kada nije upisana polazna željeznička stanica tada se uzima prva stanica pruge s 
obzirom na smjer. Kada nije odredišna željeznička stanica tada se uzima zadnja željeznička 
stanica pruge s obzirom na smjer. 


 * Oznaka pruge;Smjer;Polazna stanica;Odredišna stanica;Oznaka vlaka;Vrsta vlaka;Vrijeme polaska;Trajanje vožnje;Oznaka dana
M501;N;;Čakovec;3301;;4:11;0:35;4
M501;N;;Čakovec;3007;;5:43;0:47;
M501;N;Čakovec;;3901;;6:34;0:12;3
M501;N;;Čakovec;3303;;6:40;0:48;3
M501;N;;Čakovec;3305;;8:10;0:54;
 */

public record Schedule(
        String trackID, // Oznaka pruge
        TraversalDirection direction, // Smjer
        Station departure, // Polazna stanica
        Station destination, // Odredišna stanica
        String scheduledTrainID, // Oznaka vlaka
        TrainType trainType, // Vrsta vlaka
        ScheduleTime departureTime, // Vrijeme polaska
        ScheduleTime travelTime, // Trajanje vožnje
        List<Weekday> days // Dani vožnje odabrani na osnovi oznake dana
) implements IProduct {
    public Schedule(
            String trackID, // Oznaka pruge: obavezna
            TraversalDirection direction, // Smjer: obavezna
            String departure, // Polazna stanica
            String destination, // Odredišna stanica
            String scheduledTrainID, // Oznaka vlaka: obavezna
            TrainType trainType, // Vrsta vlaka
            ScheduleTime departureTime, // Vrijeme polaska: obavezna // Format: HH:mm
            ScheduleTime travelTime, // Trajanje vožnje
            List<Weekday> days // Dani vožnje odabrani na osnovi oznake dana
    ) {
        this(
                trackID, direction,
                departure.isBlank()
                        ? direction == TraversalDirection.FORTH
                                ? RailwaySingleton.getInstance().getTrackById(trackID).getStartStation(trainType)
                                : RailwaySingleton.getInstance().getTrackById(trackID).getEndStation(trainType)
                        : RailwaySingleton.getInstance().getStationsOnTrack(trackID, trainType).stream()
                                .filter(station -> station.name().equals(departure)).findFirst().orElse(null),
                destination.isBlank()
                        ? direction == TraversalDirection.FORTH
                                ? RailwaySingleton.getInstance().getTrackById(trackID).getEndStation(trainType)
                                : RailwaySingleton.getInstance().getTrackById(trackID).getStartStation(trainType)
                        : RailwaySingleton.getInstance().getStationsOnTrack(trackID, trainType).stream()
                                .filter(station -> station.name().equals(destination)).findFirst().orElse(null),
                scheduledTrainID, trainType, departureTime, travelTime, days);
    }

    public Schedule(
            String trackID, // Oznaka pruge: obavezna
            TraversalDirection direction, // Smjer: obavezna
            Station departure, // Polazna stanica
            Station destination, // Odredišna stanica
            String scheduledTrainID, // Oznaka vlaka: obavezna
            TrainType trainType, // Vrsta vlaka
            ScheduleTime departureTime, // Vrijeme polaska: obavezna // Format: HH:mm
            ScheduleTime travelTime, // Trajanje vožnje
            List<Weekday> days // Dani vožnje odabrani na osnovi oznake dana
    ) {
        if (trackID == null || trackID.isEmpty()) {
            throw new IllegalArgumentException("Oznaka pruge je obavezna.");
        } else if (direction == null) {
            throw new IllegalArgumentException("Smjer je obavezan.");
        } else if (scheduledTrainID == null || scheduledTrainID.isEmpty()) {
            throw new IllegalArgumentException("Oznaka vlaka je obavezna.");
        } else if (departureTime == null) {
            throw new IllegalArgumentException("Vrijeme polaska je obavezno.");
        }
        this.trackID = trackID;
        this.direction = direction;
        this.departure = departure;
        this.destination = destination;
        this.scheduledTrainID = scheduledTrainID;
        this.trainType = trainType;
        this.departureTime = departureTime;
        this.travelTime = travelTime;
        this.days = days;
    }

}
