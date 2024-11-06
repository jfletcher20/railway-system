package edu.unizg.foi.uzdiz.jfletcher20.errors;

public class LogsSingleton {

  static private volatile LogsSingleton instance = new LogsSingleton();
  private int errorCount = 0;
  public boolean logWarnings = false, logInfo = false;

  private LogsSingleton() {}

  public static LogsSingleton getInstance() {
    return instance;
  }

  public void logError(String message) {
    System.out.println(" [!] <e" + errorCount + "> Error: " + message);
    errorCount++;
  }

  public void logError(int row, String message) {
    System.out.println(" [!] <e" + errorCount + ":r" + row + "> Error: " + message);
    errorCount++;
  }

  public void logWarning(String message) {
    if (logWarnings)
      System.out.println("  [-] Warning: " + message);
  }

  public void logWarning(int row, String message) {
    if (logWarnings)
      System.out.println("  [-] <w" + row + "> Warning: " + message);
  }

  public void logInfo(String message) {
    if (logInfo)
      System.out.println("  [?] Info: " + message);
  }

  public void logInfo(int row, String message) {
    if (logInfo)
      System.out.println("  [?] <r" + row + "> Info: " + message);
  }

}
