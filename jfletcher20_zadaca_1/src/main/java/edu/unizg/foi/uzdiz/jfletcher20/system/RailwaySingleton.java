package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.unizg.foi.uzdiz.jfletcher20.compositions.TrainComposition;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.wagons.Wagon;

public class RailwaySingleton {

  static private volatile RailwaySingleton instance = new RailwaySingleton();

  private List<Station> stations = new ArrayList<>();
  private List<Wagon> wagons = new ArrayList<>();
  private Map<Integer, List<TrainComposition>> compositions = new HashMap<>();
  private List<TrainTrack> tracks = new ArrayList<>();

  private String[] initArgs = null;

  private RailwaySingleton() {
    System.out.println("Singleton created");
  }

  public static RailwaySingleton getInstance() {
    return instance;
  }

  public void setInitArgs(String[] args) {
    this.initArgs = args;
  }

  public String[] getInitArgs() {
    return this.initArgs;
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

  public List<Station> getStations() {
    return this.stations;
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
    this.tracks.remove(track);
  }

  public void clearStations() {
    this.stations.clear();
  }

  public void clearWagons() {
    this.wagons.clear();
  }

  public void clearCompositions() {
    this.compositions.clear();
  }

  public void clearTracks() {
    this.tracks.clear();
  }

  public void clearAll() {
    this.clearStations();
    this.clearWagons();
    this.clearCompositions();
    this.clearTracks();
  }

  public void printStats() {
    System.out.println("Stations: " + this.stations.size());
    System.out.println("Wagons: " + this.wagons.size());
    System.out.println("Compositions: " + this.compositions.size() + " ("
        + this.getCompositionsList().size() + ")");
    System.out.println("Tracks: " + this.tracks.size());
  }

}
