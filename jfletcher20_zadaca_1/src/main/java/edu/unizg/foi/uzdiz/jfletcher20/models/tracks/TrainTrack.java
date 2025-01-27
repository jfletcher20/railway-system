package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackCategory;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.railway.RailwaySingleton;

/**
 * Train track object represents a train track.
 * 
 * @param id            Track code
 * @param category      Track category
 * @param transportType Type of transport
 * @param trackCount    Number of tracks
 * @param trackLength   Length of track
 * @param axleLoad      Axle load
 * @param length        Length of track
 * @param status        Track status
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
    if (trackCount < 1 || trackCount > 2)
      throw new IllegalArgumentException("Broj kolosjeka mora biti 1 ili 2.");
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

  public Station getStartStation(TrainType trainType) {
    return RailwaySingleton.getInstance().getStartStation(this.id, trainType);
  }

  public Station getEndStation(TrainType trainType) {
    return RailwaySingleton.getInstance().getEndStation(this.id, trainType);
  }

  public List<TrainTrack> getTracksByStatusAndCode(TrainTrackStatus status) {
    return RailwaySingleton.getInstance().getTrackSegmentsByStatusAndCode(this, status);
  }

  public List<TrainTrackSegment> getTrackSegments() {
    return RailwaySingleton.getInstance().getSegmentsOnTrack(this.id);
  }

  public List<TrainTrackSegment> getTrackSegmentsByStatus(TrainTrackStatus status) {
    return RailwaySingleton.getInstance().getSegmentsOnTrackByStatus(this.id, status);
  }

  @Override
  public String toString() {
    return "TrainTrack{" +
        "id='" + id + '\'' +
        "::category=" + category +
        "::transportType='" + transportType + '\'' +
        "::trackCount=" + trackCount +
        "::trackLength=" + trackLength +
        "::axleLoad=" + axleLoad +
        "::linearLoad=" + linearLoad +
        "::status=" + status +
        '}';
  }

  public List<TrainTrackSegment> getTrackSegmentsBetweenStations(String startStation, String endStation) {
    return RailwaySingleton.getInstance().getSegmentsBetweenStations(this, startStation, endStation);
  }

  public List<TrainTrackSegment> getTrackSegmentsBetweenStationsOrToEnd(String startStation, String endStation) {
    return RailwaySingleton.getInstance().getSegmentsBetweenStationsOrToEnd(this, startStation, endStation);
  }

  public TraversalDirection getTraversalDirectionForStations(String startStation, String endStation) {
    return RailwaySingleton.getInstance().getTraversalDirectionForStations(this, startStation, endStation);
  }
}
