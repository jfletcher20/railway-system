package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposition;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.Schedule;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule_days.ScheduleDays;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;
import edu.unizg.foi.uzdiz.jfletcher20.models.wagons.Wagon;

public class RailwaySingleton {

  static private volatile RailwaySingleton instance = new RailwaySingleton();
  static public final Class<?> PREFERRED_COMMAND_SYSTEM = CommandSystemSingleton.class;

  private List<TrainTrack> tracks = new ArrayList<>();
  private List<Wagon> wagons = new ArrayList<>();
  private List<TrainComposition> compositions = new ArrayList<>();
  private Map<Integer, List<Wagon>> trainCompositions = new HashMap<>();
  private Map<String, List<Station>> railroad = new HashMap<>();
  private Map<String, ScheduleDays> scheduleDays = new HashMap<>();
  private List<Schedule> schedules = new ArrayList<>();

  private List<User> users = new ArrayList<>();

  private ScheduleComposite scheduleComposite = new ScheduleComposite();

  private String[] initArgs = null;

  private RailwaySingleton() {
    Logs.i("RailwaySingleton instance created. Values are not initialized.");
  }

  public static RailwaySingleton getInstance() {
    return instance;
  }

  public void setInitArgs(String[] args) {
    List<String> newArgs = new ArrayList<>();
    int zodIndex = -1, zvrIndex = -1;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--zod"))
        zodIndex = i++;
      else if (args[i].equals("--zvr"))
        zvrIndex = i++;
      else
        newArgs.add(args[i]);
    }

    if (zodIndex != -1 && zvrIndex != -1) {
      String zodArg = args[zodIndex], zodValue = args[zodIndex + 1];
      String zvrArg = args[zvrIndex], zvrValue = args[zvrIndex + 1];
      newArgs.add(zodArg);
      newArgs.add(zodValue);
      newArgs.add(zvrArg);
      newArgs.add(zvrValue);
    }

