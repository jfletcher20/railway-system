package edu.unizg.foi.uzdiz.jfletcher20.models.stations;

import edu.unizg.foi.uzdiz.jfletcher20.enums.StationActivity;
import edu.unizg.foi.uzdiz.jfletcher20.enums.StationType;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;

public class StationCreator implements ICreator {

  private static int columnCount = 17;

  public StationCreator() {
  }

  @Override
  public Station factoryMethod(String data, int row) {
    if (data == null || data.isEmpty()) {
      Logs.w(row, "StationCreator Prazan redak");
      return null;
    } else if (data.split(";", -1).length != columnCount) {
      Logs.w(row, columnCountError(data.split(";", -1).length));
      return null;
    }
    String[] parts = data.split(";", -1);
    return new Station(parts[0], // Stanica
        StationType.fromCSV(parts[2]), // Vrsta stanice
        StationActivity.fromCSV(parts[4], parts[5]), // Aktivnosti na stanici
        ParsingUtil.i(parts[7]), // Broj perona
        parts[3], // Status stanice
        !parts[14].isBlank() ? ParsingUtil.i(parts[14]) : -1, // Vrijeme normalni vlak
        !parts[15].isBlank() ? ParsingUtil.i(parts[15]) : -1, // Vrijeme ubrzani vlak
        !parts[16].isBlank() ? ParsingUtil.i(parts[16]) : -1 // Vrijeme brzi vlak
    );
  }

  private String columnCountError(int counts) {
    return "StationCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
  }

}
