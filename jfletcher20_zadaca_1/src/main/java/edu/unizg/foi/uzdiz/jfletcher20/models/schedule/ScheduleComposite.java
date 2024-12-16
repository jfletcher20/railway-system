package edu.unizg.foi.uzdiz.jfletcher20.models.schedule;

import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

/**
 * Composite class for the Schedule
 * 
 * Schedule base structure is:
 *      ScheduleComposite
 *       |_ TrainComposite
 *       |   |_ TrainTrackStageComposite
 *       |   |   |_ StationLeaf
 *       |   |   |_ StationLeaf
 *       |   |_ TrainTrackStageComposite
 *       |       |_ StationLeaf
 *       |       |_ StationLeaf
 *       |       |_ StationLeaf
 *       |_ TrainComposite
 *           |_ TrainTrackStageComposite
 *               |_ StationLeaf
 *               |_ StationLeaf
 */
public class ScheduleComposite implements IComposite {
    List<TrainComposite> children = new ArrayList<TrainComposite>();

    public void Operation() {
        System.out.println("Operation() called on ScheduleComposite");
    }

    @Override
    public int Add(IComponent component) {
        if (!(component instanceof TrainComposite)) {
            Logs.e("Pokušaj dodavanja pogrešnog tipa komponente u ScheduleComposite::Add(): "
                    + component.getClass().getName());
            return 0;
        }
        return this.children.add((TrainComposite) component) ? 1 : 0;
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

}
