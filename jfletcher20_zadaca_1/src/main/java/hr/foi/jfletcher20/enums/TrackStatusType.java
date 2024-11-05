package hr.foi.jfletcher20.enums;

public enum TrackStatusType {
  FUNCTIONAL, // ispravna
  DAMAGED, // u kvaru
  CLOSED; // zatvorena

  public static TrackStatusType fromCSV(String value) {
    switch (value) {
      case "I":
        return FUNCTIONAL;
      case "U":
        return DAMAGED;
      case "Z":
        return CLOSED;
      default:
        throw new IllegalArgumentException("Error: Nepoznat status pruge: " + value);
    }
  }
}
