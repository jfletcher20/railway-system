package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.unizg.foi.uzdiz.jfletcher20.Main;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;

/*
 * 
 * 
 * ● Pregled vlakova (voznog reda) kojima se može putovati od jedne željezničke stanice do 
druge željezničke stanice na određen dan u tjednu unutar zadanog vremena 
○ Sintaksa:  
■ IVI2S polaznaStanica - odredišnaStanica - dan - odVr - doVr - prikaz 
○ Primjeri:  
■ IVI2S Donji Kraljevec - Čakovec - N - 0:00 - 23:59 - SPKV 
■ IVI2S Donji Kraljevec - Novi Marof - Pe - 08:00 - 16:00 - KPSV 
■ IVI2S Donji Kraljevec - Ludbreg - Su - 5:20 - 20:30 - VSPK 
○ Opis primjera:  
■ Ispis tablice sa željezničkim stanicama između dviju željezničkih stanica, s 
brojem kilometara, vremenima polaska vlakova sa željezničkih stanica. 
Prikazuju se samo oni vlakovi koji prometuju na određeni dan i čije je 
vrijeme polaska s polazne željezničke stanice nakon odVr vremena i 
vrijeme dolaska u odredišnu željezničku stanicu prije doVr vremena. 
Podaci se prikazuju u stupcima čiji redoslijed je proizvoljan i stupcima se 
mogu ponavljati. S označava naziv željezničke stanice, P označava prugu, 
K označava broj km od polazne željezničke stanice, V označava vrijeme 
polaska određenog vlaka sa željezničke stanice. V se odnosi na jedan ili 
više stupaca. Potrebno je prilagoditi ispis zaglavlja i redova zadanom 
prikazu. Osim gornjih primjera prikaza mogu biti i drugi prikazi kao npr: SPV 
(nema prikaza broj kilometara), KPSVK (broj kilometara se prikazuje u 
prvom i posljednjem stupcu). U stupcu pojedinog vlaka ispisuje se vrijeme 
polaska sa željezničke stanice. U 1. primjeru su stanice koje su na istoj 
pruzi, na 2. primjeru su željezničke stanice koje su na dvije pruge, a na 3. 
primjeru su željezničke stanice koje su na tri pruge. Vlakovi se ispisuju u 
kronološkom redoslijedu vremena polaska vlaka s njegove polazne 
željezničke stanice. Slika 1 prikazuje djelomični izvod iz voznog reda od 
željezničke stanice Zabok do druge željezničke stanice Gornja Stubica za 
ponedjeljak od vremena 5:00 do vremena 12:00 uz oznake KSV. Na slici 
treba zanemariti oznake dana u tjednu. 
 * 
 * Ispis tablice ima stupce i retke zamijenjeno, npr. (ovo nije primjer ispisa za ovaj zadatak, samo primjer kako izgleda dok su zamijenjeni stupci)
 * 
 *
 * Udaljenost od pocetne stanice | od               | 3210 | 3212 | 3214 | 3216 | 3218
 . ----------------------------- | ---------------- | ---- | ---- | ---- | ---- | -----
 .                         0     | Zabok            | 5:10 | 6:10 | 7:46 | 8:47 | 10:51
 .                         2     | Hum Lug          | 5:13 | 6:13 | 7:49 | 8:50 | 10:54
 .                         4     | Oroslavje        | 5:17 | 6:17 | 7:53 | 8:54 | 10:58
 .                         7     | Stubi?ke Toplice | 5:22 | 6:22 | 7:58 | 8:59 | 11:03
 .                         9     | Donja Stubica    | 5:26 | 6:26 | 8:02 | 9:03 | 11:07
 .                         12    | Gornja Stubica   | 5:31 | 6:31 | 8:07 | 9:08 | 11:12
 *
 * 
 */

/**
 * The CommandSystem class is responsible for handling the command system of the
 * program.
 * 
 * <p>
 * After all necessary files are loaded, the program must be prepared to execute
 * commands in
 * interactive mode. During the program's operation, multiple commands can be
 * executed until the
 * command Q is entered.
 * </p>
 * 
 */
public class CommandSystemSingleton {

  Pattern quitPattern = Pattern.compile("^Q$");
  Pattern viewTracksPattern = Pattern.compile("^IP$");
  Pattern viewStationsPattern = Pattern.compile( //
      "^ISP (?<trackCode>[A-Za-z0-9]+) (?<order>[NO])$" //
  );
  Pattern viewStationsBetweenPattern = Pattern.compile( //
      "^ISI2S (?<startStation>.+) - (?<endStation>.+)$" //
  );
  Pattern viewCompositionPattern = Pattern.compile( //
      "^IK (?<compositionCode>[0-9]+)$" //
  );

