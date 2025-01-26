package edu.unizg.foi.uzdiz.jfletcher20.enums;

public enum TrainTrackStatus {
  FUNCTIONAL, // ispravna
  FAULTY, // u kvaru
  TESTING, // u testiranju
  CLOSED; // zatvorena

  public static TrainTrackStatus fromCSV(String value) {
    switch (value) {
      case "I":
        return FUNCTIONAL;
      case "K":
        return FAULTY;
      case "T":
        return TESTING;
      case "Z":
        return CLOSED;
      default:
        throw new IllegalArgumentException("Nepoznat status pruge: " + value);
    }
  }

  @Override
  public String toString() {
    switch (this) {
      case FUNCTIONAL: return "Ispravno";
      case FAULTY: return "U kvaru";
      case TESTING: return "U testiranju";
      case CLOSED: return "Zatvoreno";
      default: return "Nepoznato";
    }
  }
}
