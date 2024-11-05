package hr.foi.jfletcher20.compositions;

import hr.foi.jfletcher20.utils.ICreator;
import hr.foi.jfletcher20.utils.IProduct;

public class TrainCompositionCreator implements ICreator {

  public TrainCompositionCreator() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public IProduct factoryMethod(String data) {
    System.out.println("TrainCompositionCreator.factoryMethod() called");
    return null;
  }

}
