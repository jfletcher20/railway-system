package edu.unizg.foi.uzdiz.jfletcher20.models.tickets;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TicketPurchaseMethod;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITicketPriceStrategy;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.railway.RailwaySingleton;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket.TicketCostParameters;

/*
 * ● Kupovina karte za putovanje između dviju stanica određenim vlakom na određeni datum 
s odabranim načinom kupovanja karte 
○ Sintaksa:  
■ KKPV2S oznaka - polaznaStanica - odredišnaStanica - datum - 
načinKupovine 
○ Primjer:  
■ KKPV2S 3609 - Donji Kraljevec - Čakovec - 10.01.2025. - WM 
○ Opis primjera:  
■ Kupovina karte za putovanje vlakom s oznakom 3609 na relaciji Donji 
Kraljevec - Čakovec, za 10.01.2025., a karta se kupuje putem web/mobilne 
12 
Kolegij: Uzorci dizajna 
Akademska godina: 2024./2025. 
aplikacije. Ostali načini kupovine su: B – blagajna i V – vlak. 

Na karti moraju pisati:
    ✅ podaci o vlaku,
    ✅ relaciji,
    ✅ datumu,
    ✅ vremenu kretanja s polazne stanice i vremenu dolaska u odredišnu stanicu,
    ☒ izvorna cijena,
    ☒ popusti
    ☒ i konačna cijena,
    ✅ način kupovanja karte,
    ✅ datum i vrijeme kupovine karte. 
 */
public record Ticket(
        String trainId, // oznaka vlaka
        String departureStation, // polazna stanica
        String arrivalStation, // odredišna stanica
        LocalDate departureDate, // datum polaska
        Date purchaseDate, // datum kupovine
        TicketPurchaseMethod purchaseMethod, // B - blagajna, WM - web/mobilna aplikacija, V - vlak
        TicketCostParameters ticketCostParameters, // cijene i popusti u trenutku kupovine
        ITicketPriceStrategy priceCalculationStrategy // strategija izračuna cijene
) {

    public Ticket {
        if (purchaseMethod == null) {
            throw new IllegalArgumentException("Metoda kupnje karte mora biti definirana.");
        } else if (ticketCostParameters == null) {
            throw new IllegalArgumentException("Parametri cijene karte moraju biti definirani.");
        } else if (priceCalculationStrategy == null) {
            throw new IllegalArgumentException("Strategija izračuna cijene karte mora biti definirana.");
        }
    }

    // return the ticket data as a list of strings of the eticket data
    public List<String> getTicketData() {
        return List.of(
                trainDisplayData(),
                wrap(departureTime()) + departureStation + " - " + wrap(arrivalTime()) + arrivalStation,
                departureDateDisplay(),
                purchaseMethod.toString() //
        );
    }

    public double distance() {
        return RailwaySingleton.getInstance().getSchedule().getTrainById(trainId).getDistanceBetweenStations(departureStation, arrivalStation);
    }

    public String trainDisplayData() {
        TrainComposite train = RailwaySingleton.getInstance().getSchedule().getTrainById(trainId);
        return trainId + "::" + train.getTrainType().displayName() + " vlak";
    }

    public String departureDateDisplay() {
        return departureDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));
    }

    public String purchaseDateDisplay() {
        return purchaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
    }

    public ScheduleTime departureTime() {
        var train = RailwaySingleton.getInstance().getSchedule().getTrainById(trainId);
        return train.getDepartureTimeAtStation(departureStation);
    }

    public ScheduleTime arrivalTime() {
        var train = RailwaySingleton.getInstance().getSchedule().getTrainById(trainId);
        return train.getArrivalTimeAtStation(arrivalStation);
    }

    private String wrap(ScheduleTime time) {
        return time == null ? "" : // colorize the time in green and reset after
                "\u001B[0m" + "[" + "\u001B[32m" + time.toString() + "\u001B[0m" + "] ";
    }

    double pricePerKm(TrainType trainType) {
        switch (trainType) {
            case TrainType.NORMAL: return ticketCostParameters.getPriceNormal();
            case TrainType.FAST: return ticketCostParameters.getPriceFast();
            case TrainType.EXPRESS: return ticketCostParameters.getPriceExpress();
            default: throw new IllegalArgumentException("Nepoznat tip vlaka: " + trainType);
        }
    }

    public Map<String, String> getTicketPurchaseData() {
        return Map.of(
                "Metoda izračuna cijene", priceCalculationStrategy.getClass().getSimpleName(),
                "Ukupna udaljenost", "" + distance(),
                "Originalna cijena", "" + (this.getOriginalPrice()),
                "Popusti", ticketCostParameters.getDiscounts(this).toString(),
                "Konačna cijena", this.getPrice() + "",
                "Datum kupovine", this.purchaseDateDisplay() //
        );
    }

    private double getOriginalPrice() {
        return priceCalculationStrategy.calculateOriginalTicketPrice(this);
    }

    private double getPrice() {
        return priceCalculationStrategy.calculateTicketPrice(this);
    }

    public TrainComposite getTrain() {
        return RailwaySingleton.getInstance().getSchedule().getTrainById(trainId);
    }

    public boolean isOnWeekend() {
        return departureDate.getDayOfWeek().getValue() >= 6;
    }

}
