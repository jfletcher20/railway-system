package edu.unizg.foi.uzdiz.jfletcher20.models.wagons;

import edu.unizg.foi.uzdiz.jfletcher20.enums.WagonType;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;

/*
 * Postoje sljedeći atributi prijevoznih sredstava: **namjena** (prijevozno sredstvo s vlastitim
 * pogonom, prijevozno sredstvo s vlastitim pogonom za vuču kompozicije prijevoznih sredstava,
 * prijevozno sredstvo bez pogona), **vrsta prijevoza** (nema, putnička prijevozna sredstva,
 * putnička prijevozna sredstva za spavanje, putnička prijevozna sredstva kao restoran, teretna
 * prijevozna sredstva za automobile, teretna prijevozna sredstva za pakiranu robu u kontejnerima,
 * teretna prijevozna sredstva za robu u rasutom stanju, teretna prijevozna sredstva za robu u
 * tekućem stanju, teretna prijevozna sredstva za robu u plinovitom stanju), **vrsta pogona** (nema,
 * dizel, baterije, električna struja), **maksimalna snaga** (-1 (nije poznato), 0,0-10 MW),
 * **maksimalna brzina vožnje** (1-200 km/h), **godina proizvodnje**, **proizvođač**, **broj
 * mjesta** (sjedećih, stajaćih, kreveta, bicikala, automobila), **nosivost** (t), **zapremina**
 * (m3), **status** (ispravno, u kvaru -- "I" ili "K"). dodatno trebaju oznaka (string), opis
 * (string), površina (double)
 */

/**
 * Class representing a wagon
 *
 * @param id wagon unique identifier
 * @param description wagon description
 * @param purpose Purpose of the wagon
 * @param transportType Type of transport
 * @param driveType Type of drive
 * @param maxPower Maximum power
 * @param maxSpeed Maximum speed
 * @param yearOfProduction Year of production
 * @param manufacturer Manufacturer
 * @param numberOfSeats Number of seats
 * @param capacity Capacity
 * @param area Area
 * @param volume Volume
 * @param status Status
 */
public record Wagon(String id, // oznaka
    String description, // opis
    WagonType purpose, // namjena
    /* TransportType */ String transportType, // vrsta prijevoza
    /* DriveType */ String driveType, // vrsta pogona
    double maxPower, // maksimalna snaga
    int maxSpeed, // maksimalna brzina vožnje
    int yearOfProduction, // godina proizvodnje
    String manufacturer, // proizvođač
    int numberOfSeats, // broj sjedećih mjesta
    int numberOfStandingPlaces, // broj stajaćih mjesta
    int numberOfBicycles, // broj bicikala
    int numberOfBeds, // broj kreveta
    int numberOfCars, // broj automobila
    double capacity, // nosivost
    double area, // površina
    double volume, // zapremina
    boolean status // status
) implements IProduct {
  /**
   * Constructor for Wagon
   * 
   * @param id
   * @param description
   * @param purpose
   * @param transportType
   * @param driveType
   * @param maxPower
   * @param maxSpeed
   * @param yearOfProduction
   * @param manufacturer
   * @param numberOfSeats
   * @param capacity
   * @param area
   * @param volume
   * @param status
   */
  public Wagon {
    if (maxPower < -1 || maxPower > 10) {
      throw new IllegalArgumentException(
          "Ako je maksimalna snaga nepoznata, postavi na -1; u protivnom, mora biti u domeni [0-10] MW");
    }
    if (maxSpeed < 1 || maxSpeed > 200) {
      throw new IllegalArgumentException("Maksimalna brzina treba biti u domeni od <1-200> km/h");
    }
  }

  /**
   * Method to check if the wagon is powered for traction
   * 
   * @return boolean
   */
  public boolean getIsPowered() {
    return purpose == WagonType.SELF_POWERED_FOR_TRACTION;
  }

  /**
   * Method to manage the wagon's display
   */
  @Override
  public String toString() {
    return "Wagon{" + "id='" + id + '\'' + ", description='" + description + '\'' + ", purpose="
        + purpose + ", transportType='" + transportType + '\'' + ", driveType='" + driveType + '\''
        + ", maxPower=" + maxPower + ", maxSpeed=" + maxSpeed + ", yearOfProduction="
        + yearOfProduction + ", manufacturer='" + manufacturer + '\'' + ", numberOfSeats="
        + numberOfSeats + ", numberOfStandingPlaces=" + numberOfStandingPlaces
        + ", numberOfBicycles=" + numberOfBicycles + ", numberOfBeds=" + numberOfBeds
        + ", numberOfCars=" + numberOfCars + ", capacity=" + capacity + ", area=" + area
        + ", volume=" + volume + ", status=" + status + '}';
  }
}
