package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackCategory;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;

public class TrainTrackCreator implements ICreator {

  private static int columnCount = 17;

  public TrainTrackCreator() {
  }

  @Override
  public TrainTrack factoryMethod(String data, int row) {
    if (data == null || data.isEmpty()) {
      Logs.w(row, "TrainTrackCreator Prazan redak.");
      return null;
    } else if (data.split(";", -1).length != columnCount) {
      Logs.w(row, columnCountError(data.split(";", -1).length));
      return null;
    }
    String[] parts = data.split(";", -1);
    return new TrainTrack(parts[1], // oznaka
        TrainTrackCategory.fromCSV(parts[6]), // kategorija
        parts[8], // vrsta prijevoza
        ParsingUtil.i(parts[9]), // broj kolosjeka
        ParsingUtil.d(parts[13]), // duzina
        ParsingUtil.d(parts[10]), // DO po osovini
        ParsingUtil.d(parts[11]), // DO po duznom m
        TrainTrackStatus.fromCSV(parts[12]) // status
    );
  }

  private String columnCountError(int counts) {
    return "TrainTrackCreator Ocekivano " + columnCount + " stupaca, otkriveno " + counts;
  }

}