  Pattern viewTrainsPattern = Pattern.compile("^IV$");
  Pattern viewTrainStagesPattern = Pattern.compile("^IEV (?<trainCode>.+)$");
  Pattern viewTrainsWithStagesOnPattern = Pattern.compile("^IEVD (?<days>[A-Za-z]+)$");
  Pattern viewTrainTimetablePattern = Pattern.compile("^IVRV (?<trainCode>.+)$");
  // Pattern trainScheduleBetweenStationsPattern = Pattern.compile(
  // "^IVI2S (?<startStation>.+) - (?<endStation>.+) - (?<days>[A-Za-z]+) -
  // (?<fromTime>\\d+:\\d+) - (?<toTime>\\d+:\\d+) - (?<display>"
  // + /* any combination of S, P, K, V */ "(?=.*[SPVK])[SPVK]"
  // + ")$");
  private Pattern trainScheduleBetweenStationsPattern = Pattern.compile(
      "^IVI2S\\s+([^-]+)\\s*-\\s*([^-]+)\\s*-\\s*([^-]+)\\s*-\\s*([^-]+)\\s*-\\s*([^-]+)\\s*-\\s*([^-]+)$");

  Pattern viewUsersPattern = Pattern.compile("^PK$");
  Pattern addUserPattern = // Pattern.compile("^DK (?<name>.+) (?<lastName>\\S+)$");
      Pattern.compile("^DK (?<name>[\\p{L}]+(?: [\\p{L}]+)*) (?<lastName>[\\p{L}]+)$");

  Pattern addTrainObserverPattern = Pattern.compile(
      "^DPK (?<name>[\\p{L}]+(?: [\\p{L}]+)*) (?<lastName>[\\p{L}]+) - (?<trainId>[^-]+?)( - (?<station>.+))?$");
  Pattern linkPattern = Pattern
      .compile(
          "^LINK (?<name>[\\p{L}]+(?: [\\p{L}]+)*) (?<lastName>[\\p{L}]+) - (?<groupId>.+) - (?<action>.+)$|^LINK PREGLED$");
  private final ChatMediator userChat = new ChatMediator();

  public ChatMediator getUserChat() {
    return userChat;
  }

  public static CommandSystemSingleton instance = new CommandSystemSingleton();

  private CommandSystemSingleton() {
  }

  public static CommandSystemSingleton getInstance() {
    return instance;
  }

  public void startCommandSystem() {
    outputMenu();
    while (true) {
      Logs.withPadding(() -> {
        System.out.print("     < ");
      }, true, false);
      String command = System.console().readLine();
      if (quitPattern.matcher(command).matches()) {
        Logs.c("Prekidanje programa...");
        break;
      }
      if (Main.debugMode)
        runDebugTests(command);
      else
        identifyCommand(command);
    }
    Logs.footer(true);
  }

  public void runCommand(String command) {
    identifyCommand(command);
  }

  private void runDebugTests(String command) {
    if (command.trim().equalsIgnoreCase("All")) {
      String[] commands = new String[] {
          "IP", "ISP M501 N", "ISP M501 O",
          "ISI2S Kotoriba - Ludbreg",
          "ISI2S Ludbreg - Kotoriba",
          "ISI2S Kotoriba - Macinec",
          "ISI2S Macinec - Kotoriba",
          "IK 8001",
          "IK 1",
          "IV",
          "IEV 3609",
          "IEV 0",
          "IEV 3301",
          "IEVD PoSrPeN",
          "IEVD Po",
          "IEVD PoUSrČPeSuN",
          "IVRV 3609",
          "IVRV 0",
          "IVRV 3301",
          "IVRV 3302",
          "IVRV 991",
          "IVRV B 791",
          "DK Pero Kos",
          "DK Joshua Lee Fletcher",
          "PK",
      };
      for (String c : commands) {
        Logs.c("Izvršavanje komande: " + c);
        identifyCommand(c);
      }
    } else if (command.trim().equalsIgnoreCase("All1")) {
      String[] commands = new String[] {
          "IP", "ISP M501 N",
          "ISI2S Kotoriba - Ludbreg",
          "IK 8001",
          "IV",
          "IEV 3301",
          "IEVD PoSrPeN",
          "IVRV 3302",
          "DK Pero Kos",
          "PK",
      };
      for (String c : commands) {
        Logs.c("Izvršavanje komande: " + c);
        identifyCommand(c);
      }
    } else {
      identifyCommand(debugCommands(command));
    }
  }

  private String debugCommands(String command) {
    return switch (command.trim().toLowerCase()) {
      case "a" -> "IP";
      case "b" -> "ISP M501 N";
      case "b2" -> "ISP M501 O";
      case "c" -> "ISI2S Kotoriba - Ludbreg";
      case "c2" -> "ISI2S Ludbreg - Kotoriba";
      case "c3" -> "ISI2S Kotoriba - Macinec";
      case "c4" -> "ISI2S Macinec - Kotoriba";
      case "d" -> "IK 8001";
      case "d2" -> "IK 1";
      case "e" -> "IV";
      case "f" -> "IEV 3609";
      case "f2" -> "IEV 0";
      case "g" -> "IEVD PoSrPeN";
      case "g2" -> "IEVD Po";
      case "g3" -> "IEVD PoUSrPeSuN";
      case "h" -> "IVRV 3609";
      case "h2" -> "IVRV 0";
      case "h3" -> "IVRV 3301";
      case "h4" -> "IVRV 3302";
      case "h5" -> "IVRV 991";
      case "h6" -> "IVRV B 791";
      case "i" -> "DK Pero Kos";
      case "i2" -> "DK Joshua Lee Fletcher";
      case "j" -> "PK";
      default -> command;
    };
  }

