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

/**
 * Station object represents a single train station.
 * 
 * @param name Station name
 * @param type Station type (kolodvor, stajalište)
 * @param activity Passenger activity (ulaz/izlaz putnika), cargo activity (utovar/istovar robe),
 *        both
 * @param platformCount Number of platforms (1-99)
 * @param status Status of the station (otvorena, zatvorena)
 */

/*
 * example CSV file data: --zs [has 14 columns] Stanica;Oznaka pruge;Vrsta stanice;Status
 * stanice;Putnici ul/iz;Roba ut/ist;Kategorija pruge;Broj perona;Vrsta pruge;Broj kolosjeka;DO po
 * osovini;DO po duznom m;Status pruge;Dužina Kotoriba;M501;kol.;O;DA;DA;M;1;K;1;22,5;8,0;I;0 Donji
 * Mihaljevec;M501;staj.;O;DA;NE;M;1;K;1;22,5;8,0;I;7 Donji
 * Kraljevec;M501;kol.;O;DA;DA;M;1;K;1;22,5;8,0;I;6
 */

/*
 * Željeznička stanica je točka (u stvarnosti je objekt s pripadajućom infrastrukturom) na
 * željezničkoj pruzi na kojoj se mogu zaustaviti željeznička prijevozna sredstva za prijevoz
 * putnika za ulaz i izlaz putnika i željeznička prijevozna sredstva za prijevoz robe za utovar i
 * istovar robe.
 * 
 * Jedna željeznička stanica može se nalaziti na jednoj ili više željezničkih pruga.
 * 
 * To znači da željezničko prijevozno sredstvo može putovati između željezničkih stanica koje se
 * nalaze na jednoj, dvije ili više željezničkih pruga. Željeznička stanica ima sljedeće atribute:
 * vrsta (kolodvor, stajalište), aktivnosti (ulaz/izlaz putnika, utovar/istovar robe, oba), broj
 * perona (1 99), status (otvorena, zatvorena).
 */