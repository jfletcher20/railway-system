package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackCategory;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

/**
 * Train track object represents a train track.
 * 
 * @param id Track code
 * @param category Track category
 * @param transportType Type of transport
 * @param trackCount Number of tracks
 * @param trackLength Length of track
 * @param axleLoad Axle load
 * @param length Length of track
 * @param status Track status
 */
public record TrainTrack(String id, // oznaka pruge
    TrainTrackCategory category, // kategorija pruge
    String transportType, // vrsta prijevoza
    int trackCount, // broj kolosjeka
    double trackLength, // dužina pruge u km
    double axleLoad, // DO po osovini
    double linearLoad, // DO po duznom m
    TrainTrackStatus status // status pruge
) implements IProduct {
  public TrainTrack {
    if (id == null || id.isEmpty())
      throw new IllegalArgumentException("Oznaka pruge ne smije biti prazna.");
    if (category == null)
      throw new IllegalArgumentException("Kategorija pruge ne smije biti prazna.");
    if (transportType == null || transportType.isEmpty())
      throw new IllegalArgumentException("Vrsta prijevoza ne smije biti prazna.");
    if (trackLength < 0 || trackLength > 999)
      throw new IllegalArgumentException("Dužina pruge mora biti između 0 i 999 km.");
    if (trackCount < 1)
      throw new IllegalArgumentException("Broj kolosjeka ne smije biti manji od 1.");
    if (axleLoad < 10 || axleLoad > 50)
      throw new IllegalArgumentException("DO po osovini mora biti između 10 i 50 t/os.");
    if (linearLoad < 2 || linearLoad > 10)
      throw new IllegalArgumentException("DO po dužnom metru mora biti između 2 i 10 t/m.");
    if (status == null)
      throw new IllegalArgumentException("Status pruge mora biti definiran.");
  }

  public Station getStartStation() {
    return RailwaySingleton.getInstance().getStartStation(this.id);
  }

  public Station getEndStation() {
    return RailwaySingleton.getInstance().getEndStation(this.id);
  }
}
