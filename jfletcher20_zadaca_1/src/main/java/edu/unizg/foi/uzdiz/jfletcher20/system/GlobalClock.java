package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.ClockState;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;

public abstract class GlobalClock {
    private static int time = 0;
    private static ClockState state = ClockState.STOPPED;
    private static List<TrainComposite> trains = new ArrayList<TrainComposite>();

    public static ScheduleTime getTime() {
        return new ScheduleTime(time);
    }

    public static int getTimeAsInt() {
        return time;
    }

    public static void incrementTime() {
        time++;
    }

    public static void resetTime() {
        time = 0;
    }

    public static void setTime(int time) {
        GlobalClock.time = time;
    }

    public static void setTime(ScheduleTime time) {
        if (GlobalClock.isSimulating())
            trainSimulation(GlobalClock.getTime());
        GlobalClock.time = time.getTotalTimeInMinutes();
    }

    public static void setTime(String time) {
        GlobalClock.time = new ScheduleTime(time).getTotalTimeInMinutes();
    }

    public static void setTime(int hours, int minutes) {
        GlobalClock.time = new ScheduleTime(hours, minutes).getTotalTimeInMinutes();
    }

    public static ScheduleTime addTime(int minutes) {
        GlobalClock.time += minutes;
        return new ScheduleTime(time);
    }

    public static ScheduleTime addTime(ScheduleTime time) {
        GlobalClock.time += time.getTotalTimeInMinutes();
        return new ScheduleTime(GlobalClock.time);
    }

    public static void setState(ClockState state) {
        GlobalClock.state = state;
    }

    public static ClockState getState() {
        return GlobalClock.state;
    }

    public static boolean isSimulating() {
        return GlobalClock.state == ClockState.SIMULATING;
    }

    public static boolean isStopped() {
        return GlobalClock.state == ClockState.STOPPED;
    }

    public static boolean isPaused() {
        return GlobalClock.state == ClockState.PAUSED;
    }

    public static void simulate(TrainComposite train, Weekday day, ScheduleTime startTime, int coefficient) {
        ScheduleTime currentTime = startTime;
        GlobalClock.setTime(currentTime);
        GlobalClock.setState(ClockState.SIMULATING);
        Logs.s(currentTime, "Vlak " + train.trainID + " počinje s radom.");
        while (GlobalClock.isSimulating()) {
            try {
                if (System.in.available() > 0) {
                    String input = System.console().readLine();
                    if ("X".equalsIgnoreCase(input.trim())) {
                        Logs.s(currentTime, "Simulacija vlaka " + train.trainID + " je prekinuta.");
                        GlobalClock.setState(ClockState.STOPPED);
                        break;
                    }
                }
            } catch (IOException e) {
            }
            if (!GlobalClock.trains.contains(train))
                GlobalClock.trains.add(train);
            currentTime = currentTime.addMinutes(1);
            GlobalClock.setTime(currentTime);

            try {
                Thread.sleep(1000 * 60 / coefficient);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void trainSimulation(ScheduleTime time) {
        List<TrainComposite> trainsToRemove = new ArrayList<TrainComposite>();
        for (TrainComposite train : trains) {
            boolean arrivedAtStation = train.isCurrentlyAtStation(GlobalClock.getTime());
            if (arrivedAtStation) {
                Station currentStation = train.getCurrentStation(GlobalClock.getTime());
                Logs.s(GlobalClock.getTime(),
                        "Vlak " + train.trainID + " je na " + train.getTypeOfStation(currentStation) + " "
                                + currentStation.name());
                train.notifyObservers(currentStation.name());
            }

            if (train.hasReachedDestination(GlobalClock.getTime())) {
                Logs.s(GlobalClock.getTime(), "Vlak " + train.trainID + " je stigao na odredište.");
                trainsToRemove.add(train);
            }
        }
        for (TrainComposite train : trainsToRemove)
            trains.remove(train);
        if (trains.isEmpty()) {
            GlobalClock.setState(ClockState.STOPPED);
        }
    }

}
