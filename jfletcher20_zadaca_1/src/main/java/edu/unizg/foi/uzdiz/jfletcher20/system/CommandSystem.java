package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;

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
      "^ISP (?<trackCode>[A-Z0-9]+) (?<order>[NO])$" //
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
      Logs.c("Detektirana komanda " + command);
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
      return true;
    } else if (vcMatcher.matches()) {
      Logs.c("Detektirana komanda za pregled kompozicija.");
      return true;
    } else {
      Logs.c("Nepoznata komanda.");
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
      Logs.o("ISI2S [station1] [station2]", false);
      Logs.o("\t- Pregled stanica između dvije stanice", false);
    }, false, true);
    Logs.withPadding(() -> {
      Logs.o("IK [compositionID]", false);
      Logs.o("\t- Pregled kompozicija", false);
    }, false, true);
    Logs.withPadding(() -> Logs.o("Q - Izlaz iz programa", false), false, true);
    Logs.o("Uzorci dizajna, 2024. - Joshua Lee Fletcher");
  }

  private void viewTracks() {
    Logs.header("Pregled pruga", true);
    for (var track : RailwaySingleton.getInstance().getRailroad().keySet()) {
      TrainTrack trackObj = RailwaySingleton.getInstance().getTrackById(track);
      Logs.withPadding(() -> {
        Logs.o("Oznaka:          " + trackObj.id(), true);
        Logs.o("Početna stanica: " + trackObj.getStartStation().name(), false);
        Logs.o("Završna stanica: " + trackObj.getEndStation().name(), false);
        Logs.o(
            "Udaljenost (km): " + RailwaySingleton.getInstance().getTotalTrackLength(trackObj.id()),
            false);
        Logs.o("Ukupno ima " + RailwaySingleton.getInstance().getRailroad().get(track).size()
            + " stanica.", false);
      }, false, true);
    }
    Logs.footer(true);
  }

  private void viewStations(String trackID, String order) {
    Logs.header("Pregled stanica uz prugu", true);
    Logs.o("Oznaka pruge: " + trackID);
    Logs.o("Redoslijed: " + (order.equals("N") ? "Rastući" : "Padajući"));
    for (var station : RailwaySingleton.getInstance().getRailroad().get(trackID)) {
      Logs.withPadding(() -> {
        Logs.o("Naziv: " + station.name(), true);
        Logs.o("Vrsta: " + station.type().name(), false);
        Logs.o("Udaljenost: " + -1 + " km", false);
      }, false, true);
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
