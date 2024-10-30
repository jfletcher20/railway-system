package hr.foi.jfletcher20.wagons;

/*
 * Postoje sljedeći atributi prijevoznih sredstava: namjena (prijevozno sredstvo s vlastitim pogonom, 
prijevozno sredstvo s vlastitim pogonom za vuču kompozicije prijevoznih sredstava, prijevozno 
sredstvo bez pogona), vrsta prijevoza (nema, putnička prijevozna sredstva, putnička prijevozna 
sredstva za spavanje, putnička prijevozna sredstva kao restoran, teretna prijevozna sredstva za 
automobile, teretna prijevozna sredstva za pakiranu robu u kontejnerima, teretna prijevozna 
sredstva za robu u rasutom stanju, teretna prijevozna sredstva za robu u tekućem stanju, teretna 
prijevozna sredstva za robu u plinovitom stanju), vrsta pogona (nema, dizel, baterije, električna 
struja), maksimalna snaga (-1 (nije poznato), 0,0-10 MW), maksimalna brzina vožnje (1-200 
km/h), godina proizvodnje, proizvođač, broj mjesta (sjedećih, stajaćih, kreveta, bicikala, 
automobila), nosivost (t), zapremina (m3), status (ispravno, u kvaru).
 */

public record Wagon(
    String purpose,
    String transportType,
    String driveType,
    double maxPower,
    int maxSpeed,
    int yearOfProduction,
    String manufacturer,
    int numberOfSeats,
    double capacity,
    String status
) {

}
