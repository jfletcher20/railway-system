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

  private List<Station> stations = new ArrayList<>();
  private List<TrainTrack> tracks = new ArrayList<>();
  private List<Wagon> wagons = new ArrayList<>();
  private Map<Integer, List<TrainComposition>> compositions = new HashMap<>();
  private Map<String, List<String>> railroad = new HashMap<>();

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
      this.addStation((Station) product);
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
      this.railroad.get(track.id()).add(station.name());
      return true;
    } else {
      this.railroad.put(track.id(), new ArrayList<String>());
      this.railroad.get(track.id()).add(station.name());
    }
    return true;
  }

  public List<Station> getStations() {
    return this.stations;
  }

  public Map<String, List<String>> getRailroad() {
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
    int stationIndexOnTrack =
        this.railroad.get(trackID).indexOf(this.railroad.get(trackID).getFirst());
    int trackIndexOnRailroad = this.railroad.keySet().stream().toList().indexOf(trackID);
    int totalStationsBeforeTrack = 0;
    for (int i = 0; i < trackIndexOnRailroad; i++)
      totalStationsBeforeTrack +=
          this.railroad.get(this.railroad.keySet().stream().toList().get(i)).size();
    int realStationIndex = totalStationsBeforeTrack + stationIndexOnTrack;
    return this.stations.get(realStationIndex);
  }

  public Station getEndStation(String trackID) {
    int stationIndexOnTrack =
        this.railroad.get(trackID).indexOf(this.railroad.get(trackID).getLast());
    int trackIndexOnRailroad = this.railroad.keySet().stream().toList().indexOf(trackID);
    Logs.i("End Station index on track: " + stationIndexOnTrack);
    Logs.i("Track index on railroad: " + trackIndexOnRailroad);
    int totalStationsBeforeTrack = 0;
    for (int i = 0; i < trackIndexOnRailroad; i++)
      totalStationsBeforeTrack +=
          this.railroad.get(this.railroad.keySet().stream().toList().get(i)).size();
    int realStationIndex = totalStationsBeforeTrack + stationIndexOnTrack;
    return this.stations.get(realStationIndex);
  }

  public Station getStartStationFromIndexOfStation(String trackID, String stationName) {
    return this.stations.stream().filter(s -> this.railroad.get(trackID)
        .subList(this.railroad.get(trackID).indexOf(stationName), this.railroad.get(trackID).size())
        .contains(s.name())).findFirst().orElse(null);
  }

  public Station getEndStationFromIndexOfStation(String trackID, String stationName) {
    return this.stations.stream()
        .filter(s -> this.railroad.get(trackID)
            .subList(this.railroad.get(trackID).indexOf(stationName),
                this.railroad.get(trackID).size())
            .contains(s.name()))
        .reduce((first, second) -> second).orElse(null);
  }
  
  public List<Station> getStationsOnTrack(String trackID) {
    var stationNames = this.railroad.get(trackID);
    List<Station> stationsOnTrack = new ArrayList<>();
    int trackIndex = this.railroad.keySet().stream().toList().indexOf(trackID);
    int totalStationsBeforeTrack = 0;
    for (int i = 0; i < trackIndex; i++)
      totalStationsBeforeTrack +=
          this.railroad.get(this.railroad.keySet().stream().toList().get(i)).size();
    for (int i = 0; i < stationNames.size(); i++)
      stationsOnTrack.add(this.stations.get(totalStationsBeforeTrack + i));
    return stationsOnTrack;
  }

  public double getDistanceBetweenStations(String trackID, String station1, String station2) {
    List<String> railroadStations = this.railroad.get(trackID);
    int index1 = railroadStations.indexOf(station1);
    int index2 = railroadStations.indexOf(station2) + 1;

    // stations between (and including) station1 and station2
    // var stationsBetween = railroadStations.subList(index1, index2);
    // var stationObjects = this.stations.stream().filter(s ->
    // stationsBetween.contains(s.name())).toList();

    // get all tracks with that ID
    var trackObjects = this.tracks.stream().filter(t -> t.id().equals(trackID)).toList();
    trackObjects = trackObjects.subList(index1, index2);
    double sum = 0;
    for (TrainTrack track : trackObjects)
      sum += track.trackLength();
    return sum;
  }

  public double getTotalTrackLength(String trackID) {
    double sum = 0;
    for (var track : this.tracks.stream().filter(t -> t.id().equals(trackID)).toList())
      sum += track.trackLength();
    return sum;
  }


  public void addStation(Station station) {
    this.stations.add(station);
  }

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
    var stationsLists = this.railroad.values();
    for (List<String> stationList : stationsLists)
      stationList.removeIf(s -> s.equals(station.name()));
    this.stations.remove(station);
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
    this.stations.clear();
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
    Logs.o("Stanice: " + this.stations.size());
    Logs.o("Vozila: " + this.wagons.size());
    Logs.o("Kompozicije: " + this.compositions.size() + " (" + this.getCompositionsList().size()
        + ")");
    Logs.o("Pruge: " + this.tracks.size());
    Logs.footer(true);
  }

}
