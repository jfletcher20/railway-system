package edu.unizg.foi.uzdiz.jfletcher20.models.wagons;

import edu.unizg.foi.uzdiz.jfletcher20.enums.WagonType;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;

/**
 * Factory class for creating Wagon objects. Each Wagon object represents a single train car.
 * 
 * Wagon objects have 10 attributes.
 */
public class WagonCreator implements ICreator {

  private static int columnCount = 18;

  public WagonCreator() {}

  /**
   * Factory method for creating Wagon objects.
   * 
   * @param data String containing the data needed to create a Wagon object.
   * @return Wagon object
   */
  @Override
  public Wagon factoryMethod(String data, int row) {
    if (data == null || data.isEmpty()) {
      Logs.w(row, "WagonCreator Prazan redak.");
      return null;
    } else if (data.split(";", -1).length != columnCount) {
      Logs.w(row, columnCountError(data.split(";", -1).length));
      return null;
    }
    String[] parts = data.split(";", -1);
    return new Wagon(parts[0], // oznaka
        parts[1], // opis
        WagonType.fromCSV(parts[4]), // namjena
        /* TransportType.valueOf( */parts[5]/* .toUpperCase()) */, // vrsta prijevoza
        /* DriveType.valueOf( */ parts[6] /* .toUpperCase()) */, // vrsta pogona
        ParsingUtil.d(parts[8]), // maksimalna snaga
        ParsingUtil.i(parts[7]), // maksimalna brzina vožnje
        ParsingUtil.i(parts[3]), // godina proizvodnje
        parts[2], // proizvođač
        ParsingUtil.i(parts[9]), // broj sjedećih mjesta
        ParsingUtil.i(parts[10]), // broj stajaćih mjesta
        ParsingUtil.i(parts[11]), // broj bicikala
        ParsingUtil.i(parts[12]), // broj kreveta
        ParsingUtil.i(parts[13]), // broj automobila
        ParsingUtil.d(parts[14]), // nosivost
        ParsingUtil.d(parts[15]), // površina
        ParsingUtil.d(parts[16]), // zapremina
        parts[17].isBlank() ? false : parts[17].equals("I") // status
    );
  }

  private String columnCountError(int counts) {
    return "WagonCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
  }

}
