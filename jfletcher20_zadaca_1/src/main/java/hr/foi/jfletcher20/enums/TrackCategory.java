package hr.foi.jfletcher20.enums;

public enum TrackCategory {
  LOCAL, REGIONAL, INTERNATIONAL;

  public static TrackCategory fromCSV(String value) {
    switch (value) {
      case "L":
        return LOCAL;
      case "R":
        return REGIONAL;
      case "M":
        return INTERNATIONAL;
      default:
        System.out.println("Error: Nepoznata vrijednost TrackCategory: " + value);
        return null;
    }
  }
}
