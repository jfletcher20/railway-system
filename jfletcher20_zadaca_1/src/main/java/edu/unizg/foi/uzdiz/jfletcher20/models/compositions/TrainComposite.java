package edu.unizg.foi.uzdiz.jfletcher20.models.compositions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IObserver;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ISubject;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.Schedule;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationLeaf;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackStageComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.railway.RailwaySingleton;

public class TrainComposite implements IComponent, ISubject {

    private Map<String, Set<IObserver>> observers = new HashMap<>();
    public String trainID;
    private List<TrainTrackStageComposite> children = new ArrayList<TrainTrackStageComposite>();

    public List<TrainTrackStageComposite> getChildren() {
        List<TrainTrackStageComposite> sortedChildren = new ArrayList<>(this.children);
        sortedChildren.sort((a, b) -> a.fromTime().compareTo(b.fromTime()));
        return sortedChildren;
    }

    public TrainComposite(Schedule schedule) {
        this.trainID = schedule.scheduledTrainID();
        this.Add(new TrainTrackStageComposite(schedule));
    }

    public TrainComposite(Schedule schedule, ScheduleComposite scheduleComposite) {
        this(schedule);
    }

    @Override
    public void Operation() {
        Logs.i("\t" + this.trainID);
        for (TrainTrackStageComposite child : this.children) {
            if (this.trainID.equals("2212")) {
                child.Operation();
            } else {
                System.out.print(".");
            }
        }
    }

    public List<String> commandIV() {
        String firstStationName = this.children.getFirst().schedule.departure().name();
        String finalStationName = this.children.getLast().schedule.destination().name();
        ScheduleTime firstStationTime = this.children.getFirst().fromTime();
        ScheduleTime arrivalTime = this.children.getLast().toTime();
        double distance = this.compileDistance();
        return List.of(
                trainID,
                firstStationName, finalStationName,
                firstStationTime.toString(), arrivalTime.toString(),
                String.valueOf(distance));
    }

    public double compileDistance() {
        double distance = 0;
        for (TrainTrackStageComposite child : this.children) {
            distance += child.compileDistance();
        }
        return distance;
    }

    @Override
    public int Add(IComponent component) {
        if (!(component instanceof TrainTrackStageComposite)) {
            Logs.e("Pokušaj dodavanja pogrešnog tipa komponente u TrainComposite::Add(): "
                    + component.getClass().getName());
            return 0;
        }
        for (TrainTrackStageComposite child : this.children) {
            if (child.equals(component)) {
                Logs.e("Pokušaj dodavanja iste komponente u TrainComposite::Add()");
                return 0;
            }
        }
        // use insertion sort to add according to departure time
        TrainTrackStageComposite newStage = (TrainTrackStageComposite) component;
        for (int i = 0; i < this.children.size(); i++) {
            TrainTrackStageComposite stage = this.children.get(i);
            if (newStage.fromTime().compareTo(stage.fromTime()) < 0) {
                this.children.add(i, newStage);
                return 1;
            }
        }
        return this.children.add(newStage) ? 1 : 0;
    }

