package hr.foi.jfletcher20.tracks;

import hr.foi.jfletcher20.utils.ICreator;
import hr.foi.jfletcher20.utils.IProduct;

public class TrainTrackCreator implements ICreator {

  public TrainTrackCreator() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public IProduct factoryMethod(String data) {
    System.out.println("TrainTrackCreator.factoryMethod() called");
    return null;
  }

}
