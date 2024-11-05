package hr.foi.jfletcher20.stations;

import hr.foi.jfletcher20.utils.IProduct;
import hr.foi.jfletcher20.enums.StationActivity;
import hr.foi.jfletcher20.enums.StationType;

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
public record Station(String name, // Stanica
    StationType type, // Vrsta stanice
    StationActivity activity, // Aktivnosti na stanici
    int platformCount, // Broj perona
    String status // Status stanice
) implements IProduct {

  public Station {
    if (platformCount < 1 || platformCount > 99)
      throw new IllegalArgumentException("Broj perona mora biti između 1 i 99");
    if (status == null || status.isBlank())
      throw new IllegalArgumentException("Status ne smije biti prazan");
  }

}
