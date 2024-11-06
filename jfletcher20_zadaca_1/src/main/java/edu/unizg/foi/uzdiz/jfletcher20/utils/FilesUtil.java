package edu.unizg.foi.uzdiz.jfletcher20.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import edu.unizg.foi.uzdiz.jfletcher20.compositions.TrainCompositionCreator;
import edu.unizg.foi.uzdiz.jfletcher20.enums.FileType;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.stations.StationCreator;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;
import edu.unizg.foi.uzdiz.jfletcher20.tracks.TrainTrackCreator;
import edu.unizg.foi.uzdiz.jfletcher20.wagons.WagonCreator;

/*
 * Example run commands:
 * 
 * java -jar
 * /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zs
 * DZ_1_stanice.csv --zps DZ_1_vozila.csv --zk DZ_1_kompozicije.csv java -jar
 * /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zs
 * DZ_1_stanice.csv --zk DZ_1_kompozicije.csv --zps DZ_1_vozila.csv java -jar
 * /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zps
 * DZ_1_vozila.csv --zs DZ_1_stanice.csv --zk DZ_1_kompozicije.csv java -jar
 * /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zps
 * DZ_1_vozila.csv --zk DZ_1_kompozicije.csv --zs DZ_1_stanice.csv java -jar
 * /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zk
 * DZ_1_kompozicije.csv --zps DZ_1_vozila.csv --zs DZ_1_stanice.csv java -jar
 * /home/NWTiS_1/UZDIZ/design-patterns/jfletcher20_zadaca_1/target/jfletcher20_zadaca_1.jar --zk
 * DZ_1_kompozicije.csv --zs DZ_1_stanice.csv --zps DZ_1_vozila.csv
 * 
 * so the command has to give 3 pairs of: --filedatatype <file> where ft is one of the following:
 * zs, zps, zk and <file> is the file name that is to be read
 * 
 * zs = željezničke stanice zps = željeznička postrojenja zk = željeznička kompozicija
 * 
 * Example data from each file:
 * 
 * --zs [has 14 columns] Stanica;Oznaka pruge;Vrsta stanice;Status stanice;Putnici ul/iz;Roba
 * ut/ist;Kategorija pruge;Broj perona;Vrsta pruge;Broj kolosjeka;DO po osovini;DO po duznom
 * m;Status pruge;Dužina Kotoriba;M501;kol.;O;DA;DA;M;1;K;1;22,5;8,0;I;0 Donji
 * Mihaljevec;M501;staj.;O;DA;NE;M;1;K;1;22,5;8,0;I;7 Donji
 * Kraljevec;M501;kol.;O;DA;DA;M;1;K;1;22,5;8,0;I;6
 * 
 * --zps [has 18 columns] Oznaka;Opis;Proizvođač;Godina;Namjena;Vrsta prijevoza;Vrsta pogona;Maks
 * brzina;Maks snaga;Broj sjedećih mjesta;Broj stajaćih mjesta;Broj bicikala;Broj kreveta;Broj
 * automobila;Nosivost;Površina;Zapremina;Status D2044-1;DIZELSKA LOKOMOTIVA serije 2 044 „Mala
 * Karavela“ ili „Džems“;Đuro Đaković Hrvatska prema licenci General Motorsa USA
 * ;1981;PSVPVK;N;D;120;1,7;0;0;0;0;0;0;0;0;I D2044-2;DIZELSKA LOKOMOTIVA serije 2 044 „Mala
 * Karavela“ ili „Džems“;Đuro Đaković Hrvatska prema licenci General Motorsa USA
 * ;1981;PSVPVK;N;D;120;1,7;0;0;0;0;0;0;0;0;K D2044-3;DIZELSKA LOKOMOTIVA serije 2 044 „Mala
 * Karavela“ ili „Džems“;Đuro Đaković Hrvatska prema licenci General Motorsa USA
 * ;1981;PSVPVK;N;D;120;1,7;0;0;0;0;0;0;0;0;K
 * 
 * --zk [has 3 columns] Oznaka;Oznaka prijevoznog sredstva;Uloga 8001;D2044-1;P
 * 
 * The first row of each file is the header and the rest of the rows are the data Rows with status I
 * are ispravna, Z i zatvorena, K is u kvaru Rows that are completely empty should be skipped Rows
 * that start with # are comments and should be skipped Rows that have a column with an empty value
 * should print an error message and skip the row
 * 
 */

/**
 * FilesUtil class
 */
public abstract class FilesUtil {

  private static Pattern pattern = Pattern.compile(
      "^(--zs|--zps|--zk)\\s\\w+.csv (--zs|--zps|--zk)\\s\\w+.csv (--zs|--zps|--zk)\\s\\w+.csv$");
  private static Pattern zsHeaderPattern =
      Pattern.compile("^Stanica;Oznaka pruge;Vrsta stanice;Status stanice;"
          + "Putnici ul/iz;Roba ut/ist;Kategorija pruge;Broj perona;Vrsta pruge;"
          + "Broj kolosjeka;DO po osovini;DO po duznom m;Status pruge;Dužina$");
  private static Pattern zpsHeaderPattern =
      Pattern.compile("^Oznaka;Opis;Proizvođač;Godina;Namjena;Vrsta prijevoza;"
          + "Vrsta pogona;Maks brzina;Maks snaga;Broj sjedećih mjesta;"
          + "Broj stajaćih mjesta;Broj bicikala;Broj kreveta;Broj automobila;Nosivost;Površina;Zapremina;Status$");
  private static Pattern zkHeaderPattern =
      Pattern.compile("^Oznaka;Oznaka prijevoznog sredstva;Uloga$");

