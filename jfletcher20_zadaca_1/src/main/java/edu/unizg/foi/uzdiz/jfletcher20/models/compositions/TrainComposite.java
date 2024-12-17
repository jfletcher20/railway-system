package edu.unizg.foi.uzdiz.jfletcher20.models.compositions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public List<TrainTrackStageComposite> children = new ArrayList<TrainTrackStageComposite>();

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
        return this.children.add((TrainTrackStageComposite) component) ? 1 : 0;
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

    public List<List<String>> commandIEVD() {
        List<List<String>> commandIEVD = new ArrayList<List<String>>();
        for (TrainTrackStageComposite child : this.children) {
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

        // copy the list of children
        var stages = new ArrayList<>(this.children);
        // sort the list of children depending if the schedule departure time is before the next, so that the earliest is first
        stages.sort((a, b) -> a.fromTime().compareTo(b.fromTime()));

        // iterate over every stage and get the list of StationLeafs for each stage
        List<List<StationLeaf>> stationLeaves = new ArrayList<>();
        for (TrainTrackStageComposite stage : stages) {
            List<StationLeaf> compatibleLeaves = stage.getCompatibleLeaves();
            stationLeaves.add(compatibleLeaves);
        }

        // iterate over the stationLeaves and calculate the time and cumulative distance
        for (int i = 0; i < stationLeaves.size(); i++) {
            List<StationLeaf> leaves = stationLeaves.get(i);
            TrainTrackStageComposite stage = stages.get(i);

            for (int j = 0; j < leaves.size(); j++) {
                StationLeaf stationLeaf = leaves.get(j);
                Station station = stationLeaf.getStation();
                String stationName = station.name();

                // Calculate cumulative distance
                if (i == 0 && j == 0) {
                    cumulativeDistance = 0.0; // Starting station of the first stage
                } else if (j == 0) {
                    // First station of a new stage
                    TrainTrackStageComposite prevStage = stages.get(i - 1);
                    StationLeaf prevStationLeaf = stationLeaves.get(i - 1).get(stationLeaves.get(i - 1).size() - 1);
                    Station prevStation = prevStationLeaf.getStation();
                    double distance = RailwaySingleton.getInstance().calculateDistance(prevStation, station);
                    cumulativeDistance += distance;
                } else {
                    // Calculate distance from previous station in the same stage
                    StationLeaf prevStationLeaf = leaves.get(j - 1);
                    Station prevStation = prevStationLeaf.getStation();
                    double distance = RailwaySingleton.getInstance().calculateDistance(prevStation, station);
                    cumulativeDistance += distance;
                }

                departureTime = departureTime.addMinutes(station.timeForTrainType(stage.schedule.trainType()));

                List<String> row = Arrays.asList(
                    this.trainID,
                    stage.trackID,
                    stationName,
                    departureTime.toString(),
                    String.format("%.2f", cumulativeDistance)
                );
                result.add(row);
            }
        }

        // for (TrainTrackStageComposite stage : children) {
        //     String trackID = stage.trackID;
        //     List<StationLeaf> stations = stage.children;

        //     for (int i = 0; i < stations.size(); i++) {
        //         StationLeaf stationLeaf = stations.get(i);
        //         Station station = stationLeaf.getStation();
        //         String stationName = station.name();

        //         ScheduleTime departureTime = stage.fromTime();

        //         // Calculate cumulative distance
        //         if (i == 0 && stage == children.get(0)) {
        //             cumulativeDistance = 0.0; // Starting station of the first stage
        //         } else if (i == 0) {
        //             // First station of a new stage
        //             TrainTrackStageComposite prevStage = children.get(children.indexOf(stage) - 1);
        //             StationLeaf prevStationLeaf = prevStage.children.get(prevStage.children.size() - 1);
        //             Station prevStation = prevStationLeaf.getStation();
        //             double distance = RailwaySingleton.getInstance().calculateDistance(prevStation, station);
        //             cumulativeDistance += distance;
        //         } else {
        //             // Calculate distance from previous station in the same stage
        //             StationLeaf prevStationLeaf = stations.get(i - 1);
        //             Station prevStation = prevStationLeaf.getStation();
        //             double distance = RailwaySingleton.getInstance().calculateDistance(prevStation, station);
        //             cumulativeDistance += distance;
        //         }

        //         List<String> row = Arrays.asList(
        //             this.trainID,
        //             trackID,
        //             stationName,
        //             departureTime.toString(),
        //             String.format("%.2f", cumulativeDistance)
        //         );
        //         result.add(row);
        //     }
        // }
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