    this.initArgs = newArgs.toArray(new String[0]);
    Logs.toggleInfo();
    Logs.i("RailwaySingleton initArgs set to: " + String.join(" ", this.initArgs));
  }

  public String[] getInitArgs() {
    return this.initArgs;
  }

  public double calculateDistance(Station a, Station b) {
    return Math.abs(a.getDistanceFromStart() - b.getDistanceFromStart());
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
    } else if (product instanceof ScheduleDays) {
      this.addScheduleDays((ScheduleDays) product);
    } else if (product instanceof Schedule) {
      this.addSchedule((Schedule) product);
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

  public boolean addSchedule(Schedule schedule) {
    this.schedules.add(schedule);
    addToComposite(schedule);
    return true;
  }

  public void addToComposite(Schedule schedule) {
    scheduleComposite.Add(new TrainComposite(schedule, scheduleComposite));
  }

  public ScheduleComposite getSchedule() {
    return scheduleComposite;
  }

  public List<Schedule> getSchedules() {
    return this.schedules;
  }

  public boolean addScheduleDays(ScheduleDays scheduleDays) {
    this.scheduleDays.put(scheduleDays.dayID(), scheduleDays);
    return true;
  }

  public ScheduleDays getScheduleDays(String dayID) {
    if (dayID.isBlank()) {
      if (this.scheduleDays.containsKey("-"))
        return this.scheduleDays.get("-");
      else {
        this.scheduleDays.put("-", new ScheduleDays("-", Set.of(Weekday.values())));
        return this.scheduleDays.get("-");
      }
    }
    return this.scheduleDays.get(dayID);
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

  public Map<Integer, List<Wagon>> getTrainCompositions() {
    return this.trainCompositions;
  }

  public List<TrainComposition> getCompositions() {
    return this.compositions;
  }

  public List<Wagon> getWagonsInTrain(int compositionID) {
    return this.trainCompositions.get(compositionID);
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

  public double getDistanceFromStart(Station station) {
    TrainTrack currentTrack = getTrackOfStation(station);
    return getDistanceBetweenStations(currentTrack.id(), currentTrack.getStartStation(), station);
  }

  public double getDistanceFromStart(Station firstStation, Station currentStation) {
    TrainTrack currentTrack = getTrackOfStation(currentStation);
    return getDistanceBetweenStations(currentTrack.id(), firstStation, currentStation);
  }

  public double getDistanceFromEnd(Station station) {
    TrainTrack currentTrack = getTrackOfStation(station);
    return getDistanceFromStart(currentTrack.getEndStation()) - getDistanceFromStart(station);
  }

  public double getDistanceFromEnd(Station lastStation, Station firstStation,
      Station currentStation) {
    return Math.abs(getDistanceFromStart(lastStation, currentStation)
        - getDistanceFromStart(lastStation, firstStation));
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
    return tracks.stream().mapToDouble(TrainTrack::trackLength).sum();
  }

  public double getDistanceBetweenStations(Station station1, Station station2, TrainType trainType,
      boolean ignoreTrainType) {
    List<List<Edge>> edges = getRoutesBetweenStations(station1, station2);
    List<Edge> shortestRoute = edges.stream().min((a, b) -> {
      double aDistance = a.stream().mapToDouble(e -> e.weight).sum();
      double bDistance = b.stream().mapToDouble(e -> e.weight).sum();
      return Double.compare(aDistance, bDistance);
    }).orElse(null);
    if (shortestRoute == null)
      return 0;
    List<Edge> shortestRouteFiltered = shortestRoute.stream()
        .filter(e -> ignoreTrainType || e.from.supportsTrainType(trainType))
        .toList();

    return shortestRouteFiltered.stream().mapToDouble(e -> e.weight).sum();
  }

  public double getDistanceBetweenStations(String trackID, Station a, Station b, TraversalDirection direction,
      TrainType trainType) {
    if (direction == TraversalDirection.FORTH) {
      return getDistanceBetweenStations(a, b, trainType, true);
    } else {
      return getDistanceBetweenStations(b, a, trainType, true);
    }
  }

  public double getDistanceBetweenStations(Schedule schedule) {
    return getDistanceBetweenStations(schedule.trackID(), schedule.departure(), schedule.destination(),
        schedule.direction(), schedule.trainType());
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
        if (this.trainCompositions.containsKey(composition.trainId())) {
          this.trainCompositions.get(composition.trainId()).add(wagon);
        } else {
          this.trainCompositions.put(composition.trainId(), new ArrayList<>());
          this.trainCompositions.get(composition.trainId()).add(wagon);
        }
      }
    }
  }

  public void addComposition(TrainComposition composition) {
    this.compositions.add(composition);
    if (this.trainCompositions.containsKey(composition.trainId())) {
      Wagon wagon = composition.getWagon();
      if (wagon != null)
        this.trainCompositions.get(composition.trainId()).add(wagon);
    } else {
      this.trainCompositions.put(composition.trainId(), new ArrayList<>());
      Wagon wagon = composition.getWagon();
      if (wagon != null)
        this.trainCompositions.get(composition.trainId()).add(wagon);
    }
  }

  public void removeEmptyCompositions() {
    for (var composition : this.compositions) {
      if (!this.trainCompositions.containsKey(composition.trainId())) {
        Logs.e("Kompozicija " + composition.trainId() + " nema prijevozna sredstva.");
        this.compositions.remove(composition);
        continue;
      }
    }
  }

  public void verifyCompositions() {
    Logs.o("Naknadna verifikacija kompozicija...");
    removeEmptyCompositions();
    List<TrainComposition> compositionsToRemove = new ArrayList<>();
    for (TrainComposition composition : this.compositions) {
      if (this.trainCompositions.get(composition.trainId()).size() < 2) {
        Logs.e("Kompozicija " + composition.trainId() + " nema dovoljno prijevoznih sredstava.");
        compositionsToRemove.add(composition);
        continue;
      }
      if (composition.getTrainWagonsWithDriveRole() == null)
        continue;
      boolean hasLocomotive = false;
      boolean hasUnpoweredWagons = false;
      for (var wagon : this.trainCompositions.get(composition.trainId()))
        if (wagon.getIsPowered()) {
          hasLocomotive = true;
        } else
          hasUnpoweredWagons = true;
      if (!hasLocomotive || !hasUnpoweredWagons) {
        var increaseErrorCount = compositionsToRemove.stream().noneMatch(c -> c.trainId() == composition.trainId());
        String errorInfo = !hasLocomotive ? "lokomotivu, " : "";
        errorInfo += !hasUnpoweredWagons ? "vagona bez pogona, " : "";
        if (increaseErrorCount)
          Logs.e("Kompozicija " + composition.trainId() + " nema " + errorInfo + "zbog čega se uklanja.",
              increaseErrorCount);
        compositionsToRemove.add(composition);
        continue;
      }
    }
    _removeCompositions(compositionsToRemove);
    Logs.o("Provjera kompozicija završena.");
  }

  private void _removeCompositions(List<TrainComposition> compositionsToRemove) {
    for (var composition : compositionsToRemove) {
      if (this.compositions.contains(composition) || this.trainCompositions.containsKey(composition.trainId())) {
        Logs.w("Uklanjanje kompozicije " + composition.trainId() + "...");
        this.compositions.remove(composition);
        this.trainCompositions.remove(composition.trainId());
      }
    }
  }

  public void verifyTrainTracks() {
    Logs.o("Naknadna verifikacija pruga...");
    List<TrainTrack> tracksToRemove = new ArrayList<>();
    for (var track : this.tracks) {
      if (this.railroad.get(track.id()).size() < 2) {
        Logs.e("Pružni segment " + track.id() + " nema dovoljno stanica.");
        tracksToRemove.add(track);
      }
    }
    for (var track : tracksToRemove) {
      Logs.w("Uklanjanje pruge " + track.id() + "...");
      this.tracks.remove(track);
      this.railroad.remove(track.id());
    }
    Logs.o("Provjera pruga završena.");
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
    this.trainCompositions.remove(composition.trainId());
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
    Logs.o("Kompozicije: " + this.trainCompositions.size() + " (" +
        this.getCompositions().size() + ")");
    Logs.o("Pruge: " + this.tracks.size());
    Logs.o("Vozni redovi: " + this.schedules.size());
    Logs.footer(true);
  }

  public List<Station> getStationsByName(String station) {
    return getStations().stream().filter(s -> s.name().equals(station)).toList();
  }

  public class Graph {
    private Map<Station, List<Edge>> adjacencyList;

    public Graph() {
      adjacencyList = new HashMap<>();
    }

    public void addEdge(Station from, Station to, double distance) {
      adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(from, to, distance));
    }

    public List<Edge> getEdges(Station station) {
      return adjacencyList.getOrDefault(station, new ArrayList<>());
    }
  }

  public class Edge {
    Station from;
    Station to;
    double weight;

    public Edge(Station from, Station to, double weight) {
      this.from = from;
      this.to = to;
      this.weight = weight;
    }
  }

  public List<List<Edge>> getRoutesBetweenStations(Station startStation, Station endStation) {
    if (startStation == null || endStation == null) {
      Logs.e("Ne postoji ruta između stanica"
          + (startStation == null ? " [start station]" : "")
          + (endStation == null ? " [end station]" : ""));
      return null;
    }

    Graph graph = new Graph();
    for (List<Station> trackStations : getRailroad().values()) {
      for (int i = 0; i < trackStations.size() - 1; i++) {
        Station stationA = trackStations.get(i);
        Station stationB = trackStations.get(i + 1);
        double distance = calculateDistance(stationA, stationB);
        graph.addEdge(stationA, stationB, distance);
        graph.addEdge(stationB, stationA, distance);
      }
    }

    List<List<Edge>> allRoutes = new ArrayList<>();
    List<Edge> currentRoute = new ArrayList<>();
    Set<Station> visited = new HashSet<>();

    dfs(graph, startStation, endStation, visited, currentRoute, allRoutes);

    return allRoutes;
  }

  private void dfs(Graph graph, Station currentStation, Station endStation,
      Set<Station> visited, List<Edge> currentRoute,
      List<List<Edge>> allRoutes) {
    if (currentStation.equals(endStation)) {
      allRoutes.add(new ArrayList<>(currentRoute));
      return;
    }

    visited.add(currentStation);

    for (Edge edge : graph.getEdges(currentStation)) {
      Station neighbor = edge.to;
      if (!visited.contains(neighbor)) {
        currentRoute.add(edge);
        dfs(graph, neighbor, endStation, visited, currentRoute, allRoutes);
        currentRoute.remove(currentRoute.size() - 1);
      }
    }

    visited.remove(currentStation);
  }

  public void addUser(String name, String lastName) {
    users.add(new User(name, lastName));
  }

  public List<User> getUsers() {
    return this.users;
  }

  public double getDistanceBetweenStations(Station station1, Station station2) {
    TrainTrack track = getTrackOfStation(station1);
    return getDistanceBetweenStations(track.id(), station1, station2);
  }

  /**
   * Get user by name and last name
   * Check if user exists, if not create a new user
   * 
   * @param name     User's name; can also include middle name
   * @param lastName User's last name
   * @return
   */
  public User getUserByName(String name, String lastName) {
    return this.users.stream().filter(u -> u.name().equals(name) && u.lastName().equals(lastName)).findFirst()
        .orElseGet(() -> {
          Logs.o("Korisnik " + name + " " + lastName + " ne postoji. Dodajem korisnika...");
          User newUser = new User(name, lastName);
          this.users.add(newUser);
          return newUser;
        });
  }

  public List<Station> getStationsOnTrack(String trackID, TrainType trainType) {
    return getStationsOnTrack(trackID).stream().filter(s -> s.supportsTrainType(trainType)).toList();
  }

  public Station getStartStation(String id, TrainType trainType) {
    return getStationsOnTrack(id, trainType).getFirst();
  }

  public Station getEndStation(String id, TrainType trainType) {
    return getStationsOnTrack(id, trainType).getLast();
  }

  public List<String> trainsWithInvalidStages = new ArrayList<>();
  public void verifyTrains() {
    this.scheduleComposite.Remove(scheduleComposite);
  }

}
