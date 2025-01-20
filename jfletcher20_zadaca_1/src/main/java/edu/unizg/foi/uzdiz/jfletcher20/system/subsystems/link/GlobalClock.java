package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link;

import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.ClockState;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IClockState;
import edu.unizg.foi.uzdiz.jfletcher20.models.clock_states.ClockStatePaused;
import edu.unizg.foi.uzdiz.jfletcher20.models.clock_states.ClockStateSimulating;
import edu.unizg.foi.uzdiz.jfletcher20.models.clock_states.ClockStateStopped;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public abstract class GlobalClock {
    public static int time = 0;
    private static IClockState state;
    private static List<IClockState> states = new ArrayList<IClockState>();
    public static List<TrainComposite> trainsSimulating = new ArrayList<TrainComposite>();

    public static void initStates() {
        states.add(new ClockStateSimulating());
        states.add(new ClockStatePaused());
        states.add(new ClockStateStopped());
        state = states.get(0);
    }

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
        GlobalClock.state = states.stream().filter(s -> s.internalState == state).findFirst().get();
    }

    public static ClockState getState() {
        return state.internalState;
    }

    public static boolean isSimulating() {
        return GlobalClock.state.isSimulating();
    }

    public static boolean isStopped() {
        return GlobalClock.state.isStopped();
    }

    public static boolean isPaused() {
        return GlobalClock.state.isPaused();
    }

    public static void simulate(TrainComposite train, Weekday day, ScheduleTime startTime, int coefficient) {
        
        ScheduleTime currentTime = startTime;
        GlobalClock.setTime(currentTime);
        GlobalClock.setState(ClockState.SIMULATING);
        Logs.s("Vlak " + train.trainID + " počinje s radom.");
        
        state.simulate(train, day, startTime, coefficient);
    }

    public static String getTimeOutputBasedOnState() {
        return state.currentTime();
    }

}
