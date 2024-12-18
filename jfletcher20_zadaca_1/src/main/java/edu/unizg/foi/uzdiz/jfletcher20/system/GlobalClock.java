package edu.unizg.foi.uzdiz.jfletcher20.system;

import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;

public abstract class GlobalClock {
    private static int time = 0;

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
    
}
