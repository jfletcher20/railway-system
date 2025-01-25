package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.ticket;

import java.util.List;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TicketPurchaseMethod;
import edu.unizg.foi.uzdiz.jfletcher20.enums.Weekday;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IPrototype;
import edu.unizg.foi.uzdiz.jfletcher20.models.tickets.Ticket;

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

public class TicketCostParameters implements IPrototype {

    public TicketCostParameters(double weekendTicketDiscount, double webOrMobileTicketDiscount, double trainTicketPriceIncrease,
            double priceNormal, double priceFast, double priceExpress) {
        if (weekendTicketDiscount < 0 || webOrMobileTicketDiscount < 0 || trainTicketPriceIncrease < 0 || priceNormal < 0
                || priceFast < 0 || priceExpress < 0) {
            throw new IllegalArgumentException("TicketSystem::Vrijednosti cijena i popusta ne smiju biti negativne.");
        }
        // if any discount is greater than 100%, throw exception
        if (weekendTicketDiscount > 100 || webOrMobileTicketDiscount > 100) {
            throw new IllegalArgumentException("TicketSystem::Popusti ne smiju biti veći od 100%.");
        }
        this.weekendTicketDiscount = weekendTicketDiscount;
        this.webOrMobileTicketDiscount = webOrMobileTicketDiscount;
        this.trainTicketPriceIncrease = trainTicketPriceIncrease;
        this.priceNormal = priceNormal;
        this.priceFast = priceFast;
        this.priceExpress = priceExpress;
    }

    @Override
    public TicketCostParameters clone() {
        return new TicketCostParameters(weekendTicketDiscount, webOrMobileTicketDiscount, trainTicketPriceIncrease, priceNormal,
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

    public double discount(double modifier) {
        return 1 - modifier / 100;
    }

    public double increase(double modifier) {
        return 1 + modifier / 100;
    }

    public double getPriceNormal() {
        return priceNormal;
    }

    public double getPriceFast() {
        return priceFast;
    }

    public double getPriceExpress() {
        return priceExpress;
    }

    public double getWeekendTicketDiscount() {
        return weekendTicketDiscount;
    }

    public double getWebOrMobileTicketDiscount() {
        return webOrMobileTicketDiscount;
    }

    public double getTrainTicketPriceIncrease() {
        return trainTicketPriceIncrease;
    }

    @Override
    public String toString() {
        return "TicketSystem{" + "weekendTicketDiscount=" + weekendTicketDiscount + "\n, webOrMobileTicketDiscount="
                + webOrMobileTicketDiscount + "\n, trainTicketPriceIncrease=" + trainTicketPriceIncrease + "\n, priceNormal="
                + priceNormal + "\n, priceFast=" + priceFast + ", priceExpress=" + priceExpress + '}';
    }

    public String getDiscounts(Ticket ticket) {
        // return "Vikend popust: -" + (weekendTicketDiscount) + "%, Web/Mobilni popust: -" + webOrMobileTicketDiscount
        //         + "%, Povećanje cijene u vlaku: +" + trainTicketPriceIncrease + "%";

        // should output the discounts in red if they do not apply based on the ticket
        boolean isOnWeekend = ticket.isOnWeekend();
        boolean isWebOrMobile = ticket.purchaseMethod() == TicketPurchaseMethod.WEB_MOBILE;
        boolean isInTrain = ticket.purchaseMethod() == TicketPurchaseMethod.TRAIN;
        String codeForRed = "\u001B[31m";
        String resetCode = "\u001B[0m";

        return (isOnWeekend ? "" : codeForRed) + "Vikend popust: -" + weekendTicketDiscount + "%" + resetCode + ", " +
            (isWebOrMobile ? "" : codeForRed) + "Web/Mobilni popust: -" + webOrMobileTicketDiscount + "%" + resetCode + ", " +
            (isInTrain ? "" : codeForRed) + "Povećanje cijene u vlaku: +" + trainTicketPriceIncrease + "%" + resetCode;
            
    }


}
