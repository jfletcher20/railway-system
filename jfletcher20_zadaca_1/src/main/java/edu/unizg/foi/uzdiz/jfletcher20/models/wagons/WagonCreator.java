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
      Logs.e(row, "WagonCreator Prazan redak.");
      return null;
    } else if (data.split(";").length != columnCount) {
      Logs.e(row, columnCountError(data.split(";").length));
      return null;
    }
    String[] parts = data.split(";");
    return new Wagon(
        parts[0], // oznaka
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
        parts[17].equals("I") // status
    );
  }

  private String columnCountError(int counts) {
    return "WagonCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
  }

}

/*
 * // the header for the csv file for wagons: private static Pattern zpsHeaderPattern =
 * Pattern.compile("^Oznaka;Opis;Proizvođač;Godina;Namjena;Vrsta prijevoza;" +
 * "Vrsta pogona;Maks brzina;Maks snaga;Broj sjedećih mjesta;" +
 * "Broj stajaćih mjesta;Broj bicikala;Broj kreveta;Broj automobila;Nosivost;Površina;Zapremina;Status$"
 * );
 *
 * // the file has columns out of which we need to extract the following, which are in a different
 * order: public record Wagon(WagonType purpose, // namjena TransportType transportType, // vrsta
 * prijevoza DriveType driveType, // vrsta pogona double maxPower, // maksimalna snaga int maxSpeed,
 * // maksimalna brzina vožnje int yearOfProduction, // godina proizvodnje String manufacturer, //
 * proizvođač int numberOfSeats, // broj sjedećih mjesta double capacity, // nosivost double volume,
 * // zapremina boolean status // status )
 */
