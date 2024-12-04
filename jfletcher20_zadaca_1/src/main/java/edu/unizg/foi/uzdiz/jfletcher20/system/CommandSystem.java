package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
      identifyCommand(command);
    }
    Logs.footer(true);
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
    Logs.o("Uzorci dizajna, 2024. - Joshua Lee Fletcher");
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

  private void traverseStationsBetween(Station startStation, Station endStation) {
    var routes = RailwaySingleton.getInstance().getRoutesBetweenStations(startStation, endStation);
    for (List<Station> stations : routes) {
      Logs.tableHeader(Arrays.asList("Naziv", "Vrsta", "Udaljenost od početne stanice (km)"));
      double cumulativeDistance = 0.0;
      for (int i = 0; i < stations.size(); i++) {
        Station currentStation = stations.get(i), intermediateStation = null;
        if (i > 0) {
          Station previousStation = stations.get(i - 1);
          TrainTrack track = previousStation.getTrack();
          List<Station> trackStations = RailwaySingleton.getInstance().getRailroad().get(track.id());
          int startIndex = trackStations.indexOf(previousStation);
          int endIndex = trackStations.indexOf(currentStation);
          if (startIndex != -1 && endIndex != -1) {
            if (startIndex < endIndex) {
              for (int j = startIndex + 1; j < endIndex; j++) {
                intermediateStation = trackStations.get(j);
                cumulativeDistance += intermediateStation.getTrack().trackLength();
                outputStation(intermediateStation, cumulativeDistance);
              }
            } else
              for (int j = startIndex - 1; j > endIndex; j--) {
                intermediateStation = trackStations.get(j);
                cumulativeDistance += intermediateStation.getTrack().trackLength();
                outputStation(intermediateStation, cumulativeDistance);
              }
          }
        }
        if (i > 0)
          cumulativeDistance += currentStation.getTrack().trackLength();
        if (i == stations.size() - 1 && stations.size() > 1 && intermediateStation != null)
          cumulativeDistance += stations.getFirst().getTrack().trackLength();
        outputStation(currentStation, cumulativeDistance);
      }
      Logs.printTable();
    }
    Logs.toggleInfo();
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
