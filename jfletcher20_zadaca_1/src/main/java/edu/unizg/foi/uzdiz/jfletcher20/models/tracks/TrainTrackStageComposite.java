package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.Schedule;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationLeaf;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

public class TrainTrackStageComposite implements IComposite {

    public List<StationLeaf> children = new ArrayList<StationLeaf>();
    public String trackID;
    public Schedule schedule;

    public TrainTrackStageComposite(Schedule schedule) {
        this.schedule = schedule;
        this.trackID = schedule.trackID();
        List<Station> stations = RailwaySingleton.getInstance().getStationsOnTrack(trackID, schedule.trainType());
        if (stations == null) {
            Logs.e("Nepostojeće stanice na traci " + trackID + " za tip vlaka " + schedule.trainType().name());
            return;
        }
        if (schedule.direction() == TraversalDirection.FORTH) {
            this.Add(new StationLeaf(stations.get(0)));
            for (int i = stations.indexOf(schedule.departure()) + 1; i < stations.size(); i++) {
                Station station = stations.get(i);
                this.Add(new StationLeaf(station));
                if (station.equals(schedule.destination())) {
                    break;
                }
            }
        } else {
            for (int i = stations.indexOf(schedule.departure()) - 1; i >= 0; i--) {
                Station station = stations.get(i);
                this.Add(new StationLeaf(station));
                if (station.equals(schedule.destination())) {
                    break;
                }
            }
        }
    }

    @Override
    public void Operation() {
        Logs.i("\t\tSTAGE | " + this.trackID + " | " + this.schedule.scheduledTrainID() + " | "
                + this.schedule.departure().name() + " -> " + this.schedule.destination().name() + " | " +
                this.schedule.departureTime().toString() + " -> " + this.toTime().toString());
        for (StationLeaf child : this.children) {
            child.Operation();
        }
    }

    public List<StationLeaf> compileSchedule(Schedule schedule) {
        List<StationLeaf> compatibleLeaves = new ArrayList<StationLeaf>();
        for (StationLeaf child : this.children) {
            if (child.getStation().supportsTrainType(schedule.trainType())) {
                compatibleLeaves.add(child);
            }
        }
        return compatibleLeaves;
    }

    public double compileDistance() {
        return RailwaySingleton.getInstance().getDistanceBetweenStations(schedule);
    }

    public ScheduleTime fromTime() {
        return schedule.departureTime();
    }

    public ScheduleTime toTime() {
        return new ScheduleTime(
                schedule.departureTime().getTotalTimeInMinutes() + schedule.travelTime().getTotalTimeInMinutes());
    }

    @Override
    public int Add(IComponent component) {
        if (!(component instanceof StationLeaf)) {
            Logs.e("Pokušaj dodavanja pogrešnog tipa komponente u TrainTrackStageComposite::Add(): "
                    + component.getClass().getName());
            return 0;
        }
        if (this.children.contains(component)) {
            Logs.e("Pokušaj dodavanja iste komponente u TrainTrackStageComposite::Add()");
            return 0;
        }
        if (((StationLeaf) component).getStation().supportsTrainType(this.schedule.trainType())) {
            return this.children.add((StationLeaf) component) ? 1 : 0;
        } else {
            Logs.e("Nekompatibilna stanica " + ((StationLeaf) component).getStation().name()
                    + "(" + ((StationLeaf) component).getStation().timeForTrainType(this.schedule.trainType()) + ")"
                    + " prilikom poziva TrainTrackStageComposite::Add(component) obzirom na tip vlaka "
                    + this.schedule.trainType().name());
            return 0;
        }
    }

    @Override
    public int Remove(IComponent component) {
        if (!(component instanceof StationLeaf)) {
            Logs.e("Pokušaj uklanjanja pogrešnog tipa komponente iz TrainTrackStageComposite::Remove(): "
                    + component.getClass().getName());
            return 0;
        }
        return this.children.remove(component) ? 1 : 0;
    }

    @Override
    public IComponent GetChild(int index) {
        if (index < 0 || index >= this.children.size()) {
            Logs.e("Indeks je izvan granica liste djece");
            return null;
        }
        return this.children.get(index);
    }

    public List<String> commandIEV() {
        return List.of(
                this.trackID,
                this.schedule.departure().name(),
                this.schedule.destination().name(),
                this.schedule.departureTime().toString(),
                this.toTime().toString(),
                String.valueOf(this.compileDistance()),
                Weekday.listToString(this.schedule.days()));
    }

    public List<String> commandIEVD() {
        return List.of(
                this.trackID,
                this.schedule.departure().name(),
                this.schedule.destination().name(),
                this.schedule.departureTime().toString(),
                this.toTime().toString(),
                // String.valueOf(this.compileDistance()),
                Weekday.listToString(this.schedule.days()));
    }

    public List<StationLeaf> getCompatibleLeaves() {
        return this.children.stream().filter(child -> child.getStation().supportsTrainType(schedule.trainType()))
                .toList();
    }

}