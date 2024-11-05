package hr.foi.jfletcher20.utils;

import hr.foi.jfletcher20.RailwaySingleton;

/// The product of this builder is the Railway singleton
public class RailwaySingletonBuilder implements IBuilder {

  public RailwaySingletonBuilder() {  }

  @Override
  public void buildPart() {
    if (RailwaySingleton.getInstance().getInitArgs() != null) {
      var initArgs = RailwaySingleton.getInstance().getInitArgs();
      FileLoader.loadFiles(initArgs);
    } else {
      System.out.println("Error: Singleton nema argumenata. Je li program pravilno pokrenut?");
    }
  }

  @Override
  public Object getResult() {
    return RailwaySingleton.getInstance();
  }

}
