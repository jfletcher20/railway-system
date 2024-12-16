package edu.unizg.foi.uzdiz.jfletcher20.models.schedule_days;

import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class ScheduleDaysCreator implements ICreator {

    private static int columnCount = 2;

    public ScheduleDaysCreator() {
    }

    @Override
    public ScheduleDays factoryMethod(String data, int row) {
        if (data == null || data.isEmpty()) {
            Logs.w(row, "ScheduleDaysCreator Prazan redak.");
            return null;
        } else if (data.split(";").length != columnCount) {
            Logs.w(row, columnCountError(data.split(";").length));
            return null;
        }

        String[] parts = data.split(";");
        return new ScheduleDays(
                parts[0], // dayID
                Weekday.daysFromString(parts[1]) // day
        );
    }

    private String columnCountError(int counts) {
        return "ScheduleDaysCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
    }

}

/*
 * 
 * 
 * 
 * package edu.unizg.foi.uzdiz.jfletcher20.models.schedule_days;
 * 
 * import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
 * import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
 * 
 * // implements IProduct
 * // is a record
 * // has two variables - dayID and Weekday
 * 
 * /**
 * Record for handling the days of the week determined from dayCode in the
 * schedule.
 * public record ScheduleDays(
 * String dayID, // Oznaka dana
 * Weekday day // Dan
 * ) implements IProduct {
 * 
 * public ScheduleDays(
 * String dayID, // Oznaka dana
 * Weekday day // Dan
 * ) {
 * if (dayID == null || dayID.isEmpty())
 * throw new IllegalArgumentException("Oznaka dana je obavezna.");
 * this.dayID = dayID;
 * this.day = day;
 * }
 * }
 * 
 * 
 * 
 */