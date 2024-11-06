package edu.unizg.foi.uzdiz.jfletcher20.models.compositions;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;

public record TrainComposition(int ID, // Oznaka
    String wagonID, // Oznaka prijevoznog sredstva
    String role // Uloga
) implements IProduct {
  public TrainComposition {
    if (ID < 0)
      throw new IllegalArgumentException("ID mora biti veći ili jednak 0.");
    if (wagonID == null || wagonID.isBlank())
      throw new IllegalArgumentException("Oznaka prijevoznog sredstva mora biti postavljena.");
    if (role == null || role.isBlank())
      throw new IllegalArgumentException("Uloga mora biti postavljena.");
  }
}

/*
 * Example data from CSV file:
 * 
 * Oznaka;Oznaka prijevoznog sredstva;Uloga 8001;D2044-1;P 8001;VP-1;V 8001;VP-2;V 8001;VP-3;V ;;
 * 8002;D2044-2;P 8002;VP-4;V 8002;VP-5;V
 */
