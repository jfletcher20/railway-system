package edu.unizg.foi.uzdiz.jfletcher20.enums;

public enum TrainTrackCategory {
  LOCAL, REGIONAL, INTERNATIONAL;

  public static TrainTrackCategory fromCSV(String value) {
    switch (value) {
      case "L":
        return LOCAL;
      case "R":
        return REGIONAL;
      case "M":
        return INTERNATIONAL;
      default:
        System.out.println("Error: Nepoznata vrijednost TrainTrackCategory: " + value);
        return null;
    }
  }
}
