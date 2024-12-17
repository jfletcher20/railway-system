package edu.unizg.foi.uzdiz.jfletcher20.models.stations;

import edu.unizg.foi.uzdiz.jfletcher20.enums.StationActivity;
import edu.unizg.foi.uzdiz.jfletcher20.enums.StationType;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

/**
 * Station object represents a single train station.
 * 
 * @param name          Station name
 * @param type          Station type (kolodvor, stajalište)
 * @param activity      Passenger activity (ulaz/izlaz putnika), cargo activity
 *                      (utovar/istovar robe),
 *                      both
 * @param platformCount Number of platforms (1-99)
 * @param status        Status of the station (otvorena, zatvorena)
 * @param timeNormal    Time for normal train
 * @param timeFast      Time for fast train; does not have to be set
 * @param timeExpress   Time for express train; does not have to be set
 */
public record Station(
    String name, // Stanica
    StationType type, // Vrsta stanice
    StationActivity activity, // Aktivnosti na stanici
    int platformCount, // Broj perona
    String status, // Status stanice
    int timeNormal, // Vrijeme normalni vlak
    int timeFast, // Vrijeme ubrzani vlak
    int timeExpress // Vrijeme brzi vlak
) implements IProduct {
  public Station {
    if (platformCount < 1 || platformCount > 99)
      throw new IllegalArgumentException("Broj perona mora biti između 1 i 99");
    if (status == null || status.isBlank())
      throw new IllegalArgumentException("Nepoznat status stanice");
    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Nepoznato ime stanice");
    if (type == null)
      throw new IllegalArgumentException("Nepoznat tip stanice");
    if (activity == null)
      throw new IllegalArgumentException("Nepoznata aktivnost stanice");
    if (timeNormal < 0)
      throw new IllegalArgumentException("Vrijeme normalnog vlaka mora biti definirano i ne može biti negativno");
  }

  public TrainTrack getTrack() {
    return RailwaySingleton.getInstance().getTrackOfStation(this);
  }

  public double getDistanceTo(Station other) {
    return RailwaySingleton.getInstance().calculateDistance(this, other);
  }

  public double getDistanceFromStart() {
    return RailwaySingleton.getInstance().getDistanceFromStart(this);
  }

  public double getDistanceFromEnd() {
    return RailwaySingleton.getInstance().getDistanceFromEnd(this);
  }

  public double getDistanceFromStart(Station first) {
    return RailwaySingleton.getInstance().getDistanceFromStart(first, this);
  }

  public double getDistanceFromEnd(Station last, Station first) {
    return RailwaySingleton.getInstance().getDistanceFromEnd(last, first, this);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Station))
      return false;
    Station other = (Station) obj;
    return name().equals(other.name());
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }

  public Boolean supportsTrainType(TrainType type) {
    Logs.i("station " + name() + " supports " + type.name() + " -> result: " + (timeForTrainType(type) >= 0));
    return switch (type) {
      case NORMAL -> true;
      case FAST -> timeFast() >= 0;
      case EXPRESS -> timeExpress() >= 0;
    };
  }

  public int timeForTrainType(TrainType trainType) {
    return switch (trainType) {
      case NORMAL -> timeNormal();
      case FAST -> timeFast();
      case EXPRESS -> timeExpress();
    };
  }

}
