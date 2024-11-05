package hr.foi.jfletcher20.enums;

/**
 * Enum representing the type of a station.
 * 
 * @param STATION Station (kolodvor)
 * @param HALT Halt (stajalište)
 */
public enum StationType {
  STATION, HALT;
  
  public static StationType fromCSV(String value) {
    switch (value) {
      case "kol.":
        return STATION;
      case "staj.":
        return HALT;
      default:
        System.out.println("Error: Nepoznat tip stajališta: " + value);
        return null;
    }
  }
}
