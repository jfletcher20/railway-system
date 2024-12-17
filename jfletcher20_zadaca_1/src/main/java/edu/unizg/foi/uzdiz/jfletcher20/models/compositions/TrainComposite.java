package edu.unizg.foi.uzdiz.jfletcher20.models.compositions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.Schedule;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationLeaf;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackStageComposite;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

public class TrainComposite implements IComponent {

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
        // traverse all children and output the operation on each child
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
        int i = 0;
        for (List<StationLeaf> stage : stationLeaves) {
            TrainTrackStageComposite trainTrackStage = stages.get(i++);
            for (Station station : stage.stream().map(StationLeaf::getStation).toList()) {
                int stationIndex = stage.stream().map(StationLeaf::getStation).toList().indexOf(station);
                String stationName = station.name();
                double distance = 0.0;
                if (stationIndex == 0) {
                    cumulativeDistance += distance;
                    departureTime = trainTrackStage.fromTime();
                } else {
                    Station prevStation = stage.get(stationIndex - 1).getStation();
                    distance = RailwaySingleton.getInstance().calculateDistance(prevStation, station);
                    cumulativeDistance += distance;
                    departureTime = departureTime.addMinutes(station.timeForTrainType(trainTrackStage.schedule.trainType()));
                }
                if (!station.supportsTrainType(trainTrackStage.schedule.trainType())) continue;
                List<String> row = Arrays.asList(
                    this.trainID,
                    trainTrackStage.trackID,
                    stationName,
                    departureTime.toString(),
                    String.format("%.2f", cumulativeDistance)
                );
                result.add(row);
            }
        }
        return result;
    }

}

/*
 * ● Pregled vlakova koji voze sve etape na određene dane u tjednu
 * ○ Sintaksa:
 * ■ IEVD dani
 * ○ Primjer:
 * ■ IEVD PoSrPeN
 * ○ Opis primjera:
 * ■ Ispis tablice sa vlakovima i njihovim etapama koje voze na određene dane
 * u tjednu (oznaka vlaka, oznaka pruge, polazna željeznička stanica etape,
 * odredišna željeznička stanica etape, vrijeme polaska s polazne željezničke
 * stanice etape, vrijeme dolaska u odredišnu željezničke stanicu etape
 * daniUTjednu za etapu).
 */