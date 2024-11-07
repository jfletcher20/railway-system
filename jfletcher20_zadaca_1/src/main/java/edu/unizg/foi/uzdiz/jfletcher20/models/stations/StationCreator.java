package edu.unizg.foi.uzdiz.jfletcher20.models.stations;

import edu.unizg.foi.uzdiz.jfletcher20.enums.StationActivity;
import edu.unizg.foi.uzdiz.jfletcher20.enums.StationType;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;

public class StationCreator implements ICreator {

  private static int columnCount = 14;

  public StationCreator() {}

  @Override
  public Station factoryMethod(String data, int row) {
    if (data == null || data.isEmpty()) {
      Logs.e(row, "StationCreator Prazan redak");
      return null;
    } else if (data.split(";").length != columnCount) {
      Logs.e(row, columnCountError(data.split(";").length));
      return null;
    }
    String[] parts = data.split(";");
    return new Station(parts[0], // Stanica
        StationType.fromCSV(parts[2]), // Vrsta stanice
        StationActivity.fromCSV(parts[4], parts[5]), // Aktivnosti na stanici
        ParsingUtil.i(parts[7]), // Broj perona
        parts[3] // Status stanice
    );
  }

  private String columnCountError(int counts) {
    return "StationCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
  }

}