    @Override
    public int Remove(IComponent component) {
        if (!(component instanceof TrainTrackStageComposite)) {
            Logs.e("Pokušaj uklanjanja pogrešnog tipa komponente iz TrainComposite::Remove(): "
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

    public List<List<String>> commandIEV() {
        List<List<String>> commandIEV = new ArrayList<List<String>>();
        for (TrainTrackStageComposite child : this.children) {
            List<String> output = new ArrayList<String>();
            output.add(0, this.trainID);
            output.addAll(child.commandIEV());
            commandIEV.add(output);
        }
        return commandIEV;
    }

    public List<List<String>> commandIEVD(Set<Weekday> days) {
        List<List<String>> commandIEVD = new ArrayList<List<String>>();
        var stages = this.children.stream().filter(stage -> stage.schedule.days().containsAll(days)).toList();
        if (stages == null || stages.isEmpty()) {
            Logs.e("Nisu pronađeni dani vožnje");
            return commandIEVD;
        }
        for (TrainTrackStageComposite child : stages) {
            List<String> output = new ArrayList<String>();
            output.add(0, this.trainID);
            output.addAll(child.commandIEVD());
            commandIEVD.add(output);
        }
        return commandIEVD;

    }

    public List<List<String>> commandIVRV() {
        List<List<String>> result = new ArrayList<>();
        double cumulativeDistance = 0.0;
        ScheduleTime departureTime = this.children.get(0).fromTime();
        var stages = new ArrayList<>(this.children);
        stages.sort((a, b) -> a.fromTime().compareTo(b.fromTime()));
        List<List<StationLeaf>> stationLeaves = new ArrayList<>();
        for (TrainTrackStageComposite stage : stages) {
            List<StationLeaf> compatibleLeaves = stage.getCompatibleLeaves();
            stationLeaves.add(compatibleLeaves);
        }
        int s = 0;
        for (List<StationLeaf> stage : stationLeaves) {
            TrainTrackStageComposite trainTrackStage = stages.get(s++);
            List<Station> stations = stage.stream().map(StationLeaf::getStation).toList();
            for (int i = 0; i < stations.size(); i++) {
                Station station = stations.get(i);
                int stationIndex = i;
                String stationName = station.name();
                double distance = 0.0;
                if (stationIndex == 0) {
                    cumulativeDistance += distance;
                    departureTime = trainTrackStage.fromTime();
                } else {
                    Station prevStation = stations.get(stationIndex - 1);
                    distance = RailwaySingleton.getInstance().calculateDistance(prevStation, station);
                    cumulativeDistance += distance;
                    var stationMap = trainTrackStage.getInverseStationMap();
                    var stationLeaf = stage.get(stationIndex);
                    ScheduleTime arrivalTime = stationMap.get(stationLeaf) == null
                            ? departureTime.addMinutes(station.timeForTrainType(trainTrackStage.schedule.trainType()))
                            : stationMap.get(stationLeaf);
                    departureTime = arrivalTime;
                }
                if (!station.supportsTrainType(trainTrackStage.schedule.trainType()))
                    continue;
                List<String> row = Arrays.asList(this.trainID, trainTrackStage.trackID, stationName,
                        departureTime == null ? "-" : departureTime.toString(),
                        String.format("%.2f", cumulativeDistance));
                result.add(row);
            }
        }
        return result;
    }

    public double calculateCumulativeDistance(String startStationName, String endStationName) {
        double cumulativeDistance = 0.0;
        boolean startFound = false, endFound = false;
        var stages = new ArrayList<>(this.children);
        stages.sort((a, b) -> a.fromTime().compareTo(b.fromTime()));
        for (TrainTrackStageComposite stage : stages) {
            List<Station> stations = stage.getCompatibleLeaves().stream().map(StationLeaf::getStation).toList();
            for (int i = 0; i < stations.size(); i++) {
                Station station = stations.get(i);
                if (station.name().equals(startStationName)) {
                    startFound = true;
                    continue;
                }
                if (startFound && !endFound)
                    if (i > 0) {
                        Station prevStation = stations.get(i - 1);
                        cumulativeDistance += RailwaySingleton.getInstance().calculateDistance(prevStation, station);
                    }

                if (station.name().equals(endStationName)) {
                    endFound = true;
                    break;
                }
            }
            if (endFound)
                break;
        }
        if (!startFound || !endFound)
            throw new IllegalArgumentException("Ne postoji takva relacija za taj vlak");
        return cumulativeDistance;
    }

    public TrainType getTrainType() {
        return this.children.get(0).schedule.trainType();
    }

    @Override
    public void registerObserver(IObserver observer) {
        if (observers.containsKey(trainID)) {
            observers.get(trainID).add(observer);
            return;
        } else {
            observers.put(trainID, new HashSet<IObserver>());
            observers.get(trainID).add(observer);
        }
    }

    @Override
    public void removeObserver(IObserver observer) {
        if (!observers.containsKey(trainID)) {
            return;
        }
        observers.get(trainID).remove(observer);
    }

    @Override
    public void notifyObservers(String stationName) {
        if (observers.containsKey(trainID))
            for (IObserver observer : observers.get(trainID))
                observer.update(trainID, stationName);
        if (observers.containsKey(trainID + ":" + stationName))
            for (IObserver observer : observers.get(trainID + ":" + stationName))
                observer.update(trainID, stationName);
    }

    public void arriveAtStation(String stationName) {
        notifyObservers(stationName);
    }

    public void registerObserver(User user, String station) {
        String key = trainID + ":" + station;
        if (observers.containsKey(key)) {
            observers.get(key).add(user);
            return;
        } else {
            observers.put(key, new HashSet<IObserver>());
            observers.get(key).add(user);
        }
    }

    public boolean hasStation(String station) {
        for (TrainTrackStageComposite stage : this.children) {
            for (StationLeaf leaf : stage.children) {
                if (leaf.getStation().name().equals(station)) {
                    return true;
                }
            }
        }
        return false;
    }

    public StationLeaf getStationLeaf(String station) {
        for (TrainTrackStageComposite stage : this.children) {
            for (StationLeaf leaf : stage.children) {
                if (leaf.getStation().name().equals(station)) {
                    return leaf;
                }
            }
        }
        return null;
    }

    public TrainTrackStageComposite firstWithStation(String station) {
        for (TrainTrackStageComposite stage : this.children) {
            if (stage.hasStation(station)) {
                return stage;
            }
        }
        return null;
    }

    public boolean operatesOnDay(Weekday day) {
        return this.children.stream()
                .anyMatch(stage -> stage.schedule.days().contains(day));
    }

    public boolean hasStations(List<String> stationNames) {
        for (String stationName : stationNames) {
            if (!this.hasStation(stationName)) {
                return false;
            }
        }
        return true;
    }

    public ScheduleTime getDepartureTimeAtStation(Station start) {
        return this.getDepartureTimeAtStation(start.name());
    }

    public ScheduleTime getArrivalTimeAtStation(Station end) {
        return this.getArrivalTimeAtStation(end.name());
    }

    public List<Map<String, String>> commandIVI2S(String displayFormat) {
        List<Map<String, String>> commandIVI2S = new ArrayList<>();
        for (TrainTrackStageComposite stage : this.children) {
            commandIVI2S.add(stage.commandIVI2S(displayFormat));
        }
        return commandIVI2S;
    }

    public boolean startsAfter(ScheduleTime startTime) {
        if (this.children == null || this.children.isEmpty())
            return false;
        return this.getChildren().stream().allMatch(stage -> stage.fromTime().isAfter(startTime));
    }

    public boolean endsBefore(ScheduleTime endTime) {
        if (this.children == null || this.children.isEmpty())
            return false;
        return this.getChildren().stream().allMatch(stage -> stage.toTime().isBefore(endTime));
    }

    public TrainTrackStageComposite getTrackStageAtStation(Station start) {
        if (this.children.isEmpty() || !this.hasStation(start.name()))
            return null;
        return this.firstWithStation(start.name());
    }

    public List<Station> getStationsBetween(String start, String end) {
        Station startStation = null;
        Station endStation = null;
        List<Station> stations = new ArrayList<>();
        for (TrainTrackStageComposite stage : this.children) {
            for (StationLeaf leaf : stage.children) {
                if (leaf.getStation().name().equals(start))
                    startStation = leaf.getStation();
                if (leaf.getStation().name().equals(end))
                    endStation = leaf.getStation();
            }
            if (startStation != null && endStation != null)
                break;
        }
        if (startStation == null || endStation == null)
            return stations;
        for (TrainTrackStageComposite stage : this.children) {
            for (StationLeaf leaf : stage.children) {
                if (leaf.getStation().equals(startStation)) {
                    stations.add(leaf.getStation());
                }
                if (leaf.getStation().equals(endStation)) {
                    stations.add(leaf.getStation());
                    break;
                }
            }
        }
        return stations;
    }

    public boolean isStationBefore(String startStation, String endStation) {
        boolean foundStart = false;
        boolean foundEnd = false;
        for (TrainTrackStageComposite stage : this.children) {
            for (StationLeaf leaf : stage.children) {
                if (leaf.getStation().name().equals(startStation)) {
                    foundStart = true;
                }
                if (foundStart && leaf.getStation().name().equals(endStation)) {
                    foundEnd = true;
                    break;
                }
            }
            if (foundEnd) {
                break;
            }
        }
        return foundStart && foundEnd;
    }

    public ScheduleTime getDepartureTime(Weekday day) {
        if (this.children.isEmpty())
            return null;
        TrainTrackStageComposite firstStage = this.children.get(0);
        if (firstStage.schedule.days().contains(day))
            return firstStage.fromTime();
        else
            return null;
    }

    public boolean isCurrentlyAtStation(ScheduleTime currentTime) {
        for (TrainTrackStageComposite stage : this.children) {
            Map<ScheduleTime, StationLeaf> stationMap = stage.getStationMap();
            for (ScheduleTime time : stationMap.keySet()) {
                if (time.equals(currentTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Station getCurrentStation(ScheduleTime currentTime) {
        for (TrainTrackStageComposite stage : this.children) {
            Map<ScheduleTime, StationLeaf> stationMap = stage.getStationMap();
            for (ScheduleTime time : stationMap.keySet()) {
                if (time.equals(currentTime)) {
                    return stationMap.get(time).getStation();
                }
            }
        }
        return null;
    }

    public boolean hasReachedDestination(ScheduleTime currentTime) {
        if (this.children.isEmpty())
            return false;
        TrainTrackStageComposite lastStage = this.children.get(this.children.size() - 1);
        return lastStage.toTime().equals(currentTime);
    }

    public int getStationRole(Station currentStation) {
        return -1;
    }

    public String getTypeOfStation(Station currentStation) {
        int type = this.getStationRole(currentStation);
        return switch (type) {
            case 0 -> "polaznoj stanici";
            case 1 -> "stanici";
            case 2 -> "odredišnoj stanici";
            default -> "stanici";
        };
    }

    public boolean sameTrainTypeAcrossStages() {
        if (this.children.isEmpty())
            return false;
        Set<TrainType> trainTypes = this.children.stream().map(stage -> stage.schedule.trainType()).collect(
                HashSet::new,
                HashSet::add, HashSet::addAll);
        return trainTypes.size() == 1;
    }

    public ScheduleTime getDepartureTimeAtStation(String start) {
        if (this.children.isEmpty() || !this.hasStation(start))
            return null;
        TrainTrackStageComposite withStation = this.firstWithStation(start);
        if (withStation == null)
            return null;
        // calculate the schedule time of departure for the station
        ScheduleTime departureTime = withStation.fromTime(start);
        for (StationLeaf leaf : withStation.children) {
            if (leaf.getStation().name().equals(start)) {
                break;
            }
            departureTime = departureTime
                    .addMinutes(leaf.getStation().timeForTrainType(withStation.schedule.trainType()));
        }
        return departureTime;
    }

    public ScheduleTime getAltDepartureTimeAtStation(String start) {
        if (this.children.isEmpty() || !this.hasStation(start))
            return null;
        TrainTrackStageComposite withStation = this.firstWithStation(start);
        if (withStation == null)
            return null;
        // calculate the schedule time of departure for the station
        ScheduleTime departureTime = withStation.fromTime(start);
        return departureTime;
    }

    public ScheduleTime getArrivalTimeAtStation(String end) {
        if (this.children.isEmpty() || !this.hasStation(end))
            return null;
        TrainTrackStageComposite withStation = this.firstWithStation(end);
        if (withStation == null)
            return null;
        ScheduleTime arrivalTime = withStation.arrivalTime(end);
        for (StationLeaf leaf : withStation.children) {
            arrivalTime = arrivalTime
                    .addMinutes(leaf.getStation().timeForTrainType(withStation.schedule.trainType()));
            if (leaf.getStation().name().equals(end)) {
                break;
            }
        }
        return arrivalTime;
    }

    public double getDistanceBetweenStations(String departureStation, String arrivalStation) {
        StationLeaf departureLeaf = this.getStationLeaf(departureStation);
        StationLeaf arrivalLeaf = this.getStationLeaf(arrivalStation);
        if (departureLeaf == null || arrivalLeaf == null)
            return 0.0;
        return this.calculateCumulativeDistance(departureStation, arrivalStation);
    }

    public boolean hasStationsOnDay(Weekday weekday, List<String> of) {
        if (!hasStations(of) || !operatesOnDay(weekday))
            return false;
        // get all stages with that weekday
        List<TrainTrackStageComposite> stages = new ArrayList<>();
        for (TrainTrackStageComposite stage : this.children) {
            if (stage.schedule.days().contains(weekday)) {
                stages.add(stage);
            }
        }
        // iterate through the stages and check if a match for the name in of is found
        // for all elements from of
        int found = 0;
        for (String stationName : of) {
            for (TrainTrackStageComposite stage : stages) {
                for (StationLeaf leaf : stage.children) {
                    if (leaf.getStation().name().equals(stationName)) {
                        found++;
                    }
                }
            }
        }
        return found == of.size();
    }

    public boolean hasStationOnDay(Weekday weekday, String of) {
        return hasStationsOnDay(weekday, List.of(of));
    }

    public boolean hasRouteForParameters(Weekday weekday, String start, String end, ScheduleTime from, ScheduleTime to) {
        if (!hasStation(start) || !hasStation(end) || !operatesOnDay(weekday))
            return false;
        if (!isStationBefore(start, end))
            return false;
            // get departure time at station from and arrival time at station end, and make sure they are within from-to
        ScheduleTime departureTime = getDepartureTimeAtStation(start);
        ScheduleTime arrivalTime = getArrivalTimeAtStation(end);
        return departureTime.isBetweenOrEqual(from, to) && arrivalTime.isBetweenOrEqual(from, to);
    }

    public ScheduleTime getDepartureTimeAtStation(String departureStation, Weekday weekday) {
        if (this.children.isEmpty() || !this.hasStation(departureStation))
            return null;
        // get all stages with that weekday
        List<TrainTrackStageComposite> stages = new ArrayList<>();
        for (TrainTrackStageComposite stage : this.children) {
            if (stage.schedule.days().contains(weekday)) {
                stages.add(stage);
            }
        }
        // iterate through the stages and check if a match for the name in of is found
        // for all elements from of
        for (TrainTrackStageComposite stage : stages) {
            for (StationLeaf leaf : stage.children) {
                if (leaf.getStation().name().equals(departureStation)) {
                    return stage.getInverseStationMap().get(leaf);
                }
            }
        }
        return null;
    }

    public ScheduleTime getArrivalTimeAtStation(String arrivalStation, Weekday weekday) {
        if (this.children.isEmpty() || !this.hasStation(arrivalStation))
            return null;
        // get all stages with that weekday
        List<TrainTrackStageComposite> stages = new ArrayList<>();
        for (TrainTrackStageComposite stage : this.children) {
            if (stage.schedule.days().contains(weekday)) {
                stages.add(stage);
            }
        }
        // iterate through the stages and check if a match for the name in of is found
        // for all elements from of
        for (TrainTrackStageComposite stage : stages) {
            for (StationLeaf leaf : stage.children) {
                if (leaf.getStation().name().equals(arrivalStation)) {
                    return stage.getInverseStationMap().get(leaf);
                }
            }
        }
        return null;
    }

}
