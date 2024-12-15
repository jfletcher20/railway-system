package edu.unizg.foi.uzdiz.jfletcher20.enums;

/**
 * Enum for managing the traversal direction of a train on a track.
 */
public enum TraversalDirection {
    FORTH, REVERSE;

    public static TraversalDirection fromString(String direction) {
        if (direction.equalsIgnoreCase("N")) {
            return FORTH;
        } else if (direction.equalsIgnoreCase("O")) {
            return REVERSE;
        } else
            throw new IllegalArgumentException("Nije validan smjer " + direction);
    }

    public static String toString(TraversalDirection direction) {
        if (direction == FORTH) {
            return "N";
        } else if (direction == REVERSE) {
            return "O";
        } else
            throw new IllegalArgumentException("Nije validan smjer " + direction);
    }

    @Override
    public String toString() {
        return switch (this) {
            case FORTH -> "N";
            case REVERSE -> "O";
        };
    }
}
