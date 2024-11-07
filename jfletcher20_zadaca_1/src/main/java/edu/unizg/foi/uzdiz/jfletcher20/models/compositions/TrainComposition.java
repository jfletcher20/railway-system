package edu.unizg.foi.uzdiz.jfletcher20.models.compositions;

import java.util.List;
import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IProduct;
import edu.unizg.foi.uzdiz.jfletcher20.models.wagons.Wagon;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;

public record TrainComposition(int trainId, // Oznaka
    String wagonId, // Oznaka prijevoznog sredstva
    String role // Uloga
) implements IProduct {
  public TrainComposition {
    if (trainId < 0)
      throw new IllegalArgumentException(
          "Oznaka kompozicije mora biti broj koji je veći ili jednak 0.");
    if (wagonId == null || wagonId.isBlank())
      throw new IllegalArgumentException("Oznaka prijevoznog sredstva mora biti postavljena.");
    if (role == null || role.isBlank())
      throw new IllegalArgumentException("Uloga mora biti postavljena.");
  }

  public List<Wagon> getTrainWagons() {
    return RailwaySingleton.getInstance().getWagonsInTrain(trainId);
  }

  public Wagon getWagon() {
    return RailwaySingleton.getInstance().getWagon(wagonId);
  }

  public List<Wagon> getTrainLocomotiveWagons() {
    return getTrainWagons().stream().filter(w -> w.getIsPowered()).toList();
  }

  public List<TrainComposition> getTrainCompositions() {
    return RailwaySingleton.getInstance().getCompositionsInTrain(trainId);
  }

  public List<TrainComposition> getTrainCompositionsByRole(String role) {
    return getTrainCompositions().stream().filter(c -> c.role.equals(role)).toList();
  }

  public List<Wagon> getTrainWagonsWithRole(String role) {
    List<TrainComposition> roleCompositions = getTrainCompositionsByRole(role);
    return roleCompositions.stream().map(c -> c.getWagon()).toList();
  }

  public List<Wagon> getTrainWagonsWithDriveRole() {
    List<Wagon> driveWagons = getTrainWagonsWithRole("P");
    // check that all drive wagons are locomotives
    if (driveWagons.stream().allMatch(w -> w.getIsPowered())) {
      // check that all drive wagons are at the start of the composition
      for (int i = 0; i < driveWagons.size(); i++) {
        if (!getTrainWagons().get(i).equals(driveWagons.get(i))) {
          Logs.e("Ukloniti će se kompozicija " + trainId + " jer nisu sve lokomotive u kompoziciji "
              + trainId + " na početku kompozicije: " + driveWagons.get(i).id()
              + " nije na početku kompozicije.");
          RailwaySingleton.getInstance().removeComposition(trainId);
          return null;
        }
      }
      return driveWagons;
    } else {
      Logs.e(
          "Ukloniti će se kompozicija " + trainId + " jer nisu sva prijevozna sredstva kompozicije "
              + trainId + " s ulogom 'P' lokomotive: "
              + driveWagons.stream().filter(w -> !w.getIsPowered()).map(w -> w.id()).toList());
      RailwaySingleton.getInstance().removeComposition(trainId);
      return null;
    }
  }
}

/*
 * Example data from CSV file:
 * 
 * Oznaka;Oznaka prijevoznog sredstva;Uloga 8001;D2044-1;P 8001;VP-1;V 8001;VP-2;V 8001;VP-3;V ;;
 * 8002;D2044-2;P 8002;VP-4;V 8002;VP-5;V
 */
