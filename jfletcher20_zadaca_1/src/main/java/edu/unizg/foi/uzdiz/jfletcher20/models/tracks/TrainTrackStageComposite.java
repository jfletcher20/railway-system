package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationLeaf;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

public class TrainTrackStageComposite implements IComposite {

    public List<StationLeaf> children = new ArrayList<StationLeaf>();

    public TrainTrackStageComposite(Station start, Station end, TraversalDirection direction) {
        TrainTrack track = start.getTrack();
        List<Station> stations = RailwaySingleton.getInstance().getStationsOnTrack(track.id());
        if (direction == TraversalDirection.FORTH) {
            for (Station station : stations) {
                if (station.equals(start)) {
                    this.Add(new StationLeaf(station));
                } else if (station.equals(end)) {
                    this.Add(new StationLeaf(station));
                    break;
                }
            }
        } else {
            for (int i = stations.size() - 1; i >= 0; i--) {
                Station station = stations.get(i);
                if (station.equals(start)) {
                    this.Add(new StationLeaf(station));
                } else if (station.equals(end)) {
                    this.Add(new StationLeaf(station));
                    break;
                }
            }
        }
    }

    @Override
    public void Operation() {
        Logs.i("Operation() called on TrainTrackStageComposite");
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
