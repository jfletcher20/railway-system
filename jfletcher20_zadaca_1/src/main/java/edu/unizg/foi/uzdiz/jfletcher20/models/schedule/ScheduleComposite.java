package edu.unizg.foi.uzdiz.jfletcher20.models.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackStageComposite;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

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
        List<List<String>> commandIEVD = new ArrayList<List<String>>();
        if (days == null || days.isEmpty()) {
            Logs.e("Nisu pronađeni dani vožnje");
            return Collections.emptyList();
        }
        for (TrainComposite child : this.children) {
            // for (TrainTrackStageComposite stage : child.children) {
            // boolean daysAreInStage = stage.schedule.days().containsAll(days);
            // if (daysAreInStage) {
            // List<String> output = new ArrayList<String>();
            // output.add(0, child.trainID);
            // output.addAll(stage.commandIEV());
            // commandIEVD.add(output);
            // }
            // }
            // if all of a train composite's stage's schedules contain all of the days, add
            // to the commandIEVD output
            List<Schedule> schedules = new ArrayList<Schedule>();
            for (TrainTrackStageComposite stage : child.getChildren()) {
                schedules.add(stage.schedule);
            }
            boolean daysAreInSchedules = schedules.stream().anyMatch(s -> s.days().containsAll(days));
            if (daysAreInSchedules) {
                commandIEVD.addAll(child.commandIEVD(days));
            }
        }
        return commandIEVD;
    }

    public List<List<String>> commandIVRV(String trainID) {
        TrainComposite train = getCompositeByTrainID(trainID);
        if (train == null) {
            Logs.e("Vlak s oznakom " + trainID + " nije pronađen");
            return Collections.emptyList();
        }
        return train.commandIVRV();
    }

}
