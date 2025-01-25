package edu.unizg.foi.uzdiz.jfletcher20.enums;

import java.util.Map;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.ITicketPriceStrategy;
import edu.unizg.foi.uzdiz.jfletcher20.models.tickets.InTrainPriceStrategy;
import edu.unizg.foi.uzdiz.jfletcher20.models.tickets.TicketBoothPriceStrategy;
import edu.unizg.foi.uzdiz.jfletcher20.models.tickets.WebMobilePriceStrategy;

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
aplikacije. Ostali načini kupovine su: B – blagajna i V – vlak. Na karti moraju 
pisati podaci o vlaku, relaciji, datumu, vremenu kretanja s polazne stanice  
i vremenu dolaska u odredišnu stanicu, izvorna cijena, popusti i konačna 
cijena, način kupovanja karte, datum i vrijeme kupovine karte. 
 */
public enum TicketPurchaseMethod {
    WEB_MOBILE, TRAIN, TICKET_BOOTH;

    public static TicketPurchaseMethod fromString(String value) {
        switch (value) {
            case "B":
                return TICKET_BOOTH;
            case "WM":
                return WEB_MOBILE;
            case "V":
                return TRAIN;
            default:
                throw new IllegalArgumentException("Nepoznat način kupovine karte: " + value);
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case TICKET_BOOTH:
                return "Na blagajni";
            case WEB_MOBILE:
                return "Putem web/mobilne aplikacije";
            case TRAIN:
                return "U vlaku";
            default:
                throw new IllegalArgumentException("Nepoznat način kupovine karte: " + this);
        }
    }

    public static Map<TicketPurchaseMethod, ITicketPriceStrategy> strats = Map.of(
        TICKET_BOOTH, new TicketBoothPriceStrategy(),
        WEB_MOBILE, new WebMobilePriceStrategy(),
        TRAIN, new InTrainPriceStrategy()
    );

    public ITicketPriceStrategy getStrategy() {
        return strats.get(this);
    }
}
