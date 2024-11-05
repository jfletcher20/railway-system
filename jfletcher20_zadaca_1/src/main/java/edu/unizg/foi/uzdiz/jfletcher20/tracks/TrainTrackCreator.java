package edu.unizg.foi.uzdiz.jfletcher20.tracks;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackCategory;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;

public class TrainTrackCreator implements ICreator {

  private static int columnCount = 14;
  public TrainTrackCreator() {}

  @Override
  public TrainTrack factoryMethod(String data) {
    
    if (data == null || data.isEmpty()) {
      System.out.println("Error: Prazan redak");
      return null;
    } else if (data.split(";").length != 14) {
      System.out.println(columnCountError(data.split(";").length));
      return null;
    }
    String[] parts = data.split(";");
    return new TrainTrack(parts[1], // oznaka
        TrainTrackCategory.fromCSV(parts[6]), // kategorija
        parts[8], // vrsta prijevoza
        Integer.parseInt(parts[9]), // broj kolosjeka
        ParsingUtil.d(parts[10]), // DO po osovini
        ParsingUtil.d(parts[11]), // DO po duznom m
        TrainTrackStatus.fromCSV(parts[12]) // status
    );
  }

  private String columnCountError(int counts) {
    return "Error: TrainTrackCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
  }
  
}
