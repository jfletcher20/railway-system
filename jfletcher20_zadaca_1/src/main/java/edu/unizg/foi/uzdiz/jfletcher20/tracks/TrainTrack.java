package edu.unizg.foi.uzdiz.jfletcher20.tracks;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackCategory;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainTrackStatus;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;

// Željeznička pruga između dviju željezničkih stanica ima određene osobine: oznaka, kategorija
// (lokalna, regionalna, međunarodna), način prijevoza (klasično (ugljen, dizel, baterije) ili
// električna struja (podrazumijeva da može i klasično)), broj kolosijeka (jedan ili dva), dužina
// (0-999 km), dopušteno opterećenje po osovini (10-50 t/os), dopušteno opterećenje po dužnom metru
// (2-10 t/m), status (ispravna, u kvaru, zatvorena)

/**
 * Train track object represents a train track.
 * 
 * @param id Track code
 * @param category Track category
 * @param transportType Type of transport
 * @param trackCount Number of tracks
 * @param axleLoad Axle load
 * @param length Length of track
 * @param status Track status
 */
public record TrainTrack(String id, // oznaka pruge
    TrainTrackCategory category, // kategorija pruge
    String transportType, // vrsta prijevoza
    int trackCount, // broj kolosjeka
    double axleLoad, // DO po osovini
    double linearLoad, // DO po duznom m
    TrainTrackStatus status // status pruge
) implements IProduct {
  public TrainTrack {
    if (id == null || id.isEmpty())
      throw new IllegalArgumentException("Oznaka pruge ne smije biti prazna.");
    if (category == null)
      throw new IllegalArgumentException("Kategorija pruge ne smije biti prazna.");
    if (transportType == null || transportType.isEmpty())
      throw new IllegalArgumentException("Vrsta prijevoza ne smije biti prazna.");
    if (trackCount < 1)
      throw new IllegalArgumentException("Broj kolosjeka ne smije biti manji od 1.");
    if (axleLoad < 10 || axleLoad > 50)
      throw new IllegalArgumentException("DO po osovini mora biti između 10 i 50 t/os.");
    if (linearLoad < 2 || linearLoad > 10)
      throw new IllegalArgumentException("DO po dužnom metru mora biti između 2 i 10 t/m.");
    if (status == null)
      throw new IllegalArgumentException("Status pruge mora biti definiran.");
  }
}

/*
 * example CSV file data: --zs [has 14 columns] Stanica;Oznaka pruge;Vrsta stanice;Status
 * stanice;Putnici ul/iz;Roba ut/ist;Kategorija pruge;Broj perona;Vrsta pruge;Broj kolosjeka;DO po
 * osovini;DO po duznom m;Status pruge;Dužina Kotoriba;M501;kol.;O;DA;DA;M;1;K;1;22,5;8,0;I;0 Donji
 * Mihaljevec;M501;staj.;O;DA;NE;M;1;K;1;22,5;8,0;I;7 Donji
 * Kraljevec;M501;kol.;O;DA;DA;M;1;K;1;22,5;8,0;I;6
 */
