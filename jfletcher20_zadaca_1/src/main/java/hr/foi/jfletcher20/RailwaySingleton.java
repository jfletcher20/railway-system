package hr.foi.jfletcher20;

public class RailwaySingleton {

  static private volatile RailwaySingleton instance = new RailwaySingleton();

  private RailwaySingleton() {
    System.out.println("Singleton created");
  }

  static RailwaySingleton getInstance() {
    return instance;
  }

}
