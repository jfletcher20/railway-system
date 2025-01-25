package edu.unizg.foi.uzdiz.jfletcher20.models.clock_states;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.ClockState;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IClockState;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.GlobalClock;

public class ClockStateSimulating extends IClockState {
    public ClockStateSimulating() {
        internalState = ClockState.SIMULATING;
    }

    @Override
    public void simulate(TrainComposite train, Weekday day, ScheduleTime startTime, int coefficient) {
        while (GlobalClock.isSimulating()) {
            try {
                if (System.in.available() > 0) {
                    String input = System.console().readLine();
                    if ("X".equalsIgnoreCase(input.trim())) {
                        Logs.s("Simulacija vlaka " + train.trainID
                                + " je prekinuta. Nadajmo se da se naši putnici ne žure na ispit.");
                        GlobalClock.setState(ClockState.STOPPED);
                        break;
                    }
                }
            } catch (IOException e) {
            }
            if (!GlobalClock.trainsSimulating.contains(train))
                GlobalClock.trainsSimulating.add(train);
            setTime(GlobalClock.getTime().addMinutes(1));

            try {
                Thread.sleep(1000 * 60 / coefficient);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public String currentTime() {
        return // unicode for color green
        "\u001B[32m" + GlobalClock.getTime().toString() + // unicode for color reset
                "\u001B[0m";
    }

    @Override
    public void setTime(ScheduleTime time) {
        trainSimulation(GlobalClock.getTime());
        super.setTime(time);
    }

    private static void trainSimulation(ScheduleTime time) {
        List<TrainComposite> trainsToRemove = new ArrayList<TrainComposite>();
        for (TrainComposite train : GlobalClock.trainsSimulating) {
            boolean arrivedAtStation = train.isCurrentlyAtStation(GlobalClock.getTime());
            if (arrivedAtStation) {
                Station currentStation = train.getCurrentStation(GlobalClock.getTime());
                Logs.s(
                        "Vlak " + train.trainID + " je na " + train.getTypeOfStation(currentStation) + " "
                                + currentStation.name());
                train.notifyObservers(currentStation.name());
            }

            if (train.hasReachedDestination(GlobalClock.getTime())) {
                Logs.s("Vlak " + train.trainID + " je stiglo na odredište.");
                trainsToRemove.add(train);
            }
        }
        for (TrainComposite train : trainsToRemove)
            GlobalClock.trainsSimulating.remove(train);
        if (GlobalClock.trainsSimulating.isEmpty()) {
            GlobalClock.setState(ClockState.STOPPED);
        }
    }

}
