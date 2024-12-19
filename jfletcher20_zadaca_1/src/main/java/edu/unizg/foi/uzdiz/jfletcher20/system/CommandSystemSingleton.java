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
import edu.unizg.foi.uzdiz.jfletcher20.models.tracks.TrainTrack;
import edu.unizg.foi.uzdiz.jfletcher20.models.users.User;
import edu.unizg.foi.uzdiz.jfletcher20.utils.ParsingUtil;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;

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
      "^IVI2S\\s+([^-]+)\\s*-\\s*([^-]+)\\s*-\\s*([^-]+)\\s*-\\s*([^-]+)\\s*-\\s*([^-]+)\\s*-\\s*([^-]+)$");

  Pattern addUserPattern = Pattern.compile("^DK (?<name>[\\p{L}]+(?: [\\p{L}]+)*) (?<lastName>[\\p{L}]+)$");
  Pattern viewUsersPattern = Pattern.compile("^PK$");
  Pattern addTrainObserverPattern = Pattern.compile(
      "^DPK (?<name>[\\p{L}]+(?: [\\p{L}]+)*) (?<lastName>[\\p{L}]+) - (?<trainId>[^-]+?)( - (?<station>.+))?$");

  Pattern simulateTrainPattern = Pattern
      .compile("^SVV (?<trainId>[^-]+?) - (?<day>[\\p{L}]+) - (?<coefficient>[0-9]+)$");

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
    Matcher linkMatcher = linkPattern.matcher(command);
    Matcher addTrainObserverPatternMatcher = addTrainObserverPattern.matcher(command);
    Matcher trainScheduleBetweenStationsMatcher = trainScheduleBetweenStationsPattern.matcher(command);
    Matcher simulateTrainMatcher = simulateTrainPattern.matcher(command);
    if (matchBaseCommands(command, vtMatcher, vsMatcher, vsbMatcher, addUserMatcher, viewUsersMatcher,
        viewTrainsMatcher, viewTrainStagesMatcher, ivrvMatcher, linkMatcher, addTrainObserverPatternMatcher,
        trainScheduleBetweenStationsMatcher, simulateTrainMatcher)) {
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
      Logs.e("Neispravan format prikaza: " + format);
      Logs.footer(true);
      return;
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
    if (tableRows.isEmpty() || finalHeaders.isEmpty()) {
      return; // Exit early if there are no rows or headers.
    }

    // Step 1: Extract the map of header-to-ScheduleTime for the first row
    Map<String, ScheduleTime> headerToTimeMap = new HashMap<>();
    for (int i = 0; i < finalHeaders.size(); i++) {
      String header = finalHeaders.get(i);
      if (header.startsWith("V:")) { // Only consider train headers
        headerToTimeMap.put(header, new ScheduleTime(tableRows.get(0).get(i)));
      }
    }

    // Step 2: Reorder train headers based on their ScheduleTime (earliest to
    // latest)
    List<String> reorderedTrainHeaders = headerToTimeMap.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(ScheduleTime::compareTo))
        .map(Map.Entry::getKey)
        .toList();

    // Step 3: Compute the new order of indexes for all headers
    List<String> reorderedHeaders = new ArrayList<>(finalHeaders);
    int trainIndex = 0;
    for (int i = 0; i < finalHeaders.size(); i++) {
      if (finalHeaders.get(i).startsWith("V:")) {
        reorderedHeaders.set(i, reorderedTrainHeaders.get(trainIndex++));
      }
    }

    // Step 4: Reorder the columns in all rows based on the new header order
    List<Integer> newIndexOrder = new ArrayList<>();
    for (String header : reorderedHeaders) {
      newIndexOrder.add(finalHeaders.indexOf(header));
    }

    List<List<String>> reorderedTableRows = tableRows.stream()
        .map(row -> newIndexOrder.stream()
            .map(row::get)
            .toList())
        .toList();

    // Step 5: Set the table header and log rows
    Logs.tableHeader(reorderedHeaders.stream().map(value -> switch (value) {
      case "S" -> "od";
      case "P" -> "pruga";
      case "K" -> "km";
      case "V" -> "vlak";
      default -> value.startsWith("V:") ? value.substring(2) : value;
    }).toList());

    for (List<String> row : reorderedTableRows) {
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
}