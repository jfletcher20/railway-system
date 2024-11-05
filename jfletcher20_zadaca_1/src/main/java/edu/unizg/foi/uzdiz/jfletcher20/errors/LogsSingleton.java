package edu.unizg.foi.uzdiz.jfletcher20.errors;

public class LogsSingleton {
  
  static private volatile LogsSingleton instance = new LogsSingleton();
  private int errorCount = 0;

  private LogsSingleton() {}
  
  public static LogsSingleton getInstance() {
    return instance;
  }
  
  public void logError(String message) {
    System.out.println(" [!] <" + errorCount + "> Error: " + message);
    errorCount++;
  }
  
  public void logError(int row, String message) {
    System.out.println(" [!] <" + errorCount + ":r" + row + "> Error: " + message);
    errorCount++;
  }
  
  public void logWarning(String message) {
    System.out.println(" [-] Warning: " + message);
  }
  
  public void logWarning(int row, String message) {
    System.out.println(" [-] Warning: Redak " + row + ": " + message);
  }
  
  public void logInfo(String message) {
    System.out.println(" [?] Info: " + message);
  }
  
  public void logInfo(int row, String message) {
    System.out.println(" [?] Info: Redak " + row + ": " + message);
  }

}
