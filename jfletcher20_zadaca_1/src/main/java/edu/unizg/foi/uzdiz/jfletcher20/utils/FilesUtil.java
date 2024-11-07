package edu.unizg.foi.uzdiz.jfletcher20.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import edu.unizg.foi.uzdiz.jfletcher20.enums.FileType;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ICreator;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainCompositionCreator;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackCreator;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationCreator;
import edu.unizg.foi.uzdiz.jfletcher20.models.wagons.WagonCreator;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

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

  public static boolean loadFiles(String[] args) {
    Logs.i("Učitavanje datoteka...");
    if (!checkArgs(args)) {
      Logs.e("Neispravni argumenti za pokretanje programa.");
      return false;
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
    return true;
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
    RailwaySingleton.getInstance().addProduct(product, altProduct);
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
