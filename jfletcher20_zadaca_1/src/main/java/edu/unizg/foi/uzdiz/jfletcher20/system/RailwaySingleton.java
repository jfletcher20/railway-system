package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposition;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.models.wagons.Wagon;

public class RailwaySingleton {

  static private volatile RailwaySingleton instance = new RailwaySingleton();
  private CommandSystem commandSystem = null;

  private List<TrainTrack> tracks = new ArrayList<>();
  private List<Wagon> wagons = new ArrayList<>();
  private Map<Integer, List<TrainComposition>> compositions = new HashMap<>();
  private Map<String, List<Station>> railroad = new HashMap<>();

  private String[] initArgs = null;

  private RailwaySingleton() {
    Logs.i("RailwaySingleton instance created. Values are not initialized.");
  }

  public static RailwaySingleton getInstance() {
    return instance;
  }

  public void setInitArgs(String[] args) {
    this.initArgs = args;
    Logs.i("RailwaySingleton initArgs set.");
  }

  public String[] getInitArgs() {
    return this.initArgs;
  }

  public void setCommandSystem(CommandSystem commandSystem) {
    this.commandSystem = commandSystem;
    Logs.i("RailwaySingleton CommandSystem initialized");
  }

  public CommandSystem getCommandSystem() {
    return this.commandSystem;
  }

  public IProduct addProduct(IProduct product) {
    if (product instanceof Station) {
      // not gonna add the station here since it's been added to the railroad
    } else if (product instanceof Wagon) {
      this.addWagon((Wagon) product);
    } else if (product instanceof TrainComposition) {
      this.addComposition((TrainComposition) product);
    } else if (product instanceof TrainTrack) {
      this.addTrack((TrainTrack) product);
    }
    return product;
  }

  public IProduct addProduct(IProduct product, IProduct altProduct) {
    if (altProduct == null) {
      return this.addProduct(product);
    } else if (product != null && altProduct != null) {
      if (product instanceof Station && altProduct instanceof TrainTrack) {
        this.addRailroad((TrainTrack) altProduct, (Station) product);
        this.addProduct(product);
        this.addProduct(altProduct);
        return product;
      }
    }
    return null;
  }

  public boolean addRailroad(TrainTrack track, Station station) {
    if (this.railroad.containsKey(track.id())) {
      this.railroad.get(track.id()).add(station);
      return true;
    } else {
      this.railroad.put(track.id(), new ArrayList<>());
      this.railroad.get(track.id()).add(station);
    }
    return true;
  }

  public List<Station> getStations() {
    return this.railroad.values().stream().flatMap(List::stream).toList();
  }

  public Map<String, List<Station>> getRailroad() {
    return this.railroad;
  }

  public List<Wagon> getWagons() {
    return this.wagons;
  }

  public Map<Integer, List<TrainComposition>> getCompositions() {
    return this.compositions;
  }

  public List<TrainComposition> getCompositionsList() {
    return getCompositions().values().stream().flatMap(List::stream).toList();
  }

  public List<TrainComposition> getCompositionsListByCompositionID(int compositionID) {
    return getCompositions().get(compositionID);
  }

  public List<TrainTrack> getTracks() {
    return this.tracks;
  }

  public TrainTrack getTrackById(String trackID) {
    return this.tracks.stream().filter(t -> t.id().equals(trackID)).findFirst().orElse(null);
  }

  public Station getStartStation(String trackID) {
    return this.railroad.get(trackID).getFirst();
  }

  public Station getEndStation(String trackID) {
    return this.railroad.get(trackID).getLast();
  }

  public Station getStartStationFromIndexOfStation(String trackID, String stationName) {
    var stationsOnTrack = this.railroad.get(trackID);
    return stationsOnTrack.stream().filter(s -> s.name().equals(stationName)).findFirst()
        .orElse(null);
  }

  public Station getEndStationFromIndexOfStation(String trackID, String stationName) {
    var stationsOnTrack = this.railroad.get(trackID);
    return stationsOnTrack.stream().filter(s -> s.name().equals(stationName)).findFirst()
        .orElse(null);
  }

