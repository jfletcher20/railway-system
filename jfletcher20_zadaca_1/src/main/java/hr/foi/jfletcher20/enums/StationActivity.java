package hr.foi.jfletcher20.enums;

public enum StationActivity {
  PASSENGER, CARGO, BOTH;

  public static StationActivity fromCSV(String passengerActivity, String cargoActivity) {
    boolean isPassenger = passengerActivity.equals("DA");
    boolean isCargo = cargoActivity.equals("DA");
    if (isPassenger && isCargo) {
      return BOTH;
    } else if (isPassenger) {
      return PASSENGER;
    } else if (isCargo) {
      return CARGO;
    }
    return null;
  }
}
