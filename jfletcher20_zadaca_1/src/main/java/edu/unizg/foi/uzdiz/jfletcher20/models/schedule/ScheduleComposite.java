package edu.unizg.foi.uzdiz.jfletcher20.models.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackStageComposite;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationLeaf;

/**
 * Composite class for the Schedule
 * 
 * Schedule base structure is:
 * ScheduleComposite
 * |_ TrainComposite
 * |-|_ TrainTrackStageComposite
 * |-|-|_ StationLeaf
 * |-|-|_ StationLeaf
 * |-|_ TrainTrackStageComposite
 * |---|_ StationLeaf
 * |---|_ StationLeaf
 * |---|_ StationLeaf
 * |_ TrainComposite
 * --|_ TrainTrackStageComposite
 * ----|_ StationLeaf
 * ----|_ StationLeaf
 */
public class ScheduleComposite implements IComposite {
    public List<TrainComposite> children = new ArrayList<TrainComposite>();

    @Override
    public void Operation() {
        // traverse all children and output the operation on each child
        Logs.i("Operation() called on ScheduleComposite");
        for (TrainComposite child : this.children) {
            child.Operation();
        }
    }

    public List<List<String>> commandIV() {
        List<List<String>> commandIV = new ArrayList<List<String>>();
        for (TrainComposite child : this.children) {
            commandIV.add(child.commandIV());
        }
        return commandIV;
    }

    @Override
    public int Add(IComponent component) {
        if (component instanceof TrainComposite) {
            String newTrainID = ((TrainComposite) component).trainID;
            for (TrainComposite child : this.children) {
                if (child.trainID.equals(newTrainID)) {
                    for (TrainTrackStageComposite newStage : ((TrainComposite) component).getChildren()) {
                        child.Add(newStage);
                        return 1;
                    }
                }
            }
            return this.children.add((TrainComposite) component) ? 1 : 0;
        }
        return 0;
    }

