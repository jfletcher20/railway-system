package edu.unizg.foi.uzdiz.jfletcher20.models.schedule;

public record ScheduleTime(
        int hours,
        int minutes) {
    public ScheduleTime(int hours, int minutes) {
        // if minutes go over 60, add to the next hour
        if (minutes >= 60) {
            hours += minutes / 60;
            minutes %= 60;
        }
        // if hours go over 24, add to the next day
        if (hours >= 24) {
            hours %= 24;
        }
        this.hours = hours;
        this.minutes = minutes;
    }

    public ScheduleTime(String time, boolean throwsException) {
        this(
                Integer.parseInt(time.split(":")[0]),
                Integer.parseInt(time.split(":")[1]));
        if (throwsException) {
            if (time == null || time.isEmpty() || time.split(":").length != 2)
                throw new IllegalArgumentException("vrijeme mora biti u formatu HH:mm.");
        }
    }

    public ScheduleTime(int minutes) {
        this(minutes / 60, minutes % 60);
    }

    public int compareTo(ScheduleTime time) {
        if (this.hours == time.hours)
            return Integer.compare(this.minutes, time.minutes);
        return Integer.compare(this.hours, time.hours);
    }

    public boolean isBefore(ScheduleTime time) {
        return this.compareTo(time) < 0;
    }

    public boolean isAfter(ScheduleTime time) {
        return this.compareTo(time) > 0;
    }

    public boolean isBetween(ScheduleTime time1, ScheduleTime time2) {
        return this.isAfter(time1) && this.isBefore(time2);
    }

    public boolean isBetweenOrEqual(ScheduleTime time1, ScheduleTime time2) {
        return this.isAfter(time1) && this.isBefore(time2) || this.equals(time1) || this.equals(time2);
    }

    public ScheduleTime addMinutes(int minutes) {
        int newMinutes = this.minutes + minutes;
        int newHours = this.hours + newMinutes / 60;
        newMinutes %= 60;
        return new ScheduleTime(newHours, newMinutes);
    }

    public ScheduleTime subtractMinutes(int minutes) {
        int newMinutes = this.minutes - minutes;
        int newHours = this.hours - newMinutes / 60;
        newMinutes %= 60;
        return new ScheduleTime(newHours, newMinutes);
    }

    public ScheduleTime addHours(int hours) {
        int newHours = this.hours + hours;
        return new ScheduleTime(newHours, this.minutes);
    }

    public ScheduleTime subtractHours(int hours) {
        int newHours = this.hours - hours;
        return new ScheduleTime(newHours, this.minutes);
    }

    public int getTotalTimeInMinutes() {
        return this.hours * 60 + this.minutes;
    }

    public ScheduleTime addTime(ScheduleTime time) {
        return new ScheduleTime(this.hours + time.hours, this.minutes + time.minutes);
    }

    public ScheduleTime subtractTime(ScheduleTime time) {
        return new ScheduleTime(this.hours - time.hours, this.minutes - time.minutes);
    }

    public boolean equals(ScheduleTime time) {
        return this.hours == time.hours && this.minutes == time.minutes;
    }

    public int totalTimeBetweenTimesInMinutes(ScheduleTime a, ScheduleTime b) {
        return Math.abs(a.getTotalTimeInMinutes() - b.getTotalTimeInMinutes());
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d", hours, minutes);
    }

}