  private boolean identifyCommand(String command) {
    Matcher vtMatcher = viewTracksPattern.matcher(command);
    Matcher vsMatcher = viewStationsPattern.matcher(command);
    Matcher vsbMatcher = viewStationsBetweenPattern.matcher(command);
    Matcher vcMatcher = viewCompositionPattern.matcher(command);
    Matcher addUserMatcher = addUserPattern.matcher(command);
    Matcher viewUsersMatcher = viewUsersPattern.matcher(command);
    Matcher viewTrainsMatcher = viewTrainsPattern.matcher(command);
    Matcher viewTrainStagesMatcher = viewTrainStagesPattern.matcher(command);
    Matcher viewTrainsWithStagesOnMatcher = viewTrainsWithStagesOnPattern.matcher(command);
    Matcher ivrvMatcher = viewTrainTimetablePattern.matcher(command);
    Matcher linkMatcher = linkPattern.matcher(command);
    Matcher addTrainObserverPatternMatcher = addTrainObserverPattern.matcher(command);
    Matcher trainScheduleBetweenStationsMatcher = trainScheduleBetweenStationsPattern.matcher(command);
    if (vtMatcher.matches()) {
      viewTracks();
    } else if (vsMatcher.matches()) {
      viewStations(vsMatcher.group("trackCode"), vsMatcher.group("order"));
    } else if (vsbMatcher.matches()) {
      viewStationsBetween(vsbMatcher.group("startStation"), vsbMatcher.group("endStation"));
    } else if (addUserMatcher.matches()) {
      addUser(addUserMatcher.group("name"), addUserMatcher.group("lastName"));
    } else if (viewUsersMatcher.matches()) {
      viewUsers();
    } else if (viewTrainsMatcher.matches()) {
      viewTrains();
    } else if (linkMatcher.matches()) {
      processLinkCommand(command);
    } else if (viewTrainStagesMatcher.matches()) {
      viewTrainStagesOfTrain(viewTrainStagesMatcher.group("trainCode"));
    } else if (ivrvMatcher.matches()) {
      viewTrainTimetable(ivrvMatcher.group("trainCode"));
    } else if (addTrainObserverPatternMatcher.matches()) {
      addTrainObserver(command);
    } else if (trainScheduleBetweenStationsMatcher.matches()) {
      viewTrainScheduleBetweenStations(trainScheduleBetweenStationsMatcher);
    } else if (viewTrainsWithStagesOnMatcher.matches()) {
      try {
        viewTrainsWithStagesOnDays(Weekday.daysFromString(viewTrainsWithStagesOnMatcher.group("days")));
      } catch (IllegalArgumentException e) {
        Logs.e(e.getMessage() + " - Nepoznata oznaka dana: " + viewTrainsWithStagesOnMatcher.group("days"));
      }
    } else if (vcMatcher.matches()) {
      try {
        viewComposition(ParsingUtil.i(vcMatcher.group("compositionCode")));
      } catch (NumberFormatException e) {
        Logs.e("Neispravna oznaka kompozicije: " + vcMatcher.group("compositionCode"));
      }
    } else {
      Logs.c("Nepoznata komanda.");
      outputMenu();
      return false;
    }
    return true;
  }

  private void outputMenu() {
    Logs.header("JLF Željeznica: Interaktivni način rada", true);
    Logs.withPadding(() -> Logs.o("Validne komande:"), false, true);
    Logs.o("IP\t\t\t\t\t- Pregled pruga", false);
    Logs.o(
        "ISP [oznakaPruge] [N|O]\t\t\t- Pregled stanica uz prugu u normalnom ili obrnutom redoslijedu",
        false);
    Logs.o("ISI2S [nazivStanice1] - [nazivStanice2]\t- Pregled stanica između dvije stanice",
        false);
    Logs.o("IK [oznakaKompozicije]\t\t\t- Pregled kompozicija", false);
    Logs.o("IV\t\t\t\t\t- Pregled vlakova", false);
    Logs.o("IEV [oznakaVlaka]\t\t\t\t- Pregled etapa vlaka", false);
    Logs.o("IEVD [dani]\t\t\t\t- Pregled vlakova koji voze sve etape na određene dane u tjednu",
        false);
    Logs.o("IVRV [oznakaVlaka]\t\t\t- Pregled vlakova i njihovih etapa", false);
    Logs.o("IVI2S [polaznaStanica] - [odredišnaStanica] - [dan] - [odVr] - [doVr] - [prikaz]", false);
    Logs.o("\t\t\t\t\t\t- Pregled vlakova između dvije stanice na određeni dan u tjednu unutar zadanog vremena", false);

    Logs.o("DK [ime] [prezime]\t\t\t- Dodavanje korisnika", false);
    Logs.o("PK\t\t\t\t\t- Pregled korisnika", false);
    Logs.withPadding(() -> Logs.o(
        "DPK [ime] [prz] - [oznVlaka] [- stanica]  - Dodavanje korisnika za praćenje putovanja vlaka ili dolaska u određenu željezničku stanicu",
        false), true, false);
    Logs.withPadding(() -> Logs.o(
        "LINK [ime] [prz] - [grupa] - [O|Z|poruka] - Otvori/zatvori vezu između korisnika i grupe ili pošalji obavijest u grupu",
        false), true, false);
    Logs.o("LINK PREGLED\t\t\t\t- Pregled svih grupa", false);
    Logs.withPadding(() -> Logs.o("Q - Izlaz iz programa", false), true, true);
    Logs.o("Uzorci dizajna, 2024. - Joshua Lee Fletcher");
  }

