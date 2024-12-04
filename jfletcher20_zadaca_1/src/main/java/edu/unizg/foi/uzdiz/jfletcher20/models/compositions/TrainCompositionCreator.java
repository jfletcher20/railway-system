package edu.unizg.foi.uzdiz.jfletcher20.models.compositions;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class TrainCompositionCreator implements ICreator {

  private static int columnCount = 3;

  public TrainCompositionCreator() {}

  @Override
  public IProduct factoryMethod(String data, int row) {
    
    if (data == null || data.isEmpty()) {
      Logs.w(row, "TrainCompositionCreator Prazan redak.");
      return null;
    } else if (data.split(";").length != 3) {
      Logs.w(row, columnCountError(data.split(";").length));
      return null;
    }
    
    String[] parts = data.split(";");
    return new TrainComposition(Integer.parseInt(parts[0]), // ID
        parts[1], // wagonID
        parts[2] // role
    );
    
  }

  private String columnCountError(int counts) {
    return "TrainCompositionCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
  }

}