  public List<Station> getStationsOnTrack(String trackID) {
    return this.railroad.get(trackID);
  }

  public TrainTrack getTrackOfStation(Station station) {
    for (String trackID : this.railroad.keySet()) {
      var stations = this.railroad.get(trackID);
      int stationIndex = -1;
      for (int i = 0; i < stations.size(); i++) {
        if (stations.get(i) == station) {
          stationIndex = i;
          break;
        }
      }
      if (stationIndex != -1)
        return this.tracks.stream().filter(t -> t.id().equals(trackID)).toList().get(stationIndex);
    }
    return null;
  }

  public double getDistanceFromStart(Station station) {
    TrainTrack currentTrack = getTrackOfStation(station);
    return getDistanceBetweenStations(currentTrack.id(), currentTrack.getStartStation(), station);
  }

  public double getDistanceBetweenStations(String trackID, Station station1, Station station2) {
    var stations = this.railroad.get(trackID);
    int stationIndex1 = -1, stationIndex2 = -1;
    for (int i = 0; i < stations.size(); i++) {
      if (stations.get(i) == station1)
        stationIndex1 = i;
      if (stations.get(i) == station2)
        stationIndex2 = i;
    }
    List<TrainTrack> tracks = this.tracks.stream().filter(t -> t.id().equals(trackID)).toList();
    tracks = tracks.subList(stationIndex1, stationIndex2 + 1);
    Logs.i("RailwaySingleton getDistanceBetweenStations: " + tracks.size() + " tracks between ["
        + stationIndex1 + "]::" + station1.name() + " and [" + stationIndex2 + "]::"
        + station2.name());
    return tracks.stream().mapToDouble(TrainTrack::trackLength).sum();
  }

  public double getTotalTrackLength(String trackID) {
    double sum = 0;
    for (var track : this.tracks.stream().filter(t -> t.id().equals(trackID)).toList())
      sum += track.trackLength();
    return sum;
  }

  // public void addStation(Station station) {
  // this.railroad.put(station.name(), new ArrayList<>());
  // }

  public void addWagon(Wagon wagon) {
    this.wagons.add(wagon);
  }

  public void addComposition(TrainComposition composition) {
    if (this.compositions.containsKey(composition.ID())) {
      this.compositions.get(composition.ID()).add(composition);
    } else {
      this.compositions.put(composition.ID(), new ArrayList<TrainComposition>());
      this.compositions.get(composition.ID()).add(composition);
    }
  }

  public void addTrack(TrainTrack track) {
    this.tracks.add(track);
  }

  public void removeStation(Station station) {
    for (var track : this.railroad.keySet())
      this.railroad.get(track).removeIf(s -> s.equals(station));
  }

  public void removeWagon(Wagon wagon) {
    this.wagons.remove(wagon);
  }

  public void removeComposition(int ID) {
    this.compositions.remove(ID);
  }

  public void removeComposition(TrainComposition composition) {
    this.compositions.get(composition.ID()).removeIf(c -> c.equals(composition));
  }

  public void removeTrack(TrainTrack track) {
    this.railroad.remove(track.id());
    this.tracks.remove(track);
  }

  public void clearStations() {
    this.railroad.values().forEach(List::clear);
  }

  public void clearWagons() {
    this.wagons.clear();
  }

  public void clearCompositions() {
    this.compositions.clear();
  }

  public void clearTracks() {
    clearRailroad();
    this.tracks.clear();
  }

  public void clearRailroad() {
    this.railroad.clear();
  }

  public void clearAll() {
    this.clearTracks();
    this.clearStations();
    this.clearWagons();
    this.clearCompositions();
    this.clearRailroad();
  }

  public void printStats() {
    Logs.header("JLF Željeznica: Statistika", true);
    Logs.o("Stanice: " + this.getStations().size());
    Logs.o("Vozila: " + this.wagons.size());
    Logs.o("Kompozicije: " + this.compositions.size() + " (" + this.getCompositionsList().size()
        + ")");
    Logs.o("Pruge: " + this.tracks.size());
    Logs.footer(true);
  }

}
