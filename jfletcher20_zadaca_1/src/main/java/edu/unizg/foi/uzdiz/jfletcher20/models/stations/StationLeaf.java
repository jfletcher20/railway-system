package edu.unizg.foi.uzdiz.jfletcher20.models.stations;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.Leaf;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class StationLeaf extends Leaf {
    public Station station;

    public StationLeaf(Station station) {
        this.station = station;
    }

    @Override
    public void Operation() {
        Logs.o("\t\t\t\t" + this.station.name(), false);
    }

    public Station getStation() {
        return this.station;
    }

    @Override
    public String toString() {
        return this.station.name();
    }
	
}
