package edu.unizg.foi.uzdiz.jfletcher20.system;

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
  
  private static class LogsSingleton {

    static private volatile LogsSingleton instance = new Logs.LogsSingleton();
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

    public boolean toggleInfo() {
      logInfo = !logInfo;
      return logInfo;
    }
    
    public boolean toggleWarnings() {
      logWarnings = !logWarnings;
      return logWarnings;
    }

  }
  
}
