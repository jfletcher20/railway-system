package edu.unizg.foi.uzdiz.jfletcher20.models.schedule;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.railway.RailwaySingleton;

public class ScheduleCreator implements ICreator {

    private static int columnCount = 9;

    public ScheduleCreator() {
    }

    @Override
    public Schedule factoryMethod(String data, int row) {
        if (data == null || data.isEmpty()) {
            Logs.w(row, "ScheduleCreator Prazan redak.");
            return null;
        } else if (data.split(";", -1).length != columnCount) {
            Logs.w(row, columnCountError(data.split(";", -1).length));
            return null;
        }

        String[] parts = data.split(";", -1);
        return new Schedule(
                parts[0], // trackID
                TraversalDirection.fromString(parts[1]), // direction
                parts[2], // departure
                parts[3], // destination
                parts[4], // scheduledTrainID
                TrainType.fromString(parts[5]), // trainType
                new ScheduleTime(parts[6], true), // departureTime
                new ScheduleTime(parts[7], false), // travelTime
                RailwaySingleton.getInstance().getScheduleDays(parts[8]).days() // days
        );
    }

    private String columnCountError(int counts) {
        return "ScheduleCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
    }

}