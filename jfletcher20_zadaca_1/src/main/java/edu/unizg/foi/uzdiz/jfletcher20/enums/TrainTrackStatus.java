package edu.unizg.foi.uzdiz.jfletcher20.enums;

public enum TrainTrackStatus {
  FUNCTIONAL, // ispravna
  DAMAGED, // u kvaru
  CLOSED; // zatvorena

  public static TrainTrackStatus fromCSV(String value) {
    switch (value) {
      case "I":
        return FUNCTIONAL;
      case "U":
        return DAMAGED;
      case "Z":
        return CLOSED;
      default:
        throw new IllegalArgumentException("Nepoznat status pruge: " + value);
    }
  }
}
