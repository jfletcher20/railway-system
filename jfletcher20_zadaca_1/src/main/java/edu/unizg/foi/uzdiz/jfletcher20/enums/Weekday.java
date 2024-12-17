package edu.unizg.foi.uzdiz.jfletcher20.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Enum for handling operations with the days of the week determined from dayCode in the schedule.
 */
public enum Weekday {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, ALL;

    private static Pattern mondayIsAnywhere = Pattern.compile(".*Po.*");
    private static Pattern tuesdayIsAnywhere = Pattern.compile(".*U.*");
    private static Pattern wednesdayIsAnywhere = Pattern.compile(".*Sr.*");
    private static Pattern thursdayIsAnywhere = Pattern.compile(".*Č.*");
    private static Pattern fridayIsAnywhere = Pattern.compile(".*Pe.*");
    private static Pattern saturdayIsAnywhere = Pattern.compile(".*Su.*");
    private static Pattern sundayIsAnywhere = Pattern.compile(".*N.*");

    public static Weekday dayFromString(String value) {
        switch (value) {
            case "Po":
                return MONDAY;
            case "U":
                return TUESDAY;
            case "Sr":
                return WEDNESDAY;
            case "Č":
                return THURSDAY;
            case "Pe":
                return FRIDAY;
            case "Su":
                return SATURDAY;
            case "N":
                return SUNDAY;
            case "":
                return ALL;
            default:
                throw new IllegalArgumentException("Nepoznata oznaka dana: " + value);
        }
    }

    // causes an error if the value is "12;" because of second column being empty/null
    public static Set<Weekday> daysFromString(String value) {
        if (value == null || value.trim().isEmpty())
            return Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
        Set<Weekday> days = new java.util.HashSet<>();
        if (mondayIsAnywhere.matcher(value).matches())
            days.add(MONDAY);
        if (tuesdayIsAnywhere.matcher(value).matches())
            days.add(TUESDAY);
        if (wednesdayIsAnywhere.matcher(value).matches())
            days.add(WEDNESDAY);
        if (thursdayIsAnywhere.matcher(value).matches())
            days.add(THURSDAY);
        if (fridayIsAnywhere.matcher(value).matches())
            days.add(FRIDAY);
        if (saturdayIsAnywhere.matcher(value).matches())
            days.add(SATURDAY);
        if (sundayIsAnywhere.matcher(value).matches())
            days.add(SUNDAY);
        if (value.equals(""))
            days.addAll(Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY));
        return days;
    }

    public List<Weekday> days() {
        if (this == ALL) {
            List<Weekday> days = new ArrayList<>();
            days.add(MONDAY);
            days.add(TUESDAY);
            days.add(WEDNESDAY);
            days.add(THURSDAY);
            days.add(FRIDAY);
            days.add(SATURDAY);
            days.add(SUNDAY);
            return days;
        } else {
            List<Weekday> days = new ArrayList<>();
            days.add(this);
            return days;
        }
    }

    public static String listToString(Set<Weekday> days) {
        StringBuilder dayString = new StringBuilder();
        for (Weekday day : days) 
            dayString.append(day.toShorthand());
        return dayString.toString().trim();
    }

    public String toShorthand() {
        return switch (this) {
            case MONDAY -> "Po";
            case TUESDAY -> "U";
            case WEDNESDAY -> "Sr";
            case THURSDAY -> "Č";
            case FRIDAY -> "Pe";
            case SATURDAY -> "Su";
            case SUNDAY -> "N";
            case ALL -> "";
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case MONDAY -> "Ponedjeljak";
            case TUESDAY -> "Utorak";
            case WEDNESDAY -> "Srijeda";
            case THURSDAY -> "Četvrtak";
            case FRIDAY -> "Petak";
            case SATURDAY -> "Subota";
            case SUNDAY -> "Nedjelja";
            case ALL -> "Svi dani";
        };
    }

}