  public static void outputDebugMenu() {
    if (Main.debugMode) {
      Logs.o("\t\t[DEBUG] All\t\t\t- Za potpuno testiranje cijelog sustava", false);
      Logs.o("\t\t[DEBUG] All1\t\t\t- Za testiranje svake naredbe samo jednom", false);
      Logs.o("\t\t[DEBUG] a\t\t\t\t- Pregled pruga", false);
      Logs.o("\t\t[DEBUG] b\t\t\t\t- Pregled stanica uz prugu (normalni redoslijed)", false);
      Logs.o("\t\t[DEBUG] b2\t\t\t\t- Pregled stanica uz prugu (obrnuti redoslijed)", false);
      Logs.o("\t\t[DEBUG] c\t\t\t\t- Pregled stanica između Kotoriba - Ludbreg", false);
      Logs.o("\t\t[DEBUG] c2\t\t\t\t- Pregled stanica između Ludbreg - Kotoriba", false);
      Logs.o("\t\t[DEBUG] c3\t\t\t\t- Pregled stanica između Kotoriba - Macinec", false);
      Logs.o("\t\t[DEBUG] c4\t\t\t\t- Pregled stanica između Macinec - Kotoriba", false);
      Logs.o("\t\t[DEBUG] d\t\t\t\t- Pregled kompozicija (oznaka 8001)", false);
      Logs.o("\t\t[DEBUG] d2\t\t\t\t- Pregled kompozicija (oznaka 1)", false);
      Logs.o("\t\t[DEBUG] e\t\t\t\t- Pregled vlakova", false);
      Logs.o("\t\t[DEBUG] f\t\t\t\t- Pregled etapa vlaka", false);
      Logs.o("\t\t[DEBUG] f2\t\t\t\t- Pregled etapa vlaka", false);
      Logs.o("\t\t[DEBUG] g\t\t\t\t- Pregled vlakova koji voze sve etape na PoSrPeN", false);
      Logs.o("\t\t[DEBUG] g2\t\t\t\t- Pregled vlakova koji voze sve etape na Po", false);
      Logs.o("\t\t[DEBUG] g3\t\t\t\t- Pregled vlakova koji voze sve etape na PoUSrPeSuN", false);
      Logs.o("\t\t[DEBUG] h\t\t\t\t- Pregled vlakova i njihovih etapa", false);
      Logs.o("\t\t[DEBUG] h2\t\t\t\t- Pregled vlakova i njihovih etapa", false);
      Logs.o("\t\t[DEBUG] i\t\t\t\t- Dodavanje korisnika Pero Kos", false);
      Logs.o("\t\t[DEBUG] i2\t\t\t\t- Dodavanje korisnika Joshua Lee Fletcher", false);
      Logs.o("\t\t[DEBUG] j\t\t\t\t- Pregled korisnika", false);
    }
  }

  private void viewTracks() {
    Logs.header("Pregled pruga", true);

    List<String> header = Arrays.asList("Oznaka", "Početna stanica", "Završna stanica", "Udaljenost (km)");
    Logs.tableHeader(header);

    for (var trackId : RailwaySingleton.getInstance().getRailroad().keySet()) {
      TrainTrack track = RailwaySingleton.getInstance().getTrackById(trackId);
      String startStationName = track.getStartStation().name();
      String endStationName = track.getEndStation().name();
      String distance = String.format("%.2f", RailwaySingleton.getInstance().getTotalTrackLength(track.id()));

      List<String> row = Arrays.asList(
          track.id(),
          startStationName,
          endStationName,
          distance);

      Logs.tableRow(row);
    }
    Logs.printTable();

    Logs.footer(true);
  }

  private void viewStations(String trackID, String order) {
    Logs.header("Pregled stanica uz prugu", true);
    var data = RailwaySingleton.getInstance().getRailroad().get(trackID);
    if (data == null) {
      Logs.e("Nepostojeća pruga s oznakom: " + trackID);
      Logs.footer(true);
      return;
    }
    List<String> header = Arrays.asList("Naziv", "Vrsta", "Udaljenost od početne stanice (km)");
    Logs.tableHeader(header);
    if ("O".equalsIgnoreCase(order))
      Collections.reverse(data);
    for (var station : data) {
      double distance = !"O".equalsIgnoreCase(order) ? data.reversed().get(data.indexOf(station)).getDistanceFromEnd()
          : station.getDistanceFromStart();
      List<String> row = Arrays.asList(
          station.name(),
          station.type().toString(),
          String.format("%.2f", distance));
      Logs.tableRow(row);
    }
    Logs.printTable();
    Logs.footer(true);
  }