  public static boolean checkArgs(String[] args) {
    if (args.length != 6) {
      Logs.w("Argumenti nisu ispravno postavljeni. "
          + "Program očekuje 6 argumenata, a primljeno je: " + args.length);
      return false;
    }
    for (int i = 0; i < args.length; i += 2)
      for (int j = i + 2; j < args.length; j += 2)
        if (args[i].equals(args[j])) {
          Logs.w("Argumenti nisu ispravno postavljeni. "
              + "Argumenti ne smiju imati duplicirane tipove datoteka:  " + args[i] + " i "
              + args[j]);
          return false;
        }
    String argsString = String.join(" ", args);
    if (!pattern.matcher(argsString).matches()) {
      Logs.w("Argumenti nisu ispravno postavljeni. " + "Argumenti trebaju biti u obliku: "
          + "--tipDat1 <nazivCsvDat1> --tipDat2 <nazivCsvDat2> --tipDat3 <nazivCsvDat3>, "
          + "gdje su tipovi datoteke --zs, --zps ili --zk.");
      return false;
    }
    return true;
  }

  public static void loadFiles(String[] args) {
    Logs.i("Učitavanje datoteka...");
    if (!checkArgs(args)) {
      Logs.e("Neispravni argumenti za pokretanje programa.");
      return;
    }
    for (int i = 0; i < args.length; i += 2) {
      String fileName = args[i + 1];
      Path path = Path.of(fileName);
      FileType fileType = getFileType(path);
      boolean correctFileType = correctFileType(fileType, args[i]);
      if (!correctFileType) {
        Logs.e("Datoteka " + fileName + " nije ispravnog tipa: " + fileType);
        continue;
      }
      loadFile(path, fileType);
    }
  }

  public static void loadFile(Path path, FileType fileType) {
    try {
      var lines = Files.readAllLines(path);
      // skip the first line
      for (int i = 1; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.trim().startsWith("#")) {
          Logs.i(i, "Preskačem komentar: " + line);
          continue;
        } else if (line.trim().isBlank()) {
          Logs.i(i, "Preskačem prazan redak.");
          continue;
        } else if (line.contains(";;")) {
          Logs.e(i, "Redak sadrži praznu vrijednost na poziciji: " + line.indexOf(";;")
              + ", preskačem redak: " + line);
          continue;
        } else {
          createProduct(line, i, fileType);
          continue;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void createProduct(String data, int index, FileType fileType) {
    ICreator creator = null, altCreator = null;
    IProduct product = null, altProduct = null;
    if (fileType == FileType.ZS) {
      creator = new StationCreator();
      altCreator = new TrainTrackCreator();
    } else if (fileType == FileType.ZPS) {
      creator = new WagonCreator();
    } else if (fileType == FileType.ZK) {
      creator = new TrainCompositionCreator();
    } else {
      Logs.e(index, "Nepoznati tip datoteke: " + fileType);
      return;
    }
    try {
      product = creator.factoryMethod(data, index);
      if (altCreator != null)
        altProduct = altCreator.factoryMethod(data, index);
    } catch (Exception e) {
      Logs.e(index, "Greška " + e.getClass().getSimpleName() + "::" + e.getMessage()
          + " prilikom parsiranja retka: " + data);
      return;
    }
    if (product == null && altProduct == null) {
      emptyCol(index, data);
      return;
    }
    RailwaySingleton.getInstance().addProduct(product);
    RailwaySingleton.getInstance().addProduct(altProduct);
  }

  private static void emptyCol(int index, String data) {
    List<String> emptyColumns = new ArrayList<>();
    String[] parts = (data + " ").split(";");
    for (int i = 0; i < parts.length; i++) {
      Logs.i("parts[" + i + "/" + (parts.length - 1) + "].trim(): " + parts[i].trim());
      if (parts[i].trim().isBlank() || parts[i].trim().isEmpty() || parts[i].trim().equals(""))
        emptyColumns.add(String.valueOf(i));
    }
    Logs.e(index, "Nije moguće parsirati redak zbog null podataka na pozicijama: "
        + emptyColumns.toString() + ": " + data);
  }

  public static boolean correctFileType(FileType fileType, String arg) {
    final String prefix = "--";
    return arg.startsWith(prefix + fileType.name().toLowerCase());
  }

  public static FileType getFileType(Path filePath) {
    try {
      var contents = Files.readString(filePath);
      String header = contents.split("\n")[0].replaceAll("\r", "").trim();
      if (header.startsWith("\uFEFF"))
        header = header.substring(1);
      if (zsHeaderPattern.matcher(header.trim()).find())
        return FileType.ZS;
      else if (zpsHeaderPattern.matcher(header).find())
        return FileType.ZPS;
      else if (zkHeaderPattern.matcher(header).find())
        return FileType.ZK;
    } catch (Exception e) {
      Logs.e("Datoteka " + filePath + " nema validan format zaglavlja (provg retka).");
    }
    return null;
  }

}
