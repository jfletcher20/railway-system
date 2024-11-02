package hr.foi.jfletcher20.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import hr.foi.jfletcher20.enums.FileType;

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
 * FileLoader class
 */
public abstract class FileLoader {

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
  private static Pattern zkHeaderPattern = Pattern.compile("^Oznaka;Oznaka prijevoznog sredstva;Uloga$");
  
  public static boolean checkArgs(String[] args) {
    if (args.length != 6) {
      System.out.println("Invalidan broj argumenata. Treba biti 6, a uneseno je: " + args.length);
      return false;
    }
    // ensure there are no duplicate args
    for (int i = 0; i < args.length; i += 2) {
      for (int j = i + 2; j < args.length; j += 2) {
        if (args[i].equals(args[j])) {
          System.out.println("Argumenti nisu ispravno postavljeni. "
              + "Argumenti ne smiju imati duplicirane tipove datoteka:  " + args[i] + " i "
              + args[j]);
          return false;
        }
      }
    }
    String argsString = String.join(" ", args);
    if (!pattern.matcher(argsString).matches()) {
      System.out.println("Argumenti nisu ispravno postavljeni. "
          + "Argumenti trebaju biti u obliku: "
          + "--tipDat1 <nazivCsvDat1> --tipDat2 <nazivCsvDat2> --tipDat3 <nazivCsvDat3>, "
          + "gdje su tipovi datoteke --zs, --zps ili --zk.");
      return false;
    }
    return true;
  }

  public static void loadFiles(String[] args) {
    System.out.println("Priprema sustava...");
    if (!checkArgs(args)) {
      System.out.println("Argumenti nisu validni.");
      System.out.println("Prekidanje...");
      return;
    }
    for (int i = 0; i < args.length; i += 2) {
      String fileName = args[i + 1];
      Path path = Path.of(fileName);
      FileType fileType = getFileType(path);
      boolean correctFileType = correctFileType(fileType, args[i]);
      if (!correctFileType) {
        System.out.println("Datoteka " + fileName + " ne odgovara uzorku za tip " + args[i]);
        continue;
      }
      if (fileType == FileType.ZS) {
        loadZsFile(path);
      } else if (fileType == FileType.ZPS) {
        loadZpsFile(path);
      } else if (fileType == FileType.ZK) {
        loadZkFile(path);
      }
    }
  }

  private static void loadZsFile(Path file) {
    System.out.println("Učitavanje željezničkih stanica...");
  }

  private static void loadZpsFile(Path file) {
    System.out.println("Učitavanje željezničkih postrojenja...");
  }

  private static void loadZkFile(Path file) {
    System.out.println("Učitavanje željezničkih kompozicija...");
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
//      System.out.println("Header after cleaning: " + header);
      if (zsHeaderPattern.matcher(header.trim()).matches()) {
        return FileType.ZS;
      } else if (zpsHeaderPattern.matcher(header).matches()) {
        return FileType.ZPS;
      } else if (zkHeaderPattern.matcher(header).matches()) {
        return FileType.ZK;
      }
    } catch (Exception e) {
      System.out.println("Greška prilikom čitanja datoteke: " + filePath);
    }
    return null;
  }

}
