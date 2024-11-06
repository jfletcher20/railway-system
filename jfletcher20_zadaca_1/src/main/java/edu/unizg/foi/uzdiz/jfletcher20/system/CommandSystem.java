package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.regex.Pattern;

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
  Pattern viewStationsPattern = Pattern.compile("^ISP [A-Z0-9]+ [NO]$");
  Pattern viewStationsBetweenPattern = Pattern.compile("^ISI2S [A-Za-z]+ [A-Za-z]+$");
  Pattern viewCompositionPattern = Pattern.compile("^IK [0-9]+$");

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
    if (viewTracksPattern.matcher(command).matches()) {
      Logs.c("Detektirana komanda za pregled pruga.");
      return true;
    } else if (viewStationsPattern.matcher(command).matches()) {
      Logs.c("Detektirana komanda za pregled stanica uz prugu.");
      return true;
    } else if (viewStationsBetweenPattern.matcher(command).matches()) {
      Logs.c("Detektirana komanda za pregled stanica između dvije stanice.");
      return true;
    } else if (viewCompositionPattern.matcher(command).matches()) {
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
