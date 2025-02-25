package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.Schedule;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationLeaf;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.railway.RailwaySingleton;

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
            this.Add(new StationLeaf(stations.stream()
                    .filter(station -> station.name().equals(schedule.departure().name())
                            && station.supportsTrainType(schedule.trainType()))
                    .findFirst().orElse(stations.get(0))));
            for (int i = stations.indexOf(schedule.departure()) + 1; i < stations.size(); i++) {
                Station station = stations.get(i);
                this.Add(new StationLeaf(station));
                if (station.equals(schedule.destination())) {
                    break;
                }
            }
        } else {
            this.Add(new StationLeaf(stations.reversed().stream()
                    .filter(station -> station.name().equals(schedule.departure().name())
                            && station.supportsTrainType(schedule.trainType()))
                    .findFirst().orElse(stations.get(stations.size() - 1))));
            for (int i = stations.indexOf(schedule.departure()) - 1; i >= 0; i--) {
                Station station = stations.get(i);
                this.Add(new StationLeaf(station));
                if (station.equals(schedule.destination())) {
                    break;
                }
            }
        }
    }

    public Map<ScheduleTime, StationLeaf> getStationMap() {
        Map<ScheduleTime, StationLeaf> stationMap = new HashMap<>();
        // get the compiled schedule
        var compiledSchedule = this.compileRoute(this.schedule);
        // iterate through the compiled schedule to get the time it takes to get to each
        // station and map out the times
        ScheduleTime time = this.schedule.departureTime();
        for (StationLeaf station : compiledSchedule) {
            if (station.getStation().equals(this.schedule.departure())) {
                stationMap.put(fromTime(), station);
                continue;
            } else if (station.getStation().equals(this.schedule.destination())) {
                stationMap.put(toTime(), station);
                break;
            } else {
                time = time.addMinutes(station.getStation().timeForTrainType(this.schedule.trainType()));
                stationMap.put(time, station);
            }
        }
        return stationMap;
    }

    public Map<StationLeaf, ScheduleTime> getInverseStationMap() {
        var stationMap = this.getStationMap();
        return stationMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
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

    public List<StationLeaf> compileRoute(Schedule schedule) {
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

    public List<Station> getStations() {
        return this.children.stream()
                .map(StationLeaf::getStation)
                .collect(Collectors.toList());
    }

    public List<StationLeaf> getCompatibleLeaves() {
        return this.children.stream().filter(child -> child.getStation().supportsTrainType(schedule.trainType()))
                .toList();
    }

    public boolean hasStation(String station) {
        return this.children.stream().anyMatch(child -> child.getStation().name().equals(station));
    }

    public ScheduleTime fromTime(String name) {
        StationLeaf station = this.children.stream().filter(child -> child.getStation().name().equals(name)).findFirst()
                .orElse(null);
        if (station == null) {
            Logs.e("Stanica " + name + " nije pronađena u traci " + this.trackID);
            return null;
        }
        // iterate through the children until we find the station, compiling the time it
        // takes to get there
        ScheduleTime time = this.schedule.departureTime();
        for (StationLeaf child : this.children) {
            if (child.equals(station)) {
                return time;
            }
            time = time.addMinutes(child.getStation().timeForTrainType(this.schedule.trainType()));
        }
        return null;
    }

    public ScheduleTime arrivalTime(String name) {
        StationLeaf station = this.children.stream().filter(child -> child.getStation().name().equals(name)).findFirst()
                .orElse(null);
        if (station == null) {
            Logs.e("Stanica " + name + " nije pronađena u traci " + this.trackID);
            return null;
        }
        // iterate through the children
        ScheduleTime time = this.schedule.departureTime();
        for (StationLeaf child : this.children) {
            time = time.addMinutes(child.getStation().timeForTrainType(this.schedule.trainType()));
            if (child.equals(station)) {
                return time;
            }
        }
        return null;
    }

    public double distanceFromStart(String name) {
        StationLeaf station = this.children.stream().filter(child -> child.getStation().name().equals(name)).findFirst()
                .orElse(null);
        if (station == null) {
            Logs.e("Stanica " + name + " nije pronađena u traci " + this.trackID);
            return -1;
        }
        double distance = 0;
        for (StationLeaf child : this.children) {
            if (child.equals(station))
                return distance;
            distance += child.getStation().getDistanceFromStart();
        }
        return -1;
    }

    public double distanceBetweenStations(String name1, String name2) {
        StationLeaf station1 = this.children.stream().filter(child -> child.getStation().name().equals(name1))
                .findFirst().orElse(null);
        StationLeaf station2 = this.children.stream().filter(child -> child.getStation().name().equals(name2))
                .findFirst().orElse(null);
        if (station1 == null) {
            Logs.e("Stanica " + name1 + " nije pronađena u traci " + this.trackID);
            return -1;
        }
        if (station2 == null) {
            Logs.e("Stanica " + name2 + " nije pronađena u traci " + this.trackID);
            return -1;
        }
        return station1.getStation().getDistanceTo(station2.getStation());
    }

    public Map<String, String> commandIVI2S(String displayFormat) {
        return new HashMap<>();
    }

}