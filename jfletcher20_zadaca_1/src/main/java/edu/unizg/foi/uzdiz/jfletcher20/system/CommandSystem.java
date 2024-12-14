package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.unizg.foi.uzdiz.jfletcher20.Main;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
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
public class CommandSystem {

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

  public static CommandSystem instance = new CommandSystem();

  private CommandSystem() {
  }

  public static CommandSystem getInstance() {
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
      else identifyCommand(command);
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
          "IK 1"
      };
      for (String c : commands) {
        Logs.c("Izvršavanje komande: " + c);
        identifyCommand(c);
      }
    } else if(command.trim().equalsIgnoreCase("All1")) {
      String[] commands = new String[] {
          "IP", "ISP M501 N",
          "ISI2S Kotoriba - Ludbreg",
          "IK 8001",
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
        case "e" -> command = "IK 8001";
        case "e2" -> command = "IK 1";
      }
      identifyCommand(command);
    }
  }

  private boolean identifyCommand(String command) {
    Matcher vtMatcher = viewTracksPattern.matcher(command);
    Matcher vsMatcher = viewStationsPattern.matcher(command);
    Matcher vsbMatcher = viewStationsBetweenPattern.matcher(command);
    Matcher vcMatcher = viewCompositionPattern.matcher(command);
    if (vtMatcher.matches()) {
      // Logs.c("Detektirana komanda za pregled pruga.");
      viewTracks();
      return true;
    } else if (vsMatcher.matches()) {
      // Logs.c("Detektirana komanda za pregled stanica uz prugu.");
      viewStations(vsMatcher.group("trackCode"), vsMatcher.group("order"));
      return true;
    } else if (vsbMatcher.matches()) {
      // Logs.c("Detektirana komanda za pregled stanica između dvije stanice.");
      viewStationsBetween(vsbMatcher.group("startStation"), vsbMatcher.group("endStation"));
      return true;
    } else if (vcMatcher.matches()) {
      // Logs.c("Detektirana komanda za pregled kompozicija.");
      try {
        viewComposition(ParsingUtil.i(vcMatcher.group("compositionCode")));
      } catch (NumberFormatException e) {
        Logs.e("Neispravna oznaka kompozicije: " + vcMatcher.group("compositionCode"));
      }
      return true;
    } else {
      Logs.c("Nepoznata komanda.");
      outputMenu();
      return false;
    }
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
    Logs.withPadding(() -> {
      Logs.o("IK [oznakaKompozicije]\t\t\t- Pregled kompozicija", false);
    }, false, true);
    Logs.withPadding(() -> Logs.o("Q - Izlaz iz programa", false), false, true);
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
      Logs.o("\t\t[DEBUG] e\t\t\t\t- Pregled kompozicija (oznaka 8001)", false);
      Logs.o("\t\t[DEBUG] e2\t\t\t\t- Pregled kompozicija (oznaka 1)", false);
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
    for (List<RailwaySingleton.Edge> route : routes) {
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

}
