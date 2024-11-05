package hr.foi.jfletcher20;

import java.util.ArrayList;
import java.util.List;
import hr.foi.jfletcher20.compositions.TrainComposition;
import hr.foi.jfletcher20.stations.Station;
import hr.foi.jfletcher20.tracks.TrainTrack;
import hr.foi.jfletcher20.utils.IProduct;
import hr.foi.jfletcher20.wagons.Wagon;

public class RailwaySingleton {

  static private volatile RailwaySingleton instance = new RailwaySingleton();
  
  private List<Station> stations = new ArrayList<>();
  private List<Wagon> wagons = new ArrayList<>();
  private List<TrainComposition> compositions = new ArrayList<>();
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
  
  public List<TrainComposition> getCompositions() {
    return this.compositions;
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
    this.compositions.add(composition);
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
  
  public void removeComposition(TrainComposition composition) {
    this.compositions.remove(composition);
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

}
