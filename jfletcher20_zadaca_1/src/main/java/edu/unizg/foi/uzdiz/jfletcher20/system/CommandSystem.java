package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.lang.annotation.Repeatable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;

/*
 * Nakon što se učitaju sve potrebne datoteke potrebno je pripremiti program za izvršavanje komandi
 * u interaktivnom načinu rada. Tijekom rada programa može se izvršiti više komandi sve dok se ne
 * upiše komanda Q.
 */

/**
 * The CommandSystem class is responsible for handling the command system of the program.
 * 
 * <p>
 * After all necessary files are loaded, the program must be prepared to execute commands in
 * interactive mode. During the program's operation, multiple commands can be executed until the
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
      "^ISI2S (?<startStation>[A-Za-z]+) (?<endStation>[A-Za-z]+)$" //
  );
  Pattern viewCompositionPattern = Pattern.compile( //
      "^IK (?<compositionCode>[0-9]+)$" //
  );


  public static CommandSystem instance = new CommandSystem();

  private CommandSystem() {}

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
      // Logs.c("Detektirana komanda " + command);
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
      Logs.c("Detektirana komanda za pregled pruga.");
      viewTracks();
      return true;
    } else if (vsMatcher.matches()) {
      Logs.c("Detektirana komanda za pregled stanica uz prugu.");
      viewStations(vsMatcher.group("trackCode"), vsMatcher.group("order"));
      return true;
    } else if (vsbMatcher.matches()) {
      Logs.c("Detektirana komanda za pregled stanica između dvije stanice.");
      viewStationsBetween(vsbMatcher.group("startStation"), vsbMatcher.group("endStation"));
      return true;
    } else if (vcMatcher.matches()) {
      Logs.c("Detektirana komanda za pregled kompozicija.");
      try {
        viewComposition(ParsingUtil.i(vcMatcher.group("compositionCode")));
      } catch (NumberFormatException e) {
        Logs.e("Neispravna oznaka kompozicije: " + vcMatcher.group("compositionCode"));
      }
      return true;
    } else {
      Logs.c("Nepoznata komanda. Dostupne komande su:");
      outputMenu();
      return false;
    }
  }

  private void outputMenu() {
    Logs.header("JLF Željeznica: Interaktivni način rada", true);
    Logs.withPadding(() -> Logs.o("Validne komande:"), false, true);
    Logs.withPadding(() -> {
      Logs.o("IP", false);
      Logs.o("\t- Pregled pruga", false);
    }, false, true);
    Logs.withPadding(() -> {
      Logs.o("ISP [oznakaPruge] [N|O]", false);
      Logs.o("\t- Pregled stanica uz prugu u rastućem ili padajućem redoslijedu", false);
    }, false, true);
    Logs.withPadding(() -> {
      Logs.o("ISI2S [nazivStanice1] [nazivStanice2]", false);
      Logs.o("\t- Pregled stanica između dvije stanice", false);
    }, false, true);
    Logs.withPadding(() -> {
      Logs.o("IK [oznakaKompozicije]", false);
      Logs.o("\t- Pregled kompozicija", false);
    }, false, true);
    Logs.withPadding(() -> Logs.o("Q - Izlaz iz programa", false), false, true);
    Logs.o("Uzorci dizajna, 2024. - Joshua Lee Fletcher");
  }

  private void viewTracks() {
    Logs.header("Pregled pruga", true);
    Logs.o(" Oznaka\t| Početna stanica\t\t| Završna stanica\t\t| Udaljenost (km)", false);
    Logs.o(" ------\t| ---------------\t\t| ---------------\t\t| ---------------", false);
    for (var track : RailwaySingleton.getInstance().getRailroad().keySet()) {
      TrainTrack trackObj = RailwaySingleton.getInstance().getTrackById(track);
      var t1 = trackObj.getStartStation().name();
      var t2 = trackObj.getEndStation().name();
      String t1Padding = t1.length() > 13 ? t1.length() > 18 ? "\t" : "\t\t" : "\t\t\t";
      String t2Padding = t2.length() > 13 ? t2.length() > 18 ? "\t" : "\t\t" : "\t\t\t";
      Logs.o(" " + trackObj.id() + "\t| " + t1 + t1Padding + "| " + t2 + t2Padding + "| "
          + RailwaySingleton.getInstance().getTotalTrackLength(trackObj.id()), false);
    }
  }

  private void viewStations(String trackID, String order) {
    Logs.header("Pregled stanica uz prugu", true);
    var data = RailwaySingleton.getInstance().getRailroad().get(trackID);
    if (data == null) {
      Logs.e("Nepostojeća pruga s oznakom: " + trackID);
      Logs.footer(true);
      return;
    }
    Logs.o("Oznaka pruge: " + trackID);
    Logs.o("Redoslijed: " + (order.equals("N") ? "Rastući" : "Padajući"));
    Logs.o(" Naziv\t\t\t| Vrsta\t\t| Udaljenost od početne stanice (km)", false);
    Logs.o(" -----\t\t\t| -----\t\t| ----------------------------", false);
    if (order.equals("O"))
      data = data.reversed();
    for (var station : data) {
      String stationName = station.name();
      String stationPadding =
          stationName.length() > 8 ? stationName.length() > 17 ? "\t" : "\t\t" : "\t\t\t";
      String stationType = station.type().toString();
      String stationTypePadding =
          stationType.length() > 8 ? stationType.length() > 17 ? "\t" : "\t" : "\t";
      Logs.o(
          " " + stationName + stationPadding + "| " + station.type() + stationTypePadding + "| "
              + (order.equals("O") ? station.getDistanceFromEnd() : station.getDistanceFromStart()),
          false);
    }
    Logs.footer(true);
  }

  private void viewStationsBetween(String startStation, String endStation) {
    Logs.header("Pregled stanica između " + startStation + " - " + endStation, true);
    var data = RailwaySingleton.getInstance().getRailroad();
    for (var track : data.keySet()) {
      var stations = data.get(track);
      int start = -1, end = -1;
      for (int i = 0; i < stations.size(); i++) {
        if (stations.get(i).name().equals(startStation))
          start = i;
        if (stations.get(i).name().equals(endStation))
          end = i;
      }
      if (start != -1 && end != -1) {
        Logs.i("Pronađene stanice na pruzi " + track + ": " + startStation + " i " + endStation);
        Logs.i("Između stanica " + startStation + " i " + endStation + " nalazi se " + (end - start)
            + " stanica.");
        break;
      }
    }

    Logs.footer(true);
  }

  private void viewComposition(int trainId) {
    Logs.header("Pregled kompozicija", true);
    var data = RailwaySingleton.getInstance().getCompositionsInTrain(trainId);
    if (data == null || data.isEmpty()) {
      Logs.e("Nepostojeća kompozicija s oznakom: " + trainId);
      Logs.footer(true);
      return;
    }
    int maxDescLength =
        data.stream().mapToInt(c -> c.getWagon().description().length()).max().getAsInt();
    Logs.o("Oznaka\t| Uloga\t| Opis" + " ".repeat(maxDescLength - "Opis".length())
        + " | Godina\t| Namjena\t| Vrsta pogona\t| Maks. brzina", false);
    Logs.o("------\t| -----\t| ----" + " ".repeat(maxDescLength - "----".length())
        + " | ------\t| -------\t| ------------\t| ------------", false);
    for (var composition : data) {
      String purpose = composition.getWagon().purpose().toString();
      String purposePadding = purpose.length() > 6 ? "\t" : "\t\t";
      Logs.o(" " + composition.getWagon().id() + "\t| " + composition.role() + "\t| "
          + composition.getWagon().description()
          + " ".repeat(maxDescLength - composition.getWagon().description().length()) + " | "
          + composition.getWagon().yearOfProduction() + "\t| " + purpose + purposePadding + "| " + composition.getWagon().driveType() + "\t\t| "
          + composition.getWagon().maxSpeed(), false);
    }
    Logs.footer(true);
  }

}

/*
 * Korisniku se daje mogućnost da izvrši sljedeće komande za aktivnosti: ● Pregled pruga ○ Sintaksa:
 * ■ IP ○ Primjer: ■ IP ○ Opis primjera: ■ Ispis tablice s prugama (oznaka, početna i završna
 * željeznička stanica, ukupan broj kilometara). ● Pregled željezničkih stanica za odabranoj pruzi ○
 * Sintaksa: ■ ISP oznakaPruge redoslijed ○ Primjer: ■ ISP M501 N ○ Opis primjera: ■ Ispis tablice
 * sa željezničkim stanicama na odabranoj pruzi (naziv željezničke stanice, vrsta, broj kilometara
 * od početne željezničke stanice) prema normalnom redoslijedu. Npr. kod M501 ide od Kotoriba do
 * Macinec. ○ Primjer: ■ ISP M501 O ○ Opis primjera: ■ Ispis tablice sa željezničkim stanicama na
 * odabranoj pruzi (naziv željezničke stanice, vrsta, broj kilometara od početne željezničke
 * stanice) prema obrnutom redoslijedu. Npr. kod M501 ide od Macinec do Kotoriba. ● Pregled
 * željezničkih stanica između dviju željezničke stanica ○ Sintaksa: ■ ISI2S polaznaStanica -
 * odredišnaStanica ○ Primjer: ■ ISI2S Donji Kraljevec - Čakovec ○ Opis primjera: 4 Kolegij: Uzorci
 * dizajna Akademska godina: 2024./2025. ■ Ispis tablice sa željezničkim stanicama između dviju
 * željezničke stanica (naziv željezničke stanice, vrsta, broj kilometara od početne željezničke
 * stanice). U primjeru su stanice koje su na istoj pruzi. ○ Primjer: ■ ISI2S Donji Kraljevec -
 * Zagreb glavni kolodvor ○ Opis primjera: ■ Ispis tablice sa željezničkim stanicama na odabranoj
 * pruzi (naziv željezničke stanice, vrsta, broj kilometara od početne željezničke stanice) U
 * primjeru su željezničke stanice koje su na različitim prugama. ● Pregled kompozicije ○ Sintaksa:
 * ■ IK oznaka ○ Primjer: ■ IK 8001 ○ Opis primjera: ■ Ispis tablice sa prijevoznim sredstvima u
 * kompoziciji (oznaka, uloga, opis, godina, namjena, vrsta pogona, maks. brzina). ● Prekid rada
 * programa ○ Sintaksa: ■ Q
 */
