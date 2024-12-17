package edu.unizg.foi.uzdiz.jfletcher20.models.schedule_days;

import java.util.Set;

import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;

// implements IProduct
// is a record
// has two variables - dayID and Weekday

/**
 * Record for handling the days of the week determined from dayCode in the
 * schedule.
 */
public record ScheduleDays(
        String dayID, // Oznaka dana
        Set<Weekday> days // Dan
) implements IProduct {
    public ScheduleDays(
            String dayID, // Oznaka dana
            Set<Weekday> days // Dan
    ) {
        if (dayID == null || dayID.isEmpty())
            throw new IllegalArgumentException("Oznaka dana je obavezna.");
        this.dayID = dayID;
        this.days = days;
    }
}
