package edu.unizg.foi.uzdiz.jfletcher20.models.stations;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.Leaf;

public class StationLeaf extends Leaf {
    private Station station;

    public StationLeaf(Station station) {
        this.station = station;
    }

    @Override
    public void Operation() {
        System.out.println("\t\t\t\t" + this.station.name());
    }
	
}
