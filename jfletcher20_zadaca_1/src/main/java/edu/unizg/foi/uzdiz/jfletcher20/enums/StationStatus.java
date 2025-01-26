package edu.unizg.foi.uzdiz.jfletcher20.enums;

import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

// ispravna I, zatvorena Z, testiranje T i u kvaru K
public enum StationStatus {
    FUNCTIONAL, CLOSED, TESTING, FAULTY;

    public static StationStatus fromCSV(String value) {
        switch (value) {
            case "I":
                return FUNCTIONAL;
            case "Z":
                return CLOSED;
            case "T":
                return TESTING;
            case "K":
                return FAULTY;
            default:
                Logs.e("Error: Nepoznat status stanice" + value);
                break;
        }
        return null;
    }
}
