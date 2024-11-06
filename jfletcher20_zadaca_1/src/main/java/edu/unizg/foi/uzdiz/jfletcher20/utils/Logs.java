package edu.unizg.foi.uzdiz.jfletcher20.utils;

public abstract class Logs {
  
  public static void e(String message) {
    LogsSingleton.getInstance().logError(message);
  }
  
  public static void e(int row, String message) {
    LogsSingleton.getInstance().logError(row, message);
  }
  
  public static void w(String message) {
    LogsSingleton.getInstance().logWarning(message);
  }
  
  public static void w(int row, String message) {
    LogsSingleton.getInstance().logWarning(row, message);
  }
  
  public static void i(String message) {
    LogsSingleton.getInstance().logInfo(message);
  }
  
  public static void i(int row, String message) {
    LogsSingleton.getInstance().logInfo(row, message);
  }

  public static boolean toggleInfo() {
    return LogsSingleton.getInstance().toggleInfo();
  }
  
  public static boolean toggleWarnings() {
    return LogsSingleton.getInstance().toggleWarnings();
  }
  
}
