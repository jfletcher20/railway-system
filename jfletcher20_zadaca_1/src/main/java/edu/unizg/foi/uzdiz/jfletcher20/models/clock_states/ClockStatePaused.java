package edu.unizg.foi.uzdiz.jfletcher20.models.clock_states;

import edu.unizg.foi.uzdiz.jfletcher20.enums.ClockState;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IClockState;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.link.GlobalClock;

public class ClockStatePaused extends IClockState {
    public ClockStatePaused() {
        internalState = ClockState.PAUSED;
    }

    @Override
    public void simulate(TrainComposite train, Weekday day, ScheduleTime startTime, int coefficient) {
        return;
    }

    @Override
    public String currentTime() {
        return GlobalClock.getTime().toString();
    }
    
}
