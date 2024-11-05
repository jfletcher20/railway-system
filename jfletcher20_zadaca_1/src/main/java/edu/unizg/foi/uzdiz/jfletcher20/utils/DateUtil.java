package edu.unizg.foi.uzdiz.jfletcher20.utils;

public class DateUtil {
  private String date;

  public DateUtil(String date) {
    this.date = date;
  }

  /**
   * Method for getting the date display in a consistent format (dd.mm.yyyy. hh:mm:ss)
   */
  @Override
  public String toString() {
    if (date.length() == 4 || (date.length() == 5 && date.charAt(4) == '.')) {
      int year = 0;
      try {
        year = Integer.parseInt(date);
        return "01.01." + year + ". 00:00:00";
      } catch (NumberFormatException e) {
      }
    } else if (date.length() == 7 || (date.length() == 8 && date.charAt(7) == '.')) {
      int year = 0;
      int month = 0;
      try {
        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(5));
        return "01." + month + "." + year + ". 00:00:00";
      } catch (NumberFormatException e) {
      }
    } else if (date.length() == 10 || (date.length() == 11 && date.charAt(10) == '.')) {
      return date + ". 00:00:00";
    } else if (date.length() == 16 || (date.length() == 17 && date.charAt(16) == '.')) {
      return date + ":00";
    } else if (date.length() == 20) {
      return date;
    }
    return date;
  }
}