  private void viewStationsBetween(String startStation, String endStation) {
    Logs.header("Pregled stanica između " + startStation + " - " + endStation, true);
    List<Station> st1 = RailwaySingleton.getInstance().getStationsByName(startStation);
    List<Station> st2 = RailwaySingleton.getInstance().getStationsByName(endStation);
    if (st1.isEmpty() || st2.isEmpty()) {
      Logs.e("Nepostojeće stanice:" + (st1.isEmpty() ? " " + startStation : "")
          + (st2.isEmpty() ? " " + endStation : ""));
      Logs.footer(true);
      return;
    }
    traverseStationsBetween(st1.getFirst(), st2.getFirst());
    Logs.footer(true);
  }

  private String routeSignature(List<RailwaySingleton.Edge> route) {
    StringBuilder signature = new StringBuilder();
    for (RailwaySingleton.Edge edge : route) {
      signature.append(edge.to.name());
      signature.append(" ");
      signature.append(edge.weight);
      signature.append(" ");
      signature.append(edge.to.name());
    }
    return signature.toString();
  }

  private void traverseStationsBetween(Station startStation, Station endStation) {
    var routes = RailwaySingleton.getInstance().getRoutesBetweenStations(startStation, endStation);
    for (int i = 0; i < routes.size(); i++)
      for (int j = i + 1; j < routes.size(); j++)
        if (routeSignature(routes.get(i)).equals(routeSignature(routes.get(j))) && routes.size() > 1) {
          routes.remove(j);
          j--;
        }
    int i = 0;
    for (List<RailwaySingleton.Edge> route : routes) {
      if (routes.size() > 1) {
        Logs.o("\n", false);
        Logs.o("" + ++i + ". ruta", false);
      }
      Logs.tableHeader(Arrays.asList("Naziv", "Vrsta", "Udaljenost od početne stanice (km)"));
      double cumulativeDistance = 0.0;
      outputStation(startStation, cumulativeDistance);
      for (RailwaySingleton.Edge edge : route) {
        cumulativeDistance += edge.weight;
        Station currentStation = edge.to;
        outputStation(currentStation, cumulativeDistance);
      }
      Logs.printTable();
    }
  }

  private void outputStation(Station station, double distanceSoFar) {
    Logs.tableRow(Arrays.asList(
        station.name(),
        station.type().toString(),
        String.format("%.2f", distanceSoFar)));
  }

  private void viewComposition(int trainId) {
    Logs.header("Pregled kompozicija", true);
    var data = RailwaySingleton.getInstance().getCompositionsInTrain(trainId);
    if (data == null || data.isEmpty()) {
      Logs.e("Nepostojeća kompozicija s oznakom: " + trainId);
      Logs.footer(true);
      return;
    }
    List<String> header = Arrays.asList(
        "Oznaka", "Uloga", "Opis", "Godina", "Namjena", "Vrsta pogona", "Maks. brzina");
    Logs.tableHeader(header);
    for (var composition : data) {
      String purpose = composition.getWagon().purpose().toString();
      List<String> row = Arrays.asList(
          composition.getWagon().id(),
          composition.role().toString(),
          composition.getWagon().description(),
          String.valueOf(composition.getWagon().yearOfProduction()),
          purpose,
          composition.getWagon().driveType().toString(),
          String.valueOf(composition.getWagon().maxSpeed()));
      Logs.tableRow(row);
    }
    Logs.printTable();
    Logs.footer(true);
  }

  private void viewTrains() {
    Logs.header("Pregled voznog reda", true);
    var data = RailwaySingleton.getInstance().getSchedule().commandIV();
    if (data == null || data.isEmpty()) {
      Logs.e("Nema voznih redova.");
      Logs.footer(true);
      return;
    }
    List<String> header = Arrays.asList("Oznaka", "Polazna stanica", "Odredišna stanica", "Vrijeme polaska",
        "Vrijeme dolaska", "Udaljenost (km)");
    Logs.tableHeader(header);
    for (var train : data) {
      Logs.tableRow(train);
    }
    Logs.printTable();
    Logs.footer(true);
  }

  private void addUser(String name, String lastName) {
    Logs.header("Dodavanje korisnika", true);
    RailwaySingleton.getInstance().addUser(name, lastName);
    Logs.o("Dodan korisnik: " + name + " " + lastName);
    Logs.footer(true);
  }

  private void viewUsers() {
    Logs.header("Pregled korisnika", true);
    var data = RailwaySingleton.getInstance().getUsers();
    if (data == null || data.isEmpty()) {
      Logs.e("Nema korisnika u registru.");
      Logs.footer(true);
      return;
    }
    List<String> header = Arrays.asList("Ime", "Prezime");
    Logs.tableHeader(header);
    for (var user : data) {
      List<String> row = Arrays.asList(
          user.name(),
          user.lastName());
      Logs.tableRow(row);
    }
    Logs.printTable();
    Logs.footer(true);
  }

