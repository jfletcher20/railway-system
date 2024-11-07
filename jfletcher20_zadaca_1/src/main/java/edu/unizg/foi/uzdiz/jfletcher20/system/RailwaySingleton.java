package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
  private List<TrainComposition> compositions = new ArrayList<>();
  private Map<Integer, List<Wagon>> trains = new HashMap<>();
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

  public Wagon getWagon(String wagonId) {
    return this.wagons.stream().filter(w -> w.id().equals(wagonId)).findFirst().orElse(null);
  }

  public Map<Integer, List<Wagon>> getTrains() {
    return this.trains;
  }

  public List<TrainComposition> getCompositions() {
    return this.compositions;
  }

  public List<Wagon> getWagonsInTrain(int compositionID) {
    return this.trains.get(compositionID);
  }

  public List<TrainComposition> getCompositionsInTrain(int compositionId) {
    return this.compositions.stream().filter(c -> c.trainId() == compositionId).toList();
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

  public double getDistanceBetweenStations(Station station1, Station station2) {
    if (station1 == null || station2 == null)
      return -99999;

    var stations = this.railroad.get(station1.getTrack().id());
    if (stations == null)
      return -99999;

    int stationIndex1 = -1, stationIndex2 = -1;

    for (int i = 0; i < stations.size(); i++) {
      if (stations.get(i).equals(station1))
        stationIndex1 = i;
      if (stations.get(i).equals(station2))
        stationIndex2 = i;
    }

    // If either station wasn't found
    if (stationIndex1 == -1 || stationIndex2 == -1)
      return -99999;

    // Get track segments
    List<TrainTrack> trackSegments =
        this.tracks.stream().filter(t -> t.id().equals(station1.getTrack().id())).toList();

    // Calculate based on direction
    int start = Math.min(stationIndex1, stationIndex2);
    int end = Math.max(stationIndex1, stationIndex2);

    double totalDistance = 0;
    // Include the segment between end-1 and end
    for (int i = start; i < end; i++) {
      totalDistance += trackSegments.get(i).trackLength();
    }

    // If we're going backwards (station1 comes after station2 in the track)
//    if (stationIndex1 > stationIndex2) {
//      totalDistance = -totalDistance;
//    }

    Logs.i(String.format("Distance: %.2f units from [%d]::%s to [%d]::%s (%d segments)",
        totalDistance, stationIndex1, station1.name(), stationIndex2, station2.name(),
        Math.abs(stationIndex2 - stationIndex1)));

    return totalDistance;
  }

  public double getTotalTrackLength(String trackID) {
    if (trackID == null)
      return 0;

    return this.tracks.stream().filter(t -> t.id().equals(trackID))
        .mapToDouble(TrainTrack::trackLength).sum();
  }

  public void addWagon(Wagon wagon) {
    this.wagons.add(wagon);
    for (var composition : this.compositions) {
      if (composition.wagonId().equals(wagon.id())) {
        if (this.trains.containsKey(composition.trainId())) {
          this.trains.get(composition.trainId()).add(wagon);
        } else {
          this.trains.put(composition.trainId(), new ArrayList<>());
          this.trains.get(composition.trainId()).add(wagon);
        }
      }
    }
  }

  public void addComposition(TrainComposition composition) {
    this.compositions.add(composition);
    if (this.trains.containsKey(composition.trainId())) {
      Wagon wagon = composition.getWagon();
      if (wagon != null)
        this.trains.get(composition.trainId()).add(wagon);
    } else {
      this.trains.put(composition.trainId(), new ArrayList<>());
      Wagon wagon = composition.getWagon();
      if (wagon != null)
        this.trains.get(composition.trainId()).add(wagon);
    }
  }

  public void removeEmptyCompositions() {
    for (var composition : this.compositions) {
      if (!this.trains.containsKey(composition.trainId())) {
        Logs.e("Kompozicija " + composition.trainId() + " nema prijevozna sredstva.");
        this.compositions.remove(composition);
        continue;
      }
    }
  }

  public void verifyCompositions() {
    removeEmptyCompositions();
    for (TrainComposition composition : this.compositions) {
      if (this.trains.get(composition.trainId()).size() < 2) {
        Logs.e("Kompozicija " + composition.trainId() + " nema dovoljno prijevoznih sredstava.");
        this.compositions.remove(composition);
        continue;
      }
      if (composition.getTrainWagonsWithDriveRole() == null)
        continue;
      boolean hasLocomotive = false;
      for (var wagon : this.trains.get(composition.trainId())) {
        if (wagon.getIsPowered()) {
          hasLocomotive = true;
          break;
        }
      }
      if (!hasLocomotive) {
        Logs.e("Kompozicija " + composition.trainId() + " nema lokomotivu te se uklanja.");
        this.compositions.remove(composition);
        continue;
      }
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
    this.compositions.remove(composition);
    this.trains.remove(composition.trainId());
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
    Logs.o("Kompozicije: " + this.trains.size() + " (" + this.getCompositions().size() + ")");
    Logs.o("Pruge: " + this.tracks.size());
    Logs.footer(true);
  }

  public List<Station> getStationsByName(String station) {
    return getStations().stream().filter(s -> s.name().equals(station)).toList();
  }

  // Method to find all possible routes between stations
  public List<List<Station>> getRoutesBetweenStations(Station startStation, Station endStation) {

    if (startStation == null || endStation == null) {
      Logs.e("Ne može se pronaći ruta između stanica koje ne postoje.");
      return null;
    }

    if (startStation == endStation) {
      return List.of(List.of(startStation, endStation));
    }

    List<List<Station>> allRoutes = new ArrayList<>();
    Set<TrainTrack> visitedTracks = new HashSet<>();

    Set<Station> visited = new HashSet<>();
    List<Station> currentRoute = new ArrayList<>();

    Logs.w("Found " + allRoutes.size() + " routes between " + startStation.name() + " and "
        + endStation.name() + " with " + visited.size() + " stations visited.");

    dfs(startStation, endStation, visited, currentRoute, allRoutes, visitedTracks);
    return allRoutes;

  }


  // Depth First Search algorithm to find all possible routes between stations
  // recursively iterate over every station:
  // check if there are other tracks with a station with that station.name()
  // if there are, add that station to the current route and switch to that track
  // if the station is the end station, add the current route to allRoutes
  // else, continue the search down the current track
  //
  // if the station is not the end station, remove the station from the current route and
  // continue the search

  public void dfs(Station currentStation, Station endStation, Set<Station> visited,
      List<Station> currentRoute, List<List<Station>> allRoutes, Set<TrainTrack> visitedTracks) {

    visited.add(currentStation);
    currentRoute.add(currentStation);

    if (currentStation.name().equals(endStation.name())) {
      allRoutes.add(new ArrayList<>(currentRoute));
    } else {
      // Get all tracks that have stations with the same name as the current station
      Set<TrainTrack> connectedTracks = new HashSet<>();
      for (Map.Entry<String, List<Station>> entry : getRailroad().entrySet()) {
        if (entry.getValue().stream().anyMatch(s -> s.name().equals(currentStation.name()))) {
          connectedTracks.add(getTrackById(entry.getKey()));
        }
      }

      // For each connected track
      for (TrainTrack track : connectedTracks) {
        if (!visitedTracks.contains(track)) {
          visitedTracks.add(track);

          // Get the station object on this track that matches current station name
          Station trackStation = getStationsOnTrack(track.id()).stream()
              .filter(s -> s.name().equals(currentStation.name())).findFirst().orElse(null);

          if (trackStation != null) {
            // Get all next stations on this track
            List<Station> trackStations = getStationsOnTrack(track.id());
            int currentIndex = trackStations.indexOf(trackStation);

            // Check stations after current station
            for (int i = currentIndex + 1; i < trackStations.size(); i++) {
              Station nextStation = trackStations.get(i);
              if (!visited.contains(nextStation)) {
                dfs(nextStation, endStation, visited, currentRoute, allRoutes, visitedTracks);
              }
            }

            // Check stations before current station
            for (int i = currentIndex - 1; i >= 0; i--) {
              Station nextStation = trackStations.get(i);
              if (!visited.contains(nextStation)) {
                dfs(nextStation, endStation, visited, currentRoute, allRoutes, visitedTracks);
              }
            }
          }

          visitedTracks.remove(track);
        }
      }
    }

    visited.remove(currentStation);
    currentRoute.remove(currentRoute.size() - 1);
  }

  public boolean tracksIntersect(TrainTrack track1, TrainTrack track2) {
    return getStationsOnTrack(track1.id()).stream().anyMatch(
        s -> getStationsOnTrack(track2.id()).stream().anyMatch(s2 -> s.name().equals(s2.name())));
  }

}
/*
 * ISI2S Macinec - Zaprešić
 */
