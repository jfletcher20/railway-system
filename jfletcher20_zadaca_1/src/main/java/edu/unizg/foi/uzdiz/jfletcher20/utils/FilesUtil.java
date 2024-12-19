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
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleCreator;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule_days.ScheduleDaysCreator;
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
      "^(--zs|--zps|--zk|--zvr|--zod)\\s\\w+.csv (--zs|--zps|--zk|--zvr|--zod)\\s\\w+.csv (--zs|--zps|--zk|--zvr|--zod)\\s\\w+.csv " // DZ-1
          + "(--zs|--zps|--zk|--zvr|--zod)\\s\\w+.csv (--zs|--zps|--zk|--zvr|--zod)\\s\\w+.csv$"); // DZ-2
  private static Pattern zsHeaderPattern = Pattern.compile( //
      "^Stanica;Oznaka pruge;Vrsta stanice;Status stanice;Putnici ul/iz;"
          + "Roba ut/ist;Kategorija pruge;Broj perona;Vrsta pruge;Broj kolosjeka;"
          + "DO po osovini;DO po duznom m;Status pruge;Dužina;"
          + "Vrijeme normalni vlak;Vrijeme ubrzani vlak;Vrijeme brzi vlak$");
  private static Pattern zpsHeaderPattern = Pattern.compile( //
      "^Oznaka;Opis;Proizvođač;Godina;Namjena;Vrsta prijevoza;"
          + "Vrsta pogona;Maks brzina;Maks snaga;Broj sjedećih mjesta;"
          + "Broj stajaćih mjesta;Broj bicikala;Broj kreveta;Broj automobila;"
          + "Nosivost;Površina;Zapremina;Status$");
  private static Pattern zkHeaderPattern = Pattern.compile("^Oznaka;Oznaka prijevoznog sredstva;Uloga$");
  private static Pattern zvrHeaderPattern = Pattern.compile( //
      "^Oznaka pruge;Smjer;Polazna stanica;Odredišna stanica;Oznaka vlaka;"
          + "Vrsta vlaka;Vrijeme polaska;Trajanje vožnje;Oznaka dana$");
  private static Pattern zodHeaderPattern = Pattern.compile( //
      "^Oznaka dana;Dani vožnje$");

  public static boolean checkArgs(List<String> args) {
    if (args.size() != 10) {
      Logs.e("Argumenti nisu ispravno postavljeni. "
          + "Program očekuje 10 argumenata, a primljeno je: " + args.size());
      return false;
    }
    for (int i = 0; i < args.size(); i += 2)
      for (int j = i + 2; j < args.size(); j += 2)
        if (args.get(i).equals(args.get(j))) {
          Logs.w("Argumenti nisu ispravno postavljeni. "
              + "Argumenti ne smiju imati duplicirane tipove datoteka:  " + args.get(i) + " i "
              + args.get(j));
          return false;
        }
    String argsString = String.join(" ", args);
    if (!pattern.matcher(argsString).matches()) {
      Logs.e("Argumenti nisu ispravno postavljeni. " + "Argumenti trebaju biti u obliku: "
          + "--tipDat1 <nazivCsvDat1> --tipDat2 <nazivCsvDat2> --tipDat3 <nazivCsvDat3> "
          + "--tipDat4 <nazivCsvDat4> --tipDat5 <nazivCsvDat5>"
          + "gdje su tipovi datoteke --zs, --zps, --zk, --zvr, --zod");
      return false;
    }
    return true;
  }

  public static boolean loadFiles(List<String> args) {
    Logs.i("Učitavanje datoteka...");
    if (!checkArgs(args)) {
      Logs.e("Neispravni argumenti za pokretanje programa.");
      return false;
    }
    for (int i = 0; i < args.size(); i += 2) {
      String fileName = args.get(i + 1);
      Path path = Path.of(fileName);
      FileType fileType = getFileType(path);
      Logs.i("Datoteka: " + fileName + " tipa: " + fileType);
      boolean correctFileType = correctFileType(fileType, args.get(i));
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
      for (int i = 1; i < lines.size(); i++) {
        String line = lines.get(i);
        if (line.trim().startsWith("#")) {
          Logs.i(i, "Preskačem komentar: " + line);
          continue;
        } else if (line.trim().isBlank()) {
          Logs.i(i, "Preskačem prazan redak.");
          continue;
        } else if (line.trim().replaceAll(";", "").isEmpty()) {
          Logs.i(i, "Sadržaj stupaca retka je prazan. Preskačem redak: " + line);
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
    } else if (fileType == FileType.ZVR) {
      creator = new ScheduleCreator();
    } else if (fileType == FileType.ZOD) {
      creator = new ScheduleDaysCreator();
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
      else if (zvrHeaderPattern.matcher(header).find())
        return FileType.ZVR;
      else if (zodHeaderPattern.matcher(header).find())
        return FileType.ZOD;
    } catch (Exception e) {
      Logs.e("Datoteka " + filePath + " nema validan format zaglavlja (provg retka).");
    }
    return null;
  }

}