  private void viewTrainStagesOfTrain(String trainID) {
    Logs.header("Pregled etapa vlaka " + trainID, true);
    var data = RailwaySingleton.getInstance().getSchedule().commandIEV(trainID);
    if (data == null || data.isEmpty()) {
      Logs.e("Nema etapa za vlak s oznakom: " + trainID);
      Logs.footer(true);
      return;
    }
    List<String> header = Arrays.asList(
        "Oznaka vlaka",
        "Oznaka pruge",
        "Polazna stanica etape",
        "Odredišna stanica etape",
        "Vrijeme polaska",
        "Vrijeme dolaska",
        "Ukupan broj km",
        "Dani u tjednu");
    Logs.tableHeader(header);
    for (var stage : data) {
      Logs.tableRow(stage);
    }
    Logs.printTable();
    Logs.footer(true);
  }

  private void viewTrainsWithStagesOnDays(Set<Weekday> days) {
    Logs.header("Pregled vlakova koji voze sve etape na " + days, true);
    var data = RailwaySingleton.getInstance().getSchedule().commandIEVD(days);
    if (data == null || data.isEmpty()) {
      Logs.e("Nema vlakova koji voze sve etape na " + days);
      Logs.footer(true);
      return;
    }
    List<String> header = Arrays.asList(
        "Oznaka vlaka",
        "Oznaka pruge",
        "Polazna stanica etape",
        "Odredišna stanica etape",
        "Vrijeme polaska",
        "Vrijeme dolaska",
        "Dani u tjednu");
    Logs.tableHeader(header);
    for (var train : data) {
      Logs.tableRow(train);
    }
    Logs.printTable();
    Logs.footer(true);
  }

  private void viewTrainTimetable(String trainID) {
    Logs.header("Pregled voznog reda vlaka " + trainID, true);
    List<List<String>> data = RailwaySingleton.getInstance().getSchedule().commandIVRV(trainID);
    if (data == null || data.isEmpty()) {
      Logs.e("Nema rasporeda za vlak s oznakom: " + trainID);
      Logs.footer(true);
      return;
    }

    List<String> header = Arrays.asList(
        "Oznaka vlaka",
        "Oznaka pruge",
        "Željeznička stanica",
        "Vrijeme polaska",
        "Broj km od polazne stanice");
    Logs.tableHeader(header);

    for (var row : data) {
      Logs.tableRow(row);
    }

    Logs.printTable();
    Logs.footer(true);
  }

  /**
   * Process the LINK command (dodatna funkcionalnost izvan specifikacija zadaće
   * 2).
   * 
   * @param command The command string.
   */
  private void processLinkCommand(String command) {
    var matcher = linkPattern.matcher(command);

    // Validate the match first
    if (!matcher.matches()) {
      Logs.e("Neispravan format naredbe LINK.");
      return;
    }

    // Check for LINK PREGLED
    if (matcher.group("name") == null) {
      viewGroupChatListWithMembers();
    } else {
      // Extract the parameters safely
      String name = matcher.group("name");
      String lastName = matcher.group("lastName");
      String groupId = matcher.group("groupId");
      String action = matcher.group("action");

      var user = RailwaySingleton.getInstance().getUserByName(name, lastName, true);
      if (user == null) {
        Logs.e("Korisnik nije pronađen: " + name + " " + lastName);
        return;
      }

      switch (action) {
        case "O":
          userChat.linkUser(groupId, user);
          break;
        case "Z":
          userChat.unlinkUser(groupId, user);
          break;
        default:
          if (!action.isEmpty()) {
            userChat.broadcast(groupId, user, action);
          } else {
            Logs.e("Poruka ne smije biti prazna.");
          }
          break;
      }
    }
  }

  private void viewGroupChatListWithMembers() {
    Logs.header("Pregled grupa", true);
    var data = userChat.groupChats();
    if (data == null || data.isEmpty()) {
      Logs.e("Nema grupa.");
      Logs.footer(true);
      return;
    }
    List<String> header = Arrays.asList("Grupa", "--Članovi--");
    Logs.tableHeader(header);
    for (var group : data)
      Logs.tableRow(group);
    Logs.printTable(120);
    Logs.footer(true);
  }

  private void addTrainObserver(String command) {
    Logs.header("Dodavanje korisnika za praćenje vlaka ili dolaska u određenu željezničku stanicu", true);
    var matcher = addTrainObserverPattern.matcher(command);
    matcher.matches();
    String name = matcher.group("name");
    String lastName = matcher.group("lastName");
    User user = RailwaySingleton.getInstance().getUserByName(name, lastName, false);
    if (user == null) {
      Logs.e("Korisnik nije pronađen: " + name + " " + lastName);
      Logs.footer(true);
      return;
    }
    String station = matcher.group("station");
    String trainID = matcher.group("trainId");
    TrainComposite train = RailwaySingleton.getInstance().getSchedule().children.stream()
        .filter(t -> t.trainID.equals(trainID)).findFirst().orElse(null);
    if (train == null) {
      Logs.e("Vlak s oznakom " + trainID + " nije pronađen.");
      Logs.footer(true);
      return;
    }
    if (station == null) {
      train.registerObserver(user);
      Logs.o("Korisnik " + user + " sada prati vlak " + trainID);
    } else {
      boolean stationExists = train.hasStation(station);
      if (!stationExists) {
        Logs.e("Vlak " + trainID + " ne prolazi kroz stanicu " + station);
        Logs.footer(true);
        return;
      }
      train.registerObserver(user, station);
      Logs.o("Korisnik " + user + " sada prati dolazak vlaka " + trainID + " u stanicu " + station);
    }
    Logs.footer(true);
  }

