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
        } else if (data.split(";").length != columnCount && data.split(";").length != 1) {
            Logs.i("ScheduleDaysCreator encountered error: " + columnCountError(data.split(";").length));
            Logs.w(row, columnCountError(data.split(";").length));
            return null;
        }

        String[] parts = data.split(";");
        return new ScheduleDays(
                parts[0], // dayID
                Weekday.daysFromString(parts.length == 1 ? "" : parts[1]) // days
        );
    }

    private String columnCountError(int counts) {
        return "ScheduleDaysCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
    }

}
