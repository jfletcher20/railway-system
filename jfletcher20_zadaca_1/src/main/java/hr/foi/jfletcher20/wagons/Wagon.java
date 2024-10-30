package hr.foi.jfletcher20.wagons;

import hr.foi.jfletcher20.enums.TransportType;
import hr.foi.jfletcher20.enums.DriveType;
import hr.foi.jfletcher20.enums.WagonType;

/*
 * Postoje sljedeći atributi prijevoznih sredstava: namjena (prijevozno sredstvo s vlastitim
 * pogonom, prijevozno sredstvo s vlastitim pogonom za vuču kompozicije prijevoznih sredstava,
 * prijevozno sredstvo bez pogona), vrsta prijevoza (nema, putnička prijevozna sredstva, putnička
 * prijevozna sredstva za spavanje, putnička prijevozna sredstva kao restoran, teretna prijevozna
 * sredstva za automobile, teretna prijevozna sredstva za pakiranu robu u kontejnerima, teretna
 * prijevozna sredstva za robu u rasutom stanju, teretna prijevozna sredstva za robu u tekućem
 * stanju, teretna prijevozna sredstva za robu u plinovitom stanju), vrsta pogona (nema, dizel,
 * baterije, električna struja), maksimalna snaga (-1 (nije poznato), 0,0-10 MW), maksimalna brzina
 * vožnje (1-200 km/h), godina proizvodnje, proizvođač, broj mjesta (sjedećih, stajaćih, kreveta,
 * bicikala, automobila), nosivost (t), zapremina (m3), status (ispravno, u kvaru).
 */

/**
 * Class representing a wagon
 *
 * @param purpose Purpose of the wagon
 * @param transportType Type of transport
 * @param driveType Type of drive
 * @param maxPower Maximum power
 * @param maxSpeed Maximum speed
 * @param yearOfProduction Year of production
 * @param manufacturer Manufacturer
 * @param numberOfSeats Number of seats
 * @param capacity Capacity
 * @param status Status
 */
public record Wagon(WagonType purpose, TransportType transportType, DriveType driveType,
    double maxPower, int maxSpeed, int yearOfProduction, String manufacturer, int numberOfSeats,
    double capacity, boolean status) {
  /**
   * Constructor for Wagon
   * 
   * @param purpose
   * @param transportType
   * @param driveType
   * @param maxPower
   * @param maxSpeed
   * @param yearOfProduction
   * @param manufacturer
   * @param numberOfSeats
   * @param capacity
   * @param status
   */
  public Wagon {
    if (maxPower < -1 || maxPower > 10) {
      throw new IllegalArgumentException(
          "If max power is unknown, set to -1; otherwise, must be in domain of [0-10] MW");
    }
    if (maxSpeed < 1 || maxSpeed > 200) {
      throw new IllegalArgumentException("Max speed must be between in domain of <1-200> km/h");
    }
  }

  /**
   * Method to check if the wagon is powered
   * 
   * @return boolean
   */
  public boolean getIsPowered() {
    return this.driveType != DriveType.NONE;
  }
}
