package edu.unizg.foi.uzdiz.jfletcher20.models.tracks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TraversalDirection;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComponent;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.Schedule;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.Station;
import edu.unizg.foi.uzdiz.jfletcher20.models.stations.StationLeaf;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

public class TrainTrackStageComposite implements IComposite {

    public List<StationLeaf> children = new ArrayList<StationLeaf>();
    public String trackID;
    public Schedule schedule;

    public TrainTrackStageComposite(Schedule schedule) {
        this.schedule = schedule;
        this.trackID = schedule.trackID();
        List<Station> stations = RailwaySingleton.getInstance().getStationsOnTrack(trackID, schedule.trainType());
        if (stations == null) {
            Logs.e("Nepostojeće stanice na traci " + trackID + " za tip vlaka " + schedule.trainType().name());
            return;
        }
        if (schedule.direction() == TraversalDirection.FORTH) {
            this.Add(new StationLeaf(stations.stream()
                    .filter(station -> station.name().equals(schedule.departure().name())
                            && station.supportsTrainType(schedule.trainType()))
                    .findFirst().orElse(stations.get(0))));
            for (int i = stations.indexOf(schedule.departure()) + 1; i < stations.size(); i++) {
                Station station = stations.get(i);
                this.Add(new StationLeaf(station));
                if (station.equals(schedule.destination())) {
                    break;
                }
            }
        } else {
            this.Add(new StationLeaf(stations.reversed().stream()
                    .filter(station -> station.name().equals(schedule.departure().name())
                            && station.supportsTrainType(schedule.trainType()))
                    .findFirst().orElse(stations.get(stations.size() - 1))));
            for (int i = stations.indexOf(schedule.departure()) - 1; i >= 0; i--) {
                Station station = stations.get(i);
                this.Add(new StationLeaf(station));
                if (station.equals(schedule.destination())) {
                    break;
                }
            }
        }
    }

    @Override
    public void Operation() {
        Logs.i("\t\tSTAGE | " + this.trackID + " | " + this.schedule.scheduledTrainID() + " | "
                + this.schedule.departure().name() + " -> " + this.schedule.destination().name() + " | " +
                this.schedule.departureTime().toString() + " -> " + this.toTime().toString());
        for (StationLeaf child : this.children) {
            child.Operation();
        }
    }

    public List<StationLeaf> compileSchedule(Schedule schedule) {
        List<StationLeaf> compatibleLeaves = new ArrayList<StationLeaf>();
        for (StationLeaf child : this.children) {
            if (child.getStation().supportsTrainType(schedule.trainType())) {
                compatibleLeaves.add(child);
            }
        }
        return compatibleLeaves;
    }

    public double compileDistance() {
        return RailwaySingleton.getInstance().getDistanceBetweenStations(schedule);
    }

    public ScheduleTime fromTime() {
        return schedule.departureTime();
    }

    public ScheduleTime toTime() {
        return new ScheduleTime(
                schedule.departureTime().getTotalTimeInMinutes() + schedule.travelTime().getTotalTimeInMinutes());
    }

    @Override
    public int Add(IComponent component) {
        if (!(component instanceof StationLeaf)) {
            Logs.e("Pokušaj dodavanja pogrešnog tipa komponente u TrainTrackStageComposite::Add(): "
                    + component.getClass().getName());
            return 0;
        }
        if (this.children.contains(component)) {
            Logs.e("Pokušaj dodavanja iste komponente u TrainTrackStageComposite::Add()");
            return 0;
        }
        if (((StationLeaf) component).getStation().supportsTrainType(this.schedule.trainType())) {
            return this.children.add((StationLeaf) component) ? 1 : 0;
        } else {
            Logs.e("Nekompatibilna stanica " + ((StationLeaf) component).getStation().name()
                    + "(" + ((StationLeaf) component).getStation().timeForTrainType(this.schedule.trainType()) + ")"
                    + " prilikom poziva TrainTrackStageComposite::Add(component) obzirom na tip vlaka "
                    + this.schedule.trainType().name());
            return 0;
        }
    }

