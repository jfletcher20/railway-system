package edu.unizg.foi.uzdiz.jfletcher20.enums;

/**
 * Enum for managing the type of train defined in the schedule.
 */
public enum TrainType {
    NORMAL, FAST, EXPRESS;

    public static TrainType fromString(String type) {
        return switch (type) {
            case "" -> NORMAL; // Normalni
            case "U" -> FAST; // Ubrzani
            case "B" -> EXPRESS; // Brzi
            default -> throw new IllegalArgumentException("Nepoznata vrsta vlaka: " + type);
        };
    }

    public static String toString(TrainType type) {
        return switch (type) {
            case NORMAL -> "";
            case FAST -> "U";
            case EXPRESS -> "B";
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case NORMAL -> "";
            case FAST -> "U";
            case EXPRESS -> "B";
        };
    }

    public String displayName() {
        return switch (this) {
            case NORMAL -> "Normalni";
            case FAST -> "Ubrzani";
            case EXPRESS -> "Brzi";
        };
    }
}