  public void viewTrainScheduleBetweenStations(Matcher ivi2sMatcher) {
    String startStation = ivi2sMatcher.group(1).trim();
    String endStation = ivi2sMatcher.group(2).trim();
    String day = ivi2sMatcher.group(3).trim();
    String fromTime = ivi2sMatcher.group(4).trim();
    String toTime = ivi2sMatcher.group(5).trim();
    String displayFormat = ivi2sMatcher.group(6).trim();
    Weekday weekday;
    try {
      weekday = Weekday.dayFromString(day);
    } catch (IllegalArgumentException e) {
      Logs.e("Nepoznata oznaka dana: " + day);
      return;
    }
    ScheduleTime startTime, endTime;
    try {
      startTime = new ScheduleTime(fromTime);
      endTime = new ScheduleTime(toTime);
    } catch (IllegalArgumentException e) {
      Logs.e("Neispravno vrijeme: " + e.getMessage());
      return;
    }
    if (weekday == null || startTime == null || endTime == null) {
      Logs.e("Nevažeći parametri");
      return;
    }
    Logs.header("Pregled vlakova koji prolaze kroz " + startStation + " - " + endStation + " (" + weekday + ": "
        + startTime + "-" + endTime + ")", true);
    var data = RailwaySingleton.getInstance()
        .getSchedule()
        .commandIVI2S(startStation, endStation, weekday, startTime, endTime, displayFormat);

    if (data == null || data.isEmpty()) {
      Logs.e("Nema pronađenih vlakova");
      return;
    }

    // Sort data based on the earliest "V" column value
    // data.sort((a, b) -> {
    //   String vKeyA = a.keySet().stream().filter(k -> k.startsWith("V")).findFirst().orElse(null);
    //   String vKeyB = b.keySet().stream().filter(k -> k.startsWith("V")).findFirst().orElse(null);
    //   if (vKeyA == null || vKeyB == null || a.get(vKeyA) == null || b.get(vKeyB) == null) {
    //     return 0;
    //   }
    //   ScheduleTime timeA = new ScheduleTime(a.get(vKeyA));
    //   ScheduleTime timeB = new ScheduleTime(b.get(vKeyB));
    //   return timeA.compareTo(timeB);
    // });
    // Sort data based on the K column's double
    data.sort((a, b) -> {
      String kKeyA = a.keySet().stream().filter(k -> k.startsWith("K")).findFirst().orElse("0");
      String kKeyB = b.keySet().stream().filter(k -> k.startsWith("K")).findFirst().orElse("0");
      if (kKeyA == null || kKeyB == null || a.get(kKeyA) == null || b.get(kKeyB) == null) {
        return 0;
      }
      double kA = Double.parseDouble(a.get(kKeyA));
      double kB = Double.parseDouble(b.get(kKeyB));
      return Double.compare(kA, kB);
    });

    // Create headers from format
    List<String> headers = createHeadersFromFormat(displayFormat);
    List<String> finalHeaders = new ArrayList<>();
    List<List<String>> tableRows = new ArrayList<>();

    // should reconstruct the entire row by:
    // storing the values of the row's existing v-keys in a map
    // remove the row's existing v-keys
    // iterating over all the vkeys in the allvkeysintable and for those that are not in the map, add them with a default value, for those which are readd the existing value
    Set<String> allVKeysInTable = data.stream()
        .flatMap(row -> row.keySet().stream().filter(k -> k.startsWith("V")))
        .collect(Collectors.toSet());
        
    var newTableRows = new ArrayList<>(data);
    for (Map<String, String> row : data) {
      Map<String, String> newRow = new HashMap<>();
      Map<String, String> vValues = new HashMap<>();
      for (String key : row.keySet()) {
        if (key.startsWith("V")) {
          vValues.put(key, row.get(key));
        } else {
          newRow.put(key, row.get(key));
        }
      }
      for (String vKey : allVKeysInTable) {
        newRow.put(vKey, vValues.getOrDefault(vKey, "-"));
      }
      // swap out the row
      newTableRows.set(newTableRows.indexOf(row), newRow);
    }

    data = newTableRows;

    // Process each row for the table
    for (Map<String, String> row : data) {
      List<String> tableRow = new ArrayList<>();
      for (String header : headers) {
        if (header.equals("V")) {
          // Handle all "V" keys in the row
          List<String> vKeys = row.keySet().stream()
              .filter(k -> k.startsWith("V"))
              .toList();
          for (String key : vKeys) {
            tableRow.add(row.get(key));
            finalHeaders.add(key); // Use the actual key as the header
          }
        } else {
          tableRow.add(row.getOrDefault(header, ""));
          finalHeaders.add(header);
        }
      }
      tableRows.add(tableRow);
    }

    // Ensure headers align with repeated characters in displayFormat
    finalHeaders = new ArrayList<>();
    for (char c : displayFormat.toCharArray()) {
      if (c == 'V') {
        data.stream()
            .flatMap(row -> row.keySet().stream().filter(k -> k.startsWith("V")))
            .distinct()
            .forEach(finalHeaders::add);
      } else {
        finalHeaders.add(String.valueOf(c));
      }
    }

    // Print the table
    Logs.tableHeader(finalHeaders);
    for (List<String> row : tableRows) {
      Logs.tableRow(row);
    }
    Logs.printTable();
    Logs.footer(true);
  }

