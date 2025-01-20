package edu.unizg.foi.uzdiz.jfletcher20.interfaces;

import edu.unizg.foi.uzdiz.jfletcher20.enums.ClockState;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.GlobalClock;

public abstract class IClockState {
    public ClockState internalState;
    public abstract void simulate(TrainComposite train, Weekday day, ScheduleTime startTime, int coefficient);
    public abstract String currentTime();
    public boolean isSimulating() {
        return internalState == ClockState.SIMULATING;
    }
    public boolean isStopped() {
        return internalState == ClockState.STOPPED;
    }
    public boolean isPaused() {
        return internalState == ClockState.PAUSED;
    }
    public void setTime(ScheduleTime time) {
        GlobalClock.setTime(time.getTotalTimeInMinutes());
    }
}
