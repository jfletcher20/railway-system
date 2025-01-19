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

import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.tickets.Ticket;
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;

/*
 * Potrebno je dodati funkcionalnost za kupovinu karata za vožnju putnika. Određivanje 
cijene vožnje putnika vlakom temelji se na €/km za pojedinu vrstu vlaka (normalni, ubrzani, brzi). 
Osnovna cijena karte vrijedi za kupovinu na blagajni. Tvrtka daje % popusta na cijenu ako se vozi 
vlakom u subotu i/ili nedjelju. Tvrtka želi promovirati kupovinu karte putem web/mobilne aplikacije 
za što daje određeni % popusta na cijenu. S druge strane tvrtka za kupovinu karte u vlaku 
određuje % uvećanja cijene. Izračun cijene karte s obzirom na način kupovine (blagajna, 
web/mobilna aplikacija, u vlaku) treba se temeljiti na uzorku dizajna Strategy.  
Svaku kupovinu karte potrebno je pohraniti kako bi se moglo do nje kasnije pristupiti, a 
treba se temeljiti na uzorku dizajna Memento.
SUma sumarum:
  % popusta za subotu i nedjelju, % popusta za kupovinu karte putem web/mobilne
  % uvećanja za kupovinu karte u vlaku

● Određivanje cijene vožnje putnika vlakom €/km, popust za subotu i nedjelju, % popusta 
za kupovinu karte putem web/mobilne aplikacije i % uvećanja za kupovinu karte u vlaku 
○ Sintaksa:  
■ CVP cijenaNormalni cijenaUbrzani cijenaBrzi popustSuN popustWebMob 
uvecanjeVlak 
○ Primjer:  
■ CVP 0,10 0,12 0,15 20,0 10,0 10,0 
○ Opis primjera:  
■ Cijena karte za vožnju normalnim vlakom je 0,10 €/km, za ubrzanim vlakom 
je 0,12 €/km, za brzim vlakom je 0,15 €/km, popust za vožnju vlakom 
subotom i nedjeljom je 20,0%, popust za kupovinu karte putem 
web/mobilne aplikacije je 10,0% i uvećanje za kupovinu karte u vlaku je 
10,0% 




● Kupovina karte za putovanje između dviju stanica određenim vlakom na određeni datum 
s odabranim načinom kupovanja karte 
○ Sintaksa:  
■ KKPV2S oznaka - polaznaStanica - odredišnaStanica - datum - 
načinKupovine 
○ Primjer:  
■ KKPV2S 3609 - Donji Kraljevec - Čakovec - 10.01.2025. - WM 
○ Opis primjera:  
■ Kupovina karte za putovanje vlakom s oznakom 3609 na relaciji Donji 
Kraljevec - Čakovec, za 10.01.2025., a karta se kupuje putem web/mobilne aplikacije. Ostali načini kupovine su: B – blagajna i V – vlak. Na karti moraju 
pisati podaci o vlaku, relaciji, datumu, vremenu kretanja s polazne stanice  
i vremenu dolaska u odredišnu stanicu, izvorna cijena, popusti i konačna 
cijena, način kupovanja karte, datum i vrijeme kupovine karte.


● Ispit kupljenih karata za putovanje vlakom 
○ Sintaksa:  
■ IKKPV [n] 
○ Primjer:  
■ IKKPV 
○ Opis primjera:  
■ Ispit svih kupljenih karata za putovanja vlakom 
○ Primjer:  
■ IKKPV 3 
○ Opis primjera:  
■ Ispit 3. kupljene karte za putovanje vlakom 


● Usporedba karata za putovanje između dviju stanica na određeni datum unutar zadanog 
vremena s odabranim načinom kupovanja karte 
○ Sintaksa:  
■ UKP2S polaznaStanica - odredišnaStanica - datum - odVr - doVr 
načinKupovine 
○ Primjer:  
■ UKP2S Donji Kraljevec - Čakovec - 10.01.2025. - 0:00 - 23:59 - WM 
○ Opis primjera:  
■ Usporedba karata za putovanje vlakom na relaciji Donji Kraljevec – 
Čakovec, za 10.01.2025., s kretanjem s polazne stanice nakon 0:00 i 
dolaskom u odredišnu stanicu prije 23:59, a karta se kupuje putem 
web/mobilne aplikacije. Na svakoj karti moraju pisati podaci o svim 
vlakovima kojima se treba voziti, relaciji pojedinog vlaka, datumu, vremenu 
kretanja s polazne stanice  i vremenu dolaska u odredišnu stanicu pojedine 
relacije, izvorna cijena, popusti i konačna cijena, način kupovanja karte. 
○ Primjeri:  
■ UKP2S Donji Kraljevec - Novi Marof - 10.01.2025. - 08:00 - 16:00 - B 
○ Opis primjera:  
■ Usporedba cijena karti za putovanje vlakom na relaciji Donji Kraljevec – 
Novi Marof, za 10.01.2025., s kretanjem s polazne stanice nakon 8:00 i 
dolaskom u odredišnu stanicu prije 16:00, a karta se kupuje na blagajni.  
○ Primjeri:  
■ UKP2S Donji Kraljevec - Ludbreg - 10.01.2025. - 5:20 - 20:30 - V 
○ Opis primjera:  
■ Usporedba cijena karti za putovanje vlakom na relaciji Donji Kraljevec – 
Ludbreg, za 10.01.2025., s kretanjem s polazne stanice nakon 5:20 i 
dolaskom u odredišnu stanicu prije 20:30, a karta se kupuje u vlaku.


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
  Pattern trainScheduleBetweenStationsPattern = Pattern.compile(
      "^IVI2S (?<startStation>.+) - (?<endStation>.+) - (?<day>.+)"
          + " - (?<fromTime>[0-9]{1,2}:[0-9]{2}) - (?<toTime>[0-9]{1,2}:[0-9]{2}) - (?<format>.+)$");

  Pattern addUserPattern = Pattern.compile("^DK (?<name>[\\p{L}]+(?: [\\p{L}]+)*) (?<lastName>[\\p{L}]+)$");
  Pattern viewUsersPattern = Pattern.compile("^PK$");
  Pattern addTrainObserverPattern = Pattern.compile(
      "^DPK (?<name>[\\p{L}]+(?: [\\p{L}]+)*) (?<lastName>[\\p{L}]+) - (?<trainId>[^-]+?)( - (?<station>.+))?$");

  Pattern simulateTrainPattern = Pattern
      .compile("^SVV (?<trainId>[^-]+?) - (?<day>[\\p{L}]+) - (?<coefficient>[0-9]+)$");

  Pattern ticketPricePattern = Pattern.compile(
      "^CVP (?<normalPrice>[0-9]+,[0-9]+) (?<fastPrice>[0-9]+,[0-9]+) (?<expressPrice>[0-9]+,[0-9]+)"
          + " (?<discountWeekend>[0-9]+,[0-9]+) (?<discountWebMobile>[0-9]+,[0-9]+) (?<trainIncrease>[0-9]+,[0-9]+)$");

  Pattern buyTicketPattern = Pattern.compile(
      "^KKPV2S (?<trainId>[^-]+?) - (?<startStation>.+) - (?<endStation>.+) - (?<date>[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}) - (?<purchaseMethod>.+)$");

  Pattern viewBoughtTicketsPattern = Pattern.compile("^IKKPV( (?<n>[0-9]+))?$");

  Pattern compareTicketsPattern = Pattern.compile(
      "^UKP2S (?<startStation>.+) - (?<endStation>.+) - (?<date>[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}) - (?<fromTime>[0-9]{1,2}:[0-9]{2}) - (?<toTime>[0-9]{1,2}:[0-9]{2}) - (?<purchaseMethod>.+)$");

  Pattern linkPattern = Pattern.compile("^LINK (?<name>[\\p{L}]+(?: [\\p{L}]+)*) (?<lastName>[\\p{L}]+) -"
      + " (?<groupId>.+) - (?<action>.+)$|^LINK PREGLED$");
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
      identifyCommand(command);
    }
    Logs.footer(true);
  }

  public void runCommand(String command) {
    identifyCommand(command);
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
    Matcher addTrainObserverPatternMatcher = addTrainObserverPattern.matcher(command);
    Matcher trainScheduleBetweenStationsMatcher = trainScheduleBetweenStationsPattern.matcher(command);
    Matcher simulateTrainMatcher = simulateTrainPattern.matcher(command);
    Matcher ticketPriceMatcher = ticketPricePattern.matcher(command);
    Matcher ticketPurchaseMatcher = buyTicketPattern.matcher(command);
    Matcher ticketsViewMatcher = viewBoughtTicketsPattern.matcher(command);
    Matcher ticketsCompareMatcher = compareTicketsPattern.matcher(command);
    Matcher linkMatcher = linkPattern.matcher(command);
    if (matchBaseCommands(command, vtMatcher, vsMatcher, vsbMatcher, addUserMatcher, viewUsersMatcher,
        viewTrainsMatcher, viewTrainStagesMatcher, ivrvMatcher, linkMatcher, addTrainObserverPatternMatcher,
        trainScheduleBetweenStationsMatcher, simulateTrainMatcher)) {
      return true;
    } else if (matchTicketCommands(command, ticketPriceMatcher, ticketPurchaseMatcher, ticketsViewMatcher,
        ticketsCompareMatcher)) {
      return true;
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

  private boolean matchBaseCommands(String command, Matcher vtMatcher, Matcher vsMatcher, Matcher vsbMatcher,
      Matcher addUserMatcher, Matcher viewUsersMatcher, Matcher viewTrainsMatcher, Matcher viewTrainStagesMatcher,
      Matcher ivrvMatcher, Matcher linkMatcher, Matcher addTrainObserverPatternMatcher,
      Matcher trainScheduleBetweenStationsMatcher, Matcher simulateTrainMatcher) {
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
    } else if (simulateTrainMatcher.matches()) {
      simulateTrain(simulateTrainMatcher);
    } else
      return false;
    return true;
  }

  private boolean matchTicketCommands(String command, Matcher ticketPriceMatcher, Matcher ticketPurchaseMatcher,
      Matcher ticketsViewMatcher, Matcher ticketsCompareMatcher) {
    if (ticketPriceMatcher.matches()) {
      setTicketPrices(ticketPriceMatcher);
    } else if (ticketPurchaseMatcher.matches()) {
      buyTicket(ticketPurchaseMatcher);
    } else if (ticketsViewMatcher.matches()) {
      viewBoughtTickets(ticketsViewMatcher);
    } else if (ticketsCompareMatcher.matches()) {
      compareTickets(ticketsCompareMatcher);
    } else
      return false;
    return true;
  }

  private void outputMenu() {
    Logs.header("JLF Željeznica: Interaktivni način rada", true);
    Logs.withPadding(() -> Logs.o("Validne komande:"), false, true);

    dz1CommandsMenu();

    trainCommandsMenu();

    userCommandsMenu();

    customCommandsMenu();

    Logs.withPadding(() -> Logs.o("Q - Izlaz iz programa", false), true, true);
    Logs.o("Uzorci dizajna, 2024. - Joshua Lee Fletcher");
  }

  private void dz1CommandsMenu() {
    Logs.o("IP\t\t\t\t\t- Pregled pruga", false);
    Logs.o(
        "ISP [oznakaPruge] [N|O]\t\t\t- Pregled stanica uz prugu u normalnom ili obrnutom redoslijedu",
        false);
    Logs.o("ISI2S [nazivStanice1] - [nazivStanice2]\t- Pregled stanica između dvije stanice",
        false);
    Logs.o("IK [oznakaKompozicije]\t\t\t- Pregled kompozicija", false);
  }

  private void trainCommandsMenu() {
    Logs.o("IV\t\t\t\t\t- Pregled vlakova", false);
    Logs.o("IEV [oznakaVlaka]\t\t\t\t- Pregled etapa vlaka", false);
    Logs.o("IEVD [dani]\t\t\t\t- Pregled vlakova koji voze sve etape na određene dane u tjednu",
        false);
    Logs.o("IVRV [oznakaVlaka]\t\t\t- Pregled vlakova i njihovih etapa", false);
    Logs.o("IVI2S [polaznaStanica] - [odredišnaStanica] - [dan] - [odVr] - [doVr] - [prikaz]", false);
    Logs.o("\t\t\t\t\t\t- Pregled vlakova između dvije stanice na "
        + "određeni dan u tjednu unutar zadanog vremena", false);
  }

  private void userCommandsMenu() {
    Logs.o("DK [ime] [prezime]\t\t\t- Dodavanje korisnika", false);
    Logs.o("PK\t\t\t\t\t- Pregled korisnika", false);
    Logs.withPadding(() -> Logs.o(
        "DPK [ime] [prz] - [oznVlaka] [- stanica]  "
            + "- Dodavanje korisnika za praćenje putovanja vlaka ili dolaska u određenu željezničku stanicu",
        false), true, false);
    Logs.o(
        "SVV [oznakaVlaka] - [dan] - [koeficijent]\t- Simulacija vožnje vlaka na dan u tjednu u koeficijentu vremena",
        false);
  }

  private void customCommandsMenu() {
    Logs.withPadding(() -> Logs.o(
        "LINK [ime] [prz] - [grupa] - [O|Z|poruka] "
            + "- Otvori/zatvori vezu između korisnika i grupe ili pošalji obavijest u grupu",
        false), true, false);
    Logs.o("LINK PREGLED\t\t\t\t- Pregled svih grupa", false);
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
    matcher.matches();
    if (matcher.group("name") == null) {
      viewGroupChatListWithMembers();
    } else {
      String name = matcher.group("name"), lastName = matcher.group("lastName");
      String groupId = matcher.group("groupId"), action = matcher.group("action");
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
    String stStart = ivi2sMatcher.group(1).trim(), stEnd = ivi2sMatcher.group(2).trim();
    String day = ivi2sMatcher.group(3).trim(), format = ivi2sMatcher.group(6).trim();
    String toTime = ivi2sMatcher.group(5).trim(), fromTime = ivi2sMatcher.group(4).trim();
    if (invalidIVI2SParams(stStart, stEnd, day, fromTime, toTime, format))
      return;
    Weekday schDay = Weekday.dayFromString(day);
    ScheduleTime scStart = new ScheduleTime(fromTime), scEnd = new ScheduleTime(toTime);
    Logs.header("Pregled vlakova koji prolaze kroz " + stStart + " - " + stEnd + " (" + schDay + ": "
        + scStart + "-" + scEnd + ")", true);
    var dt = RailwaySingleton.getInstance().getSchedule().commandIVI2S(stStart, stEnd, schDay, scStart, scEnd, format);
    if (dt == null || dt.isEmpty()) {
      Logs.e("Nema pronađenih vlakova");
      return;
    }
    try {
      ivi2sSecondPart(dt, format);
    } catch (Exception e) {
      System.out.println(e);
      Logs.e("Neispravan format prikaza: " + format);
      Logs.footer(true);
      throw e;
    }
    Logs.printTable();
    Logs.footer(true);
  }

  private boolean invalidIVI2SParams(String stStart, String stEnd, String day, String fromTime, String toTime,
      String format) {
    Weekday schDay;
    try {
      schDay = Weekday.dayFromString(day);
    } catch (IllegalArgumentException e) {
      Logs.e("Nepoznata oznaka dana: " + day + "; ova naredba samo prima oznaku jednog dana u tjednu.");
      return true;
    }
    ScheduleTime scStart, scEnd;
    try {
      scStart = new ScheduleTime(fromTime);
      scEnd = new ScheduleTime(toTime);
    } catch (IllegalArgumentException e) {
      Logs.e("Neispravno vrijeme: " + e.getMessage());
      return true;
    }
    if (schDay == null) {
      Logs.e("Nepoznata oznaka dana: " + day);
      return true;
    }
    if (scStart == null || scEnd == null) {
      Logs.e("Neispravno vrijeme: " + fromTime + " - " + toTime);
      return true;
    }
    return false;
  }

  private void ivi2sSecondPart(List<Map<String, String>> data, String displayFormat) {
    data.sort((a, b) -> {
      String kKeyA = a.keySet().stream().filter(k -> k.startsWith("K")).findFirst().orElse("0");
      String kKeyB = b.keySet().stream().filter(k -> k.startsWith("K")).findFirst().orElse("0");
      if (kKeyA == null || kKeyB == null || a.get(kKeyA) == null || b.get(kKeyB) == null)
        return 0;
      double kA = Double.parseDouble(a.get(kKeyA)), kB = Double.parseDouble(b.get(kKeyB));
      return Double.compare(kA, kB);
    });
    List<String> headers = createHeadersFromFormat(displayFormat);
    List<String> finalHeaders = new ArrayList<>();
    List<List<String>> tableRows = new ArrayList<>();
    Set<String> allVKeysInTable = data.stream().flatMap(row -> row.keySet().stream().filter(k -> k.startsWith("V")))
        .collect(Collectors.toSet());
    var newTableRows = new ArrayList<>(data);
    for (Map<String, String> row : data) {
      Map<String, String> newRow = new HashMap<>();
      Map<String, String> vValues = new HashMap<>();
      for (String key : row.keySet()) {
        if (key.startsWith("V"))
          vValues.put(key, row.get(key));
        else
          newRow.put(key, row.get(key));
      }
      for (String vKey : allVKeysInTable)
        newRow.put(vKey, vValues.getOrDefault(vKey, "-"));
      newTableRows.set(newTableRows.indexOf(row), newRow);
    }
    data = newTableRows;
    fixDistances(data, headers, finalHeaders, tableRows, displayFormat);
  }

  private void fixDistances(List<Map<String, String>> data, List<String> headers, List<String> finalHeaders,
      List<List<String>> tableRows, String displayFormat) {
    // get the distance ("K") in the very first row and store it as the starting
    // point for every row, starting from the first, reduce the K value by the K
    // value of the starting point this way, the distance will be calculated from
    // the starting point

    Map<String, String> firstRow = data.get(0);
    double startingDistance = Double.parseDouble(firstRow.get("K"));

    for (Map<String, String> row : data) {
      double currentDistanceForRow = Double.parseDouble(row.get("K"));
      double distanceFromStart = currentDistanceForRow - startingDistance;
      row.put("K", String.valueOf(distanceFromStart));
    }

    ivi2sOutputTable(data, headers, finalHeaders, tableRows, displayFormat);
  }

  private void ivi2sOutputTable(List<Map<String, String>> data, List<String> headers, List<String> finalHeaders,
      List<List<String>> tableRows, String displayFormat) {
    for (Map<String, String> row : data) {
      List<String> tableRow = new ArrayList<>();
      for (String header : headers)
        if (header.equals("V")) {
          List<String> vKeys = row.keySet().stream().filter(k -> k.startsWith("V")).toList();
          for (String key : vKeys) {
            tableRow.add(row.get(key));
            finalHeaders.add(key);
          }
        } else {
          tableRow.add(row.getOrDefault(header, ""));
          finalHeaders.add(header);
        }
      tableRows.add(tableRow);
    }
    finalHeaders = new ArrayList<>();
    for (char c : displayFormat.toCharArray())
      if (c == 'V') {
        data.stream().flatMap(row -> row.keySet().stream().filter(k -> k.startsWith("V"))).distinct()
            .forEach(finalHeaders::add);
      } else
        finalHeaders.add(String.valueOf(c));
    finishIvi2SOutput(finalHeaders, tableRows);
  }

  private void finishIvi2SOutput(List<String> finalHeaders, List<List<String>> tableRows) {
    if (tableRows.isEmpty() || finalHeaders.isEmpty())
      return;

    List<List<Integer>> vColumnGroups = identifyColumnGroups(finalHeaders);
    for (List<Integer> group : vColumnGroups) {
      sortAndReorderGroup(group, finalHeaders, tableRows);
    }
    printTable(finalHeaders, tableRows);
  }

  private List<List<Integer>> identifyColumnGroups(List<String> finalHeaders) {
    List<List<Integer>> vColumnGroups = new ArrayList<>();
    List<Integer> currentGroupIndices = new ArrayList<>();
    List<String> currentGroupHeaders = new ArrayList<>();
    Map<String, Integer> headerIndexMap = new HashMap<>();

    for (int i = 0; i < finalHeaders.size(); i++) {
      String header = finalHeaders.get(i);
      if (header.startsWith("V:")) {
        processVColumnHeader(vColumnGroups, currentGroupIndices, currentGroupHeaders, headerIndexMap, i, header);
      } else {
        finalizeGroupIfNotEmpty(vColumnGroups, currentGroupIndices, currentGroupHeaders, headerIndexMap);
      }
    }
    finalizeGroupIfNotEmpty(vColumnGroups, currentGroupIndices, currentGroupHeaders, headerIndexMap);
    return vColumnGroups;
  }

  private void processVColumnHeader(List<List<Integer>> vColumnGroups, List<Integer> currentGroupIndices,
      List<String> currentGroupHeaders, Map<String, Integer> headerIndexMap, int index, String header) {
    String trainId = header;
    if (headerIndexMap.containsKey(trainId) && headerIndexMap.get(trainId) == currentGroupHeaders.size()) {
      vColumnGroups.add(new ArrayList<>(currentGroupIndices));
      currentGroupIndices.clear();
      currentGroupHeaders.clear();
      headerIndexMap.clear();
    }
    currentGroupIndices.add(index);
    currentGroupHeaders.add(trainId);
    headerIndexMap.put(trainId, currentGroupHeaders.size() - 1);
  }

  private void finalizeGroupIfNotEmpty(List<List<Integer>> vColumnGroups, List<Integer> currentGroupIndices,
      List<String> currentGroupHeaders, Map<String, Integer> headerIndexMap) {
    if (!currentGroupIndices.isEmpty()) {
      vColumnGroups.add(new ArrayList<>(currentGroupIndices));
      currentGroupIndices.clear();
      currentGroupHeaders.clear();
      headerIndexMap.clear();
    }
  }

  private void sortAndReorderGroup(List<Integer> group, List<String> finalHeaders, List<List<String>> tableRows) {
    List<String> groupHeaders = new ArrayList<>();
    List<ScheduleTime> groupTimes = new ArrayList<>();

    for (int index : group) {
      groupHeaders.add(finalHeaders.get(index));
      groupTimes.add(new ScheduleTime(tableRows.get(0).get(index)));
    }

    List<Integer> sortedIndices = getSortedIndices(groupHeaders, groupTimes);
    reorderHeadersAndRows(group, finalHeaders, tableRows, groupHeaders, sortedIndices);
  }

  private List<Integer> getSortedIndices(List<String> groupHeaders, List<ScheduleTime> groupTimes) {
    List<Integer> sortedIndices = new ArrayList<>();
    for (int i = 0; i < groupHeaders.size(); i++) {
      int minIndex = -1;
      ScheduleTime minTime = null;
      for (int j = 0; j < groupHeaders.size(); j++) {
        if (sortedIndices.contains(j)) {
          continue;
        }
        if (minTime == null || groupTimes.get(j).compareTo(minTime) < 0) {
          minTime = groupTimes.get(j);
          minIndex = j;
        }
      }
      sortedIndices.add(minIndex);
    }
    return sortedIndices;
  }

  private void reorderHeadersAndRows(List<Integer> group, List<String> finalHeaders, List<List<String>> tableRows,
      List<String> groupHeaders, List<Integer> sortedIndices) {
    for (int j = 0; j < group.size(); j++) {
      finalHeaders.set(group.get(j), groupHeaders.get(sortedIndices.get(j)));
    }

    for (List<String> row : tableRows) {
      List<String> groupValues = group.stream().map(row::get).collect(Collectors.toList());
      List<String> sortedGroupValues = sortedIndices.stream().map(groupValues::get).collect(Collectors.toList());
      for (int j = 0; j < group.size(); j++) {
        row.set(group.get(j), sortedGroupValues.get(j));
      }
    }
  }

  private void printTable(List<String> finalHeaders, List<List<String>> tableRows) {
    Logs.tableHeader(finalHeaders.stream().map(value -> switch (value) {
      case "S" -> "od";
      case "P" -> "pruga";
      case "K" -> "km";
      case "V" -> "vlak";
      default -> value.startsWith("V:") ? value.substring(2) : value;
    }).toList());
    for (List<String> row : tableRows) {
      Logs.tableRow(row);
    }
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

  private void simulateTrain(Matcher simulateTrainMatcher) {
    String trainId = simulateTrainMatcher.group("trainId").trim();
    String dayStr = simulateTrainMatcher.group("day").trim();
    String coefficientStr = simulateTrainMatcher.group("coefficient").trim();
    Weekday day;
    try {
      day = Weekday.dayFromString(dayStr);
    } catch (IllegalArgumentException e) {
      Logs.e("Nepoznata oznaka dana: " + dayStr);
      return;
    }
    int coefficient;
    try {
      coefficient = Integer.parseInt(coefficientStr);
    } catch (NumberFormatException e) {
      Logs.e("Neispravan koeficijent: " + coefficientStr);
      return;
    }
    TrainComposite train = RailwaySingleton.getInstance()
        .getSchedule()
        .getTrainById(trainId);
    if (train == null) {
      Logs.e("Vlak s oznakom ID " + trainId + " ne postoji.");
      return;
    }
    ScheduleTime currentTime = train.getDepartureTime(day);
    if (currentTime == null) {
      Logs.e("Vlak ne radi danom " + day);
      return;
    }
    GlobalClock.simulate(train, day, currentTime, coefficient);
  }

  private void setTicketPrices(Matcher ticketPriceMatcher) {
    String normalPriceStr = ticketPriceMatcher.group("normalPrice");
    String fastPriceStr = ticketPriceMatcher.group("fastPrice");
    String expressPriceStr = ticketPriceMatcher.group("expressPrice");
    try {
      double normalPrice = ParsingUtil.d(normalPriceStr);
      double fastPrice = ParsingUtil.d(fastPriceStr);
      double expressPrice = ParsingUtil.d(expressPriceStr);
      RailwaySingleton.getInstance().setTicketPrices(normalPrice, fastPrice, expressPrice);
    } catch (NumberFormatException e) {
      Logs.e("Neispravna cijena: " + e.getMessage());
      return;
    }
  }

  private void buyTicket(Matcher ticketPurchaseMatcher) {
  }

  // implement just like before
  private void viewBoughtTickets(Matcher ticketsViewMatcher) {
    Logs.header("Pregled kupljenih karata", true);
    String nStr = ticketsViewMatcher.group("n");
    int n = nStr != null ? Integer.parseInt(nStr) : -1;
    Logs.tableHeader(Arrays.asList("Oznaka", "Vlak", "Polazna stanica", "Odredišna stanica", "Vrijeme polaska",
        "Vrijeme dolaska", "Klasa", "Cijena"));
    List<Ticket> tickets = RailwaySingleton.getInstance().getTickets();
    if (tickets.isEmpty()) {
      Logs.e("Nema kupljenih karata.");
      Logs.footer(true);
      return;
    }
    if (n > 0 && n <= tickets.size()) {
      displayTicket(tickets.get(n - 1));
    } else if (n == -1) {
      for (Ticket ticket : tickets) {
        displayTicket(ticket);
      }
    } else {
      Logs.e("Neispravan broj karte: " + n);
    }
    Logs.footer(true);
  }

  private void displayTicket(Ticket ticket) {
    List<String> row = new ArrayList<>();
    // List<String> row = Arrays.asList(
    //     ticket.id(),
    //     ticket.trainID(),
    //     ticket.startStation(),
    //     ticket.endStation(),
    //     ticket.departureTime(),
    //     ticket.arrivalTime(),
    //     ticket.ticketClass().toString(),
    //     String.valueOf(ticket.price()));
    Logs.tableRow(row);
  }

  private void compareTickets(Matcher ticketsCompareMatcher) {
  }
}