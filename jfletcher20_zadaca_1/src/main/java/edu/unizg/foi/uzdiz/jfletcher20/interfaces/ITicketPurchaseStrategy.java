package edu.unizg.foi.uzdiz.jfletcher20.interfaces;

import edu.unizg.foi.uzdiz.jfletcher20.enums.TicketPurchaseMethod;

public class ITicketPurchaseStrategy {
    public TicketPurchaseMethod purchaseMethod;
    public ITicketPurchaseStrategy() {
    }

    public double calculateTicketPrice(double distance, double basePrice, double priceModifier) {
        return basePrice * priceModifier * distance;
    }
    
}
