package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.Schedule;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationLeaf;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

public class TrainTrackStageComposite implements IComposite {

    public List<StationLeaf> children = new ArrayList<StationLeaf>();
    public String trackID;

    public TrainTrackStageComposite(Station start, Station end, TraversalDirection direction, String trackID) {
        this.trackID = trackID;
        List<Station> stations = RailwaySingleton.getInstance().getStationsOnTrack(trackID);
        if (direction == TraversalDirection.FORTH) {
            for (int i = stations.indexOf(start) + 1; i < stations.size(); i++) {
                Station station = stations.get(i);
                this.Add(new StationLeaf(station));
                if (station.equals(end)) {
                    break;
                }
            }
        } else {
            for (int i = stations.indexOf(start) - 1; i >= 0; i--) {
                Station station = stations.get(i);
                this.Add(new StationLeaf(station));
                if (station.equals(end)) {
                    break;
                }
            }
        }
    }

    public TrainTrackStageComposite(Schedule schedule) {
        this(schedule.departure(), schedule.destination(), schedule.direction(), schedule.trackID());
    }

    @Override
    public void Operation() {
        Logs.i("\t\tstage");
        for (StationLeaf child : this.children) {
            child.Operation();
        }
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
        return this.children.add((StationLeaf) component) ? 1 : 0;
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

}
