package edu.unizg.foi.uzdiz.jfletcher20.models.users;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IObserver;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

/*
 * 
 *  Dodavanje korisnika u registar korisnika 
○ Sintaksa:  
■ DK ime prezime 
○ Primjer:  
■ DK Pero Kos 
○ Opis primjera:  
■ Dodaje se korisnik 
● Pregled korisnika iz registra korisnika 
○ Sintaksa:  
■ PK  
○ Primjer:  
■ PK  
○ Opis primjera:  
■ Ispis korisnika
 */
public record User(
        String name, // Ime korisnika
        String lastName // Prezime korisnika
) implements IObserver {
    public User {
        if (name == null || lastName == null) {
            throw new IllegalArgumentException("Ime i prezime korisnika ne smiju biti null");
        } else if (name.isBlank()) {
            throw new IllegalArgumentException("Ime korisnika ne smije biti prazno");
        } else if (lastName.isBlank()) {
            throw new IllegalArgumentException("Prezime korisnika ne smije biti prazno");
        }
    }

    public User(String fullName) {
        this(fullName.split(" ", -1)[0], fullName.split(" ", -1)[1]);
    }

    @Override
    public void update(String trainID, String stationName) {
        Logs.u(this, trainID, stationName);
    }

    @Override
    public String toString() {
        return name.substring(0, 1).toUpperCase() + ". " + lastName;
    }

}
