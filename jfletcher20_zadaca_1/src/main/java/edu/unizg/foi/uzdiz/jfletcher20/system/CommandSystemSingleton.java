package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unizg.foi.uzdiz.jfletcher20.Main;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrackStageComposite;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;

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
  Pattern addUserPattern = Pattern.compile("^DK (?<name>.+) (?<lastName>.+)$");
  Pattern viewUsersPattern = Pattern.compile("^PK$");

  Pattern viewTrainsPattern = Pattern.compile("^IV$");
  Pattern viewTrainStagesPattern = Pattern.compile("^IEV (?<trainCode>\\d+)$");

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
          "IEV 3609",
          "DK Pero Kos",
          "PK",
      };
      for (String c : commands) {
        Logs.c("Izvršavanje komande: " + c);
        identifyCommand(c);
      }
    } else {
      switch (command.trim().toLowerCase()) {
        case "a" -> command = "IP";
        case "b" -> command = "ISP M501 N";
        case "b2" -> command = "ISP M501 O";
        case "c" -> command = "ISI2S Kotoriba - Ludbreg";
        case "c2" -> command = "ISI2S Ludbreg - Kotoriba";
        case "c3" -> command = "ISI2S Kotoriba - Macinec";
        case "c4" -> command = "ISI2S Macinec - Kotoriba";
        case "d" -> command = "IK 8001";
        case "d2" -> command = "IK 1";
        case "e" -> command = "IV";
        case "f" -> command = "IEV 3609";
        case "g" -> command = "DK Pero Kos";
        case "h" -> command = "DK Pero Kos";
        case "h2" -> command = "DK Joshua Lee Fletcher";
        case "i" -> command = "PK";
      }
      identifyCommand(command);
    }
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
    } else if (viewTrainStagesMatcher.matches()) {
      viewTrainStagesOfTrain(viewTrainStagesMatcher.group("trainCode"));
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
    Logs.o("IEV [oznakaVlaka]\t\t\t- Pregled etapa vlaka", false);
    Logs.o("DK [ime] [prezime]\t\t\t- Dodavanje korisnika", false);
    Logs.o("PK\t\t\t\t\t- Pregled korisnika", false);

    Logs.withPadding(() -> Logs.o("Q - Izlaz iz programa", false), true, true);
    outputDebugMenu();
    Logs.o("Uzorci dizajna, 2024. - Joshua Lee Fletcher");
  }

  private void outputDebugMenu() {
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
      Logs.o("\t\t[DEBUG] f\t\t\t\t- Pregled etapa vlaka (oznaka 3609)", false);
      Logs.o("\t\t[DEBUG] g\t\t\t\t- Dodavanje korisnika (Pero Kos)", false);
      Logs.o("\t\t[DEBUG] h\t\t\t\t- Dodavanje korisnika (Pero Kos)", false);
      Logs.o("\t\t[DEBUG] h2\t\t\t\t- Dodavanje korisnika (Joshua Lee Fletcher)", false);
      Logs.o("\t\t[DEBUG] i\t\t\t\t- Pregled korisnika", false);
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
      double distance = "O".equalsIgnoreCase(order) ? station.getDistanceFromEnd() : station.getDistanceFromStart();
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
}

/*
 * ● Pregled etapa vlaka
 * ○ Sintaksa:
 * ■ IEV oznaka
 * ○ Primjer:
 * ■ IEV 3609
 * ○ Opis primjera:
 * ■ Ispis tablice sa etapama vlaka (oznaka vlaka, oznaka pruge, polazna
 * željeznička stanica etape, odredišna željeznička stanica etape, vrijeme
 * polaska s polazne željezničke stanice etape, vrijeme dolaska u odredišnu
 * stanicu etape, ukupan broj km od polazne željezničke stanice etape do
 * odredišne željezničke stanice vlaka etape, daniUTjednu za etapu).
 */