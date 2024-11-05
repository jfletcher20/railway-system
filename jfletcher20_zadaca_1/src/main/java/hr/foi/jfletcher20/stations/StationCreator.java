package hr.foi.jfletcher20.stations;

import hr.foi.jfletcher20.utils.ICreator;
import hr.foi.jfletcher20.utils.IProduct;

public class StationCreator implements ICreator {

  public StationCreator() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public IProduct factoryMethod(String data) {
    System.out.println("StationCreator.factoryMethod() called");
    return null;
  }

}
