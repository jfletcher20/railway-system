package hr.foi.jfletcher20.enums;

/**
 * Enum for wagon type
 */
public enum WagonType {
  SELF_POWERED, SELF_POWERED_FOR_TRACTION, UNPOWERED;
  
  /**
   * Map Croatian acronyms or English text to the appropriate enum values:
   * PSVP -> SELF_POWERED
   * PSVPVK -> SELF_POWERED_FOR_TRACTION
   * PSBP -> UNPOWERED
   */
  public static WagonType fromCSV(String value) {
    switch (value) {
      case "SELF_POWERED":
        return SELF_POWERED;
      case "SELF_POWERED_FOR_TRACTION":
        return SELF_POWERED_FOR_TRACTION;
      case "UNPOWERED":
        return UNPOWERED;
      case "PSVP":
        return SELF_POWERED;
      case "PSVPVK":
        return SELF_POWERED_FOR_TRACTION;
      case "PSBP":
        return UNPOWERED;
      default:
        System.out.println("Error: Nije prepoznat tip vagona: " + value);
        return null;
    }
  }
}