    @Override
    public int Remove(IComponent component) {
        if (!(component instanceof TrainComposite)) {
            Logs.e("Pokušaj uklanjanja pogrešnog tipa komponente iz ScheduleComposite::Remove(): "
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

    public TrainComposite getCompositeByTrainID(String trainCode) {
        for (TrainComposite child : this.children) {
            if (child.trainID.equals(trainCode)) {
                return child;
            }
        }
        return null;
    }

    public List<List<String>> commandIEV(String trainID) {
        TrainComposite train = getCompositeByTrainID(trainID);
        if (train == null) {
            Logs.e("Vlak s oznakom " + trainID + " nije pronađen");
            return null;
        }
        return train.commandIEV();
    }

    public List<List<String>> commandIEVD(Set<Weekday> days) {
        List<List<String>> commandIEVD = new ArrayList<>();
        if (days == null || days.isEmpty()) {
            Logs.e("Nisu pronađeni dani vožnje");
            return Collections.emptyList();
        }
        for (TrainComposite child : this.children) {
            List<Schedule> schedules = new ArrayList<>();
            for (TrainTrackStageComposite stage : child.getChildren()) {
                schedules.add(stage.schedule);
            }
            boolean daysAreInSchedules = schedules.stream().anyMatch(s -> s.days().containsAll(days));
            if (daysAreInSchedules) {
                List<List<String>> newEntries = child.commandIEVD(days);
                for (List<String> entry : newEntries)
                    insertionSort(commandIEVD, entry);
            }
        }
        return commandIEVD;
    }

    /**
     * Inserts a new entry into the sorted list using insertion sort logic.
     * Sorting is based on the 'fromTime' column (index 4).
     */
    private void insertionSort(List<List<String>> list, List<String> newEntry) {
        ScheduleTime newTime = new ScheduleTime(newEntry.get(4));
        int i = 0;
        while (i < list.size()) {
            ScheduleTime currentTime = new ScheduleTime(list.get(i).get(4));
            if (newTime.compareTo(currentTime) <= 0)
                break;
            i++;
        }
        list.add(i, newEntry);
    }

    public List<List<String>> commandIVRV(String trainID) {
        TrainComposite train = getCompositeByTrainID(trainID);
        if (train == null) {
            Logs.e("Vlak s oznakom " + trainID + " nije pronađen");
            return Collections.emptyList();
        }
        return train.commandIVRV();
    }

    Station getStationByName(String stationName) {
        for (TrainComposite train : this.children) {
            for (TrainTrackStageComposite stage : train.getChildren()) {
                for (StationLeaf station : stage.children) {
                    if (station.getStation().name().equals(stationName)) {
                        return station.getStation();
                    }
                }
            }
        }
        return null;
    }

    List<TrainComposite> hasStations(List<String> stationNames) {
        List<TrainComposite> trains = new ArrayList<>();
        for (TrainComposite train : this.children) {
            if (train.hasStations(stationNames)) {
                trains.add(train);
            }
        }
        return trains;
    }

    public List<Map<String, String>> commandIVI2S(String startStation, String endStation, Weekday weekday,
            ScheduleTime startTime, ScheduleTime endTime, String displayFormat) {
        Station start = getStationByName(startStation);
        if (start == null) {
            Logs.e("Početna stanica " + startStation + " nije pronađena");
            return Collections.emptyList();
        }
        Station end = getStationByName(endStation);
        if (end == null) {
            Logs.e("Završna stanica " + endStation + " nije pronađena");
            return Collections.emptyList();
        }

        List<Map<String, String>> commandIVI2S = new ArrayList<>();
        List<TrainComposite> trainsWithStations = hasStations(List.of(startStation, endStation));
        // now check if the startStation is before the endStation for each train
        List<TrainComposite> finalTrainsWithStations = new ArrayList<>();
        for (TrainComposite train : trainsWithStations) {
            if (train.isStationBefore(startStation, endStation)) {
                finalTrainsWithStations.add(train);
            }
        }
        // list of { TRAINID: { STATION-NAME: DEPARTURE TIME } }
        List<Map<String, Map<String, String>>> trainStations = new ArrayList<>();
        for (TrainComposite train : finalTrainsWithStations) {
            boolean isWeekdayInStages = train.getChildren().stream()
                    .allMatch(stage -> stage.schedule.days().contains(weekday));
            if (!isWeekdayInStages)
                continue;
            boolean trainIsAfterStart = train.startsAfter(startTime);
            boolean trainIsBeforeEnd = train.endsBefore(endTime);
            if (!(trainIsAfterStart && trainIsBeforeEnd))
                continue;
            // get the IVRV output for that train:
            /*
             * IVRV 3050
             * Oznaka vlaka | Oznaka pruge | ?eljezni?ka stanica | Vrijeme polaska | Broj km
             * od polazne ...
             * ------------ | ------------ | ---------------------- | --------------- |
             * ----------------------
             * 3050 | M101 | Zagreb glavni kolodvor | 05:43 | 0.00
             */
            List<List<String>> trainIVRV = train.commandIVRV();
            // now grab a sublist of the trainIVRV from startStation to endStation,
            // including start and end in the final result
            // get the first row whose list item's index 2 is equal to startStation's name,
            // and hte last row whose list item's index 2 is equal to endStation's name
            int startStationIndex = -1;
            int endStationIndex = -1;
            for (int i = 0; i < trainIVRV.size(); i++) {
                if (trainIVRV.get(i).get(2).equals(startStation)) {
                    startStationIndex = i;
                }
                if (trainIVRV.get(i).get(2).equals(endStation)) {
                    endStationIndex = i;
                }
            }
            // now we have the start and end station indexes, we can get the sublist
            if (startStationIndex == -1 || endStationIndex == -1) {
                Logs.e("Početna ili završna stanica nije pronađena u IVRV izlazu");
                continue;
            }
            trainIVRV = trainIVRV.subList(startStationIndex, endStationIndex + 1);
            // now we can convert the trainIVRV to a map of stations and their departure
            // times for that train
            for (List<String> ivrvRowData : trainIVRV) {
                String stationName = ivrvRowData.get(2);
                String departureTime = ivrvRowData.get(3);
                String trackID = ivrvRowData.get(1);
                Map<String, String> stationDeparture = Map.of(stationName, departureTime);
                trainStations.add(Map.of(train.trainID, stationDeparture));
                String distanceFromStartStation = ivrvRowData.get(4);
                if (commandIVI2S.isEmpty()) {
                    commandIVI2S.add(new HashMap<>());
                    commandIVI2S.get(0).putAll(Map.of("S", stationName, "P", trackID, "K", distanceFromStartStation,
                            "V:" + train.trainID, departureTime));
                } else {
                    boolean found = false;
                    for (Map<String, String> row : commandIVI2S) {
                        if (row.get("P").equals(trackID) && row.get("S").equals(stationName)) {
                            Map<String, String> newRow = new HashMap<>(row);
                            newRow.put("V:" + train.trainID, departureTime);
                            commandIVI2S.set(commandIVI2S.indexOf(row), newRow);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        commandIVI2S.add(Map.of("S", stationName, "P", trackID, "K", distanceFromStartStation,
                                "V:" + train.trainID, departureTime));
                    }
                }
            }
        }

        return commandIVI2S;
    }

}
