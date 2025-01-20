package edu.unizg.foi.uzdiz.jfletcher20.utils;

import java.util.Calendar;
import java.util.Date;

public class ParsingUtil {

  public static Integer i(String value) {
    return value.isEmpty() ? null : Integer.parseInt(value);
  }

  public static Boolean b(String value) {
    return value.isEmpty() ? null : Boolean.parseBoolean(value);
  }

  public static Float f(String value) {
    value = value.replace(",", ".");
    return value.isEmpty() ? null : Float.parseFloat(value);
  }

  public static Double d(String value) {
    value = value.replace(",", ".");
    return value.isEmpty() ? null : Double.parseDouble(value);
  }

  private enum MONTH_DAYS {
    JANUARY(31), FEBRUARY(28), MARCH(31), APRIL(30), MAY(31), JUNE(30), JULY(31), AUGUST(31), SEPTEMBER(30), OCTOBER(31), NOVEMBER(30), DECEMBER(31);

    private final int days;

    MONTH_DAYS(int days) {
      this.days = days;
    }

    public int getDays() {
      return days;
    }
  }

  public static Date dt(String value) {
    if (value.isEmpty())
      return null;
    String[] dateParts = value.split("\\.");
    if (dateParts.length != 3)
      return null;
    int day = i(dateParts[0]);
    int month = i(dateParts[1]);
    int year = i(dateParts[2]);
    if (day < 1) {
      throw new IllegalArgumentException("Dan ne može biti manji od 1.");
    } else if (month < 1) {
      throw new IllegalArgumentException("Mjesec ne može biti manji od 1.");
    } else if (year < 1) {
      throw new IllegalArgumentException("Godina ne može biti manja od 1.");
    } else if (month > 12) {
      throw new IllegalArgumentException("Mjesec ne može biti veći od 12.");
    } else if (day > MONTH_DAYS.values()[month - 1].getDays() && !(month == 2 && day == 29 && year % 4 == 0)) {
      throw new IllegalArgumentException("Dan ne može biti veći od broja dana u mjesecu.");
    }
    
    var date = Calendar.getInstance();
    date.set(year, month - 1, day);
    return date.getTime();
  }

}
