package edu.unizg.foi.uzdiz.jfletcher20;

import edu.unizg.foi.uzdiz.jfletcher20.utils.FileLoader;
import edu.unizg.foi.uzdiz.jfletcher20.utils.RailwaySingletonBuilder;
import edu.unizg.foi.uzdiz.jfletcher20.utils.SystemInitializationDirector;

/*
 * Example run commands:
 * 
 * java -jar /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zs DZ_1_stanice.csv --zps DZ_1_vozila.csv --zk DZ_1_kompozicije.csv
 * java -jar /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zs DZ_1_stanice.csv --zk DZ_1_kompozicije.csv --zps DZ_1_vozila.csv
 * java -jar /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zps DZ_1_vozila.csv --zs DZ_1_stanice.csv --zk DZ_1_kompozicije.csv
 * java -jar /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zps DZ_1_vozila.csv --zk DZ_1_kompozicije.csv --zs DZ_1_stanice.csv
 * java -jar /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zk DZ_1_kompozicije.csv --zps DZ_1_vozila.csv --zs DZ_1_stanice.csv
 * java -jar /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zk DZ_1_kompozicije.csv --zs DZ_1_stanice.csv --zps DZ_1_vozila.csv
 * 
 * so the command has to give 3 pairs of: --filedatatype <file>
 * where ft is one of the following: zs, zps, zk
 * and <file> is the file name that is to be read
 * 
 * zs = željezničke stanice
 * zps = željeznička postrojenja
 * zk = željeznička kompozicija
 * 
 * Example data from each file:
 * 
 *  --zs [has 14 columns]
 *  Stanica;Oznaka pruge;Vrsta stanice;Status stanice;Putnici ul/iz;Roba ut/ist;Kategorija pruge;Broj perona;Vrsta pruge;Broj kolosjeka;DO po osovini;DO po duznom m;Status pruge;Dužina
 *  Kotoriba;M501;kol.;O;DA;DA;M;1;K;1;22,5;8,0;I;0
 *  Donji Mihaljevec;M501;staj.;O;DA;NE;M;1;K;1;22,5;8,0;I;7
 *  Donji Kraljevec;M501;kol.;O;DA;DA;M;1;K;1;22,5;8,0;I;6
 *  
 *  --zps [has 18 columns]
 *  Oznaka;Opis;Proizvođač;Godina;Namjena;Vrsta prijevoza;Vrsta pogona;Maks brzina;Maks snaga;Broj sjedećih mjesta;Broj stajaćih mjesta;Broj bicikala;Broj kreveta;Broj automobila;Nosivost;Površina;Zapremina;Status
 *  D2044-1;DIZELSKA LOKOMOTIVA serije 2 044 „Mala Karavela“ ili „Džems“;Đuro Đaković Hrvatska prema licenci General Motorsa USA ;1981;PSVPVK;N;D;120;1,7;0;0;0;0;0;0;0;0;I
 *  D2044-2;DIZELSKA LOKOMOTIVA serije 2 044 „Mala Karavela“ ili „Džems“;Đuro Đaković Hrvatska prema licenci General Motorsa USA ;1981;PSVPVK;N;D;120;1,7;0;0;0;0;0;0;0;0;K
 *  D2044-3;DIZELSKA LOKOMOTIVA serije 2 044 „Mala Karavela“ ili „Džems“;Đuro Đaković Hrvatska prema licenci General Motorsa USA ;1981;PSVPVK;N;D;120;1,7;0;0;0;0;0;0;0;0;K
 * 
 *  --zk [has 3 columns]
 *  Oznaka;Oznaka prijevoznog sredstva;Uloga
 *  8001;D2044-1;P
 *  
 * The first row of each file is the header and the rest of the rows are the data
 * Rows with status I are ispravna, Z i zatvorena, K is u kvaru
 * Rows that are completely empty should be skipped
 * Rows that start with # are comments and should be skipped
 * Rows that have a column with an empty value should print an error message and skip the row
 * 
 */

/**
 * Main class
 */
public class Main {
  public static void main(String[] args) {
    putArgsInSingleton(args);
    var initDirector = new SystemInitializationDirector(new RailwaySingletonBuilder());
    initDirector.construct();
  }
  
  private static void putArgsInSingleton(String[] args) {
    RailwaySingleton.getInstance().setInitArgs(args);
  }
}