    @Override
    public int Remove(IComponent component) {
        if (!(component instanceof StationLeaf)) {
            Logs.e("Pokušaj uklanjanja pogrešnog tipa komponente iz TrainTrackStageComposite::Remove(): "
                    + component.getClass().getName());
            return 0;
        }
        return this.children.remove(component) ? 1 : 0;
    }

    @Override
    public IComponent GetChild(int index) {
        if (index < 0 || index >= this.children.size()) {
            Logs.e("Indeks je izvan granica liste djece");
            return null;
        }
        return this.children.get(index);
    }

    public List<String> commandIEV() {
        return List.of(
                this.trackID,
                this.schedule.departure().name(),
                this.schedule.destination().name(),
                this.schedule.departureTime().toString(),
                this.toTime().toString(),
                String.valueOf(this.compileDistance()),
                Weekday.listToString(this.schedule.days()));
    }

    public List<String> commandIEVD() {
        return List.of(
                this.trackID,
                this.schedule.departure().name(),
                this.schedule.destination().name(),
                this.schedule.departureTime().toString(),
                this.toTime().toString(),
                // String.valueOf(this.compileDistance()),
                Weekday.listToString(this.schedule.days()));
    }

    public List<Station> getStations() {
        return this.children.stream()
                .map(StationLeaf::getStation)
                .collect(Collectors.toList());
    }

    public ScheduleTime getDepartureTimeAtStation(Station station) {
        int index = getStationIndex(station);
        if (index == -1)
            return null;

        ScheduleTime baseTime = this.schedule.departureTime();
        int minutesToAdd = calculateMinutesToStation(index);

        return baseTime.addMinutes(minutesToAdd);
    }

    private int getStationIndex(Station station) {
        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).getStation().equals(station)) {
                return i;
            }
        }
        return -1;
    }

    private int calculateMinutesToStation(int stationIndex) {
        if (stationIndex == 0)
            return 0;

        int minutes = 0;
        for (int i = 0; i < stationIndex; i++) {
            Station current = this.children.get(i).getStation();
            Station next = this.children.get(i + 1).getStation();
            minutes += calculateTravelTime(current, next);
        }
        return minutes;
    }

    private int calculateTravelTime(Station from, Station to) {
        double distance = from.getDistanceTo(to);
        // Assume average speed of 60 km/h
        return (int) (distance / 60.0 * 60);
    }

    public List<StationLeaf> getCompatibleLeaves() {
        return this.children.stream().filter(child -> child.getStation().supportsTrainType(schedule.trainType()))
                .toList();
    }

    public Map<String, String> commandIVI2S(String displayFormat) {
        // need to return a map of values according to IVI2S output format - available display controls are: S, P, K, V
        // S maps to station name, P maps to track ID, K maps to distance, V maps to departure time
        return Map.of(
                "S", this.schedule.departure().name(),
                "P", this.trackID,
                "K", String.valueOf(this.compileDistance()),
                "V", this.schedule.departureTime().toString()
        );
    }

    public boolean hasStation(String station) {
        return this.children.stream().anyMatch(child -> child.getStation().name().equals(station));
    }

}

/*
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
9 
Kolegij: Uzorci dizajna 
Akademska godina: 2024./2025. 
polaska sa željezničke stanice. U 1. primjeru su stanice koje su na istoj 
pruzi, na 2. primjeru su željezničke stanice koje su na dvije pruge, a na 3. 
primjeru su željezničke stanice koje su na tri pruge. Vlakovi se ispisuju u 
kronološkom redoslijedu vremena polaska vlaka s njegove polazne 
željezničke stanice. Slika 1 prikazuje djelomični izvod iz voznog reda od 
željezničke stanice Zabok do druge željezničke stanice Gornja Stubica za 
ponedjeljak od vremena 5:00 do vremena 12:00 uz oznake KSV. Na slici 
treba zanemariti oznake dana u tjednu.
 */