  private List<String> createHeadersFromFormat(String format) {
    List<String> headers = new ArrayList<>();
    for (char c : format.toCharArray()) {
      switch (c) {
        case 'S' -> headers.add("S");
        case 'P' -> headers.add("P");
        case 'K' -> headers.add("K");
        case 'V' -> headers.add("V");
        default -> throw new IllegalArgumentException("Nepoznat format: " + c);
      }
    }
    return headers;
  }
}

/*
 * 
 * 
 * ● Pregled vlakova (voznog reda) kojima se može putovati od jedne željezničke
 * stanice do
 * druge željezničke stanice na određen dan u tjednu unutar zadanog vremena
 * ○ Sintaksa:
 * ■ IVI2S polaznaStanica - odredišnaStanica - dan - odVr - doVr - prikaz
 * ○ Primjeri:
 * ■ IVI2S Donji Kraljevec - Čakovec - N - 0:00 - 23:59 - SPKV
 * ■ IVI2S Donji Kraljevec - Novi Marof - Pe - 08:00 - 16:00 - KPSV
 * ■ IVI2S Donji Kraljevec - Ludbreg - Su - 5:20 - 20:30 - VSPK
 * ○ Opis primjera:
 * ■ Ispis tablice sa željezničkim stanicama između dviju željezničkih stanica,
 * s
 * brojem kilometara, vremenima polaska vlakova sa željezničkih stanica.
 * Prikazuju se samo oni vlakovi koji prometuju na određeni dan i čije je
 * vrijeme polaska s polazne željezničke stanice nakon odVr vremena i
 * vrijeme dolaska u odredišnu željezničku stanicu prije doVr vremena.
 * Podaci se prikazuju u stupcima čiji redoslijed je proizvoljan i stupcima se
 * mogu ponavljati. S označava naziv željezničke stanice, P označava prugu,
 * K označava broj km od polazne željezničke stanice, V označava vrijeme
 * polaska određenog vlaka sa željezničke stanice. V se odnosi na jedan ili
 * više stupaca. Potrebno je prilagoditi ispis zaglavlja i redova zadanom
 * prikazu. Osim gornjih primjera prikaza mogu biti i drugi prikazi kao npr: SPV
 * (nema prikaza broj kilometara), KPSVK (broj kilometara se prikazuje u
 * prvom i posljednjem stupcu). U stupcu pojedinog vlaka ispisuje se vrijeme
 * polaska sa željezničke stanice. U 1. primjeru su stanice koje su na istoj
 * pruzi, na 2. primjeru su željezničke stanice koje su na dvije pruge, a na 3.
 * primjeru su željezničke stanice koje su na tri pruge. Vlakovi se ispisuju u
 * kronološkom redoslijedu vremena polaska vlaka s njegove polazne
 * željezničke stanice. Slika 1 prikazuje djelomični izvod iz voznog reda od
 * željezničke stanice Zabok do druge željezničke stanice Gornja Stubica za
 * ponedjeljak od vremena 5:00 do vremena 12:00 uz oznake KSV. Na slici
 * treba zanemariti oznake dana u tjednu.
 * 
 * Ispis tablice ima stupce i retke zamijenjeno, npr. (ovo nije primjer ispisa
 * za ovaj zadatak, samo primjer kako izgleda dok su zamijenjeni stupci)
 * 
 *
 * Udaljenost od pocetne stanice | od | 3210 | 3212 | 3214 | 3216 | 3218
 * . ----------------------------- | ---------------- | ---- | ---- | ---- |
 * ---- | -----
 * . 0 | Zabok | 5:10 | 6:10 | 7:46 | 8:47 | 10:51
 * . 2 | Hum Lug | 5:13 | 6:13 | 7:49 | 8:50 | 10:54
 * . 4 | Oroslavje | 5:17 | 6:17 | 7:53 | 8:54 | 10:58
 * . 7 | Stubi?ke Toplice | 5:22 | 6:22 | 7:58 | 8:59 | 11:03
 * . 9 | Donja Stubica | 5:26 | 6:26 | 8:02 | 9:03 | 11:07
 * . 12 | Gornja Stubica | 5:31 | 6:31 | 8:07 | 9:08 | 11:12
 *
 * 
 */

/*
 * IVI2S Mala Subotica - Kotoriba - Pe - 00:00 - 23:59 - SPVK
 * IVI2S Zabok - Zagreb glavni kolodvor - Pe - 06:55 - 07:22 - SPKV
 */