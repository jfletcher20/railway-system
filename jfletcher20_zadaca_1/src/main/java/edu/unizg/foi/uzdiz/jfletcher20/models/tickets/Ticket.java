package edu.unizg.foi.uzdiz.jfletcher20.models.tickets;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TicketPurchaseMethod;
import edu.unizg.foi.uzdiz.jfletcher20.enums.TrainType;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITicketPriceStrategy;
import edu.unizg.foi.uzdiz.jfletcher20.models.compositions.TrainComposite;
import edu.unizg.foi.uzdiz.jfletcher20.models.schedule.ScheduleTime;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.railway.RailwaySingleton;
import edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket.TicketCostParameters;

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
        } else if (trainId == null || trainId.isBlank()) {
            throw new IllegalArgumentException("Oznaka vlaka mora biti definirana.");
        } else if (departureStation == null || departureStation.isBlank()) {
            throw new IllegalArgumentException("Polazna stanica mora biti definirana.");
        } else if (arrivalStation == null || arrivalStation.isBlank()) {
            throw new IllegalArgumentException("Odredišna stanica mora biti definirana.");
        } else if (departureDate == null) {
            throw new IllegalArgumentException("Datum polaska mora biti definiran.");
        } else if (purchaseDate == null) {
            throw new IllegalArgumentException("Datum kupovine mora biti definiran.");
        }

        TrainComposite train = RailwaySingleton.getInstance().getSchedule().getTrainById(trainId);
        Weekday weekday = Weekday.fromDayOfWeek(departureDate.getDayOfWeek());
        if (!train.operatesOnDay(weekday)) {
            throw new IllegalArgumentException("Vlak ne vozi na danu tjedna " + weekday);
        } else if (!train.hasStationsOnDay(weekday, List.of(departureStation, arrivalStation))) {
            throw new IllegalArgumentException("Vlak ne vozi tom relacijom na danu tjedna " + weekday);
        }
    }

    // return the ticket data as a list of strings of the ticket data
    public List<String> getTicketData() {
        return List.of(
                trainDisplayData(),
                wrap(departureTime()) + departureStation + " - " + wrap(arrivalTime()) + arrivalStation,
                departureDateDisplay(),
                purchaseMethod.toString() //
        );
    }

    public double distance() {
        var train = RailwaySingleton.getInstance().getSchedule().getTrainById(trainId);
        return train.getDistanceBetweenStations(departureStation, arrivalStation);
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
        // return train.getDepartureTimeAtStation(departureStation);
        return train.getDepartureTimeAtStation(departureStation, Weekday.fromDayOfWeek(departureDate.getDayOfWeek()));
    }

    public ScheduleTime arrivalTime() {
        var train = RailwaySingleton.getInstance().getSchedule().getTrainById(trainId);
        return train.getArrivalTimeAtStation(arrivalStation, Weekday.fromDayOfWeek(departureDate.getDayOfWeek()));
    }

    private String wrap(ScheduleTime time) {
        // had to remove colorizing time because it caused problems when displaying in
        // table
        // since each of the characters counting towards colorization would be counted
        // as a length-significant character
        // return time == null ? "" : // colorize the time in green and reset after
        // "\u001B[0m" + "[" + "\u001B[32m" + time.toString() + "\u001B[0m" + "] ";
        return time == null ? "" : "[" + time.toString() + "] ";
    }

    double pricePerKm(TrainType trainType) {
        switch (trainType) {
            case TrainType.NORMAL:
                return ticketCostParameters.getPriceNormal();
            case TrainType.FAST:
                return ticketCostParameters.getPriceFast();
            case TrainType.EXPRESS:
                return ticketCostParameters.getPriceExpress();
            default:
                throw new IllegalArgumentException("Nepoznat tip vlaka: " + trainType);
        }
    }

    public Map<String, String> getTicketPurchaseData() {
        // numbers should be rounded to 2 decimals
        return Map.of(
                // "Ukupna udaljenost", "" + String.format("%.2f", distance()),
                "Izvorna cijena", String.format("%.2f", this.getOriginalPrice()) + " €",
                "Konačna cijena", String.format("%.2f", this.getPrice()) + " €",
                "Datum kupovine", this.purchaseDateDisplay(), //
                "Popusti i dodatak na cijenu u vlaku", ticketCostParameters.getDiscounts(this).toString() //
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
