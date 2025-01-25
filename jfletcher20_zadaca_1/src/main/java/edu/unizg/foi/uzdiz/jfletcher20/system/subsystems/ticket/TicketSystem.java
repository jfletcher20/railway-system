package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IPrototype;

/*

Potrebno je dodati funkcionalnost za kupovinu karata za vožnju putnika. Određivanje 
cijene vožnje putnika vlakom temelji se na €/km za pojedinu vrstu vlaka (normalni, ubrzani, brzi). 
Osnovna cijena karte vrijedi za kupovinu na blagajni.

Tvrtka daje % popusta na cijenu ako se vozi vlakom u subotu i/ili nedjelju.
Tvrtka želi promovirati kupovinu karte putem web/mobilne aplikacije za što daje određeni % popusta na cijenu.
S druge strane tvrtka za kupovinu karte u vlaku određuje % uvećanja cijene.

Izračun cijene karte s obzirom na način kupovine (blagajna, web/mobilna aplikacija, u vlaku) treba se temeljiti na uzorku dizajna Strategy.

Svaku kupovinu karte potrebno je pohraniti kako bi se moglo do nje kasnije pristupiti, a 
treba se temeljiti na uzorku dizajna Memento. */

/*
 * Thus, discounts occur under following condiitons:
 * 1. Weekend discounts
 * 2. Web or Mobile purchase discounts
 * 3. Train ticket price increase
 */

public class TicketSystem implements IPrototype {

    public TicketSystem(double weekendTicketDiscount, double webOrMobileTicketDiscount, double trainTicketPriceIncrease,
            double priceNormal, double priceFast, double priceExpress) {
        if (weekendTicketDiscount < 0 || webOrMobileTicketDiscount < 0 || trainTicketPriceIncrease < 0 || priceNormal < 0
                || priceFast < 0 || priceExpress < 0) {
            throw new IllegalArgumentException("TicketSystem::Vrijednosti cijena i popusta ne smiju biti negativne.");
        }
        // if any discount is greater than 100%, throw exception
        if (weekendTicketDiscount > 100 || webOrMobileTicketDiscount > 100) {
            throw new IllegalArgumentException("TicketSystem::Popusti ne smiju biti veći od 100%.");
        }
        this.weekendTicketDiscount = weekendTicketDiscount * 0.01;
        this.webOrMobileTicketDiscount = webOrMobileTicketDiscount * 0.01;
        this.trainTicketPriceIncrease = trainTicketPriceIncrease * 0.01;
        this.priceNormal = priceNormal;
        this.priceFast = priceFast;
        this.priceExpress = priceExpress;
    }

    @Override
    public TicketSystem clone() {
        return new TicketSystem(weekendTicketDiscount, webOrMobileTicketDiscount, trainTicketPriceIncrease, priceNormal,
                priceFast, priceExpress);
    }

    final List<Weekday> discounts = Weekday.getWeekend();
    private double weekendTicketDiscount = 0, webOrMobileTicketDiscount = 0, trainTicketPriceIncrease = 0;
    private double priceNormal = 0, priceFast = 0, priceExpress = 0;

    public void setWeekendTicketDiscount(double weekendTicketDiscount) {
        this.weekendTicketDiscount = weekendTicketDiscount;
    }

    public void setWebOrMobileTicketDiscount(double webOrMobileTicketDiscount) {
        this.webOrMobileTicketDiscount = webOrMobileTicketDiscount;
    }

    public void setTrainTicketPriceIncrease(double trainTicketPriceIncrease) {
        this.trainTicketPriceIncrease = trainTicketPriceIncrease;
    }

    public void setPriceNormal(double priceNormal) {
        this.priceNormal = priceNormal;
    }

    public void setPriceFast(double priceFast) {
        this.priceFast = priceFast;
    }

    public void setPriceExpress(double priceExpress) {
        this.priceExpress = priceExpress;
    }

    private double discount(double modifier) {
        return 1 - modifier;
    }

    private double increase(double modifier) {
        return 1 + modifier;
    }

    // toString() should show all the ticket system data with newlines
    @Override
    public String toString() {
        return "TicketSystem{" + "weekendTicketDiscount=" + weekendTicketDiscount + "\n, webOrMobileTicketDiscount="
                + webOrMobileTicketDiscount + "\n, trainTicketPriceIncrease=" + trainTicketPriceIncrease + "\n, priceNormal="
                + priceNormal + "\n, priceFast=" + priceFast + ", priceExpress=" + priceExpress + '}';
    }

}
