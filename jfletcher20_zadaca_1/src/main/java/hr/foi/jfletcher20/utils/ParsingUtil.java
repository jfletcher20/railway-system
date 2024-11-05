package hr.foi.jfletcher20.utils;

public class ParsingUtil {

  public static Double d(String value) {
    value = value.replace(",", ".");
    return value.isEmpty() ? null : Double.parseDouble(value);
  }
  
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

}
