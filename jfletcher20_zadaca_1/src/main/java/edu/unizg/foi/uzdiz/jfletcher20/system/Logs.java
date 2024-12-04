package edu.unizg.foi.uzdiz.jfletcher20.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap;

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

  public static void o(String message) {
    LogsSingleton.getInstance().logConsoleMessage(message);
  }

  public static void o(String message, boolean usePrefixSymbol) {
    LogsSingleton.getInstance().logConsoleMessage(message, usePrefixSymbol);
  }

  public static void o(int row, String message) {
    LogsSingleton.getInstance().logConsoleMessage(row, message);
  }

  public static void c(String message) {
    LogsSingleton.getInstance().logCommandMessage(message);
  }

  public static void c(int row, String message) {
    LogsSingleton.getInstance().logCommandMessage(row, message);
  }

  public static void header(String header) {
    LogsSingleton.getInstance().logHeader(header);
  }

  public static void tableHeader(List<String> header) {
    LogsSingleton.getInstance().addHeader(header);
  }

  public static void tableRow(List<String> row) {
    LogsSingleton.getInstance().addRow(row);
  }

  public static void printTable() {
    LogsSingleton.getInstance().printTable();
  }

  public static List<String> flushTable() {
    return LogsSingleton.getInstance().flushTable();
  }

  public static void setMaxColumnLength(int length) {
    LogsSingleton.getInstance().setMaxColumnLength(length);
  }

  /**
   * Adds padding to the logs before and after the function call.
   * 
   * @param function
   */
  public static void withPadding(Runnable function) {
    LogsSingleton.getInstance().logPadding();
    function.run();
    LogsSingleton.getInstance().logPadding();
  }

  /**
   * Adds padding to the logs before and after the function call.
   * 
   * @param function
   */
  public static void withPadding(Runnable function, boolean top, boolean bottom) {
    if (top)
      LogsSingleton.getInstance().logPadding();
    function.run();
    if (bottom)
      LogsSingleton.getInstance().logPadding();
  }

  public static void header(String header, boolean addPadding) {
    if (addPadding)
      withPadding(() -> LogsSingleton.getInstance().logHeader(header));
    else
      LogsSingleton.getInstance().logHeader(header);
  }

  public static void footer() {
    LogsSingleton.getInstance().logFooter();
  }

  public static void footer(boolean withPadding) {
    if (withPadding)
      withPadding(() -> LogsSingleton.getInstance().logFooter());
    else
      LogsSingleton.getInstance().logFooter();
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
    private Map<Integer, Integer> rowErrorCounts = new HashMap<>();
    public List<AbstractMap.SimpleEntry<String, String>> errorList = new ArrayList<>();

    public List<List<String>> tableOutput = new ArrayList<>();
    private int maxColumnLength = 22;

    private LogsSingleton() {
    }

    public static LogsSingleton getInstance() {
      return instance;
    }

    public void logError(String message) {
      String key = "e" + errorCount;
      errorList.add(new AbstractMap.SimpleEntry<>(key, message));
      System.out.println("   [!] " + key + " Error: " + message);
      errorCount++;
    }

    public void logError(int row, String message) {
      ++row;
      int instanceIndex = rowErrorCounts.getOrDefault(row, 0) + 1;
      rowErrorCounts.put(row, instanceIndex);

      if (instanceIndex == 1)
        errorCount++;

      String errKey = "e" + errorCount;
      if (instanceIndex > 1)
        errKey = "/\\" + " ".repeat(errKey.length() - 2);
      String key = errKey + ":r" + row + (instanceIndex > 1 ? ":" + instanceIndex : "");
      errorList.add(new AbstractMap.SimpleEntry<>(key, message));
      System.out.println("   [!] " + key + " Error: " + message);
    }

    public void addHeader(List<String> header) {
      tableOutput.addFirst(header);
    }

    public void addRow(List<String> row) {
      tableOutput.add(row);
    }

    public void setMaxColumnLength(int length) {
      maxColumnLength = length;
    }

    public List<String> flushTable() {
      List<String> rows = new ArrayList<>();
      if (tableOutput.isEmpty())
        return rows;
      int[] columnLengths = new int[tableOutput.get(0).size()];
      for (List<String> row : tableOutput)
        for (int i = 0; i < row.size(); i++) {
          String cell = row.get(i);
          if (cell.length() > maxColumnLength)
            cell = cell.substring(0, maxColumnLength - 3) + "...";
          columnLengths[i] = Math.max(columnLengths[i], cell.length());
        }
      for (int rowIndex = 0; rowIndex < tableOutput.size(); rowIndex++) {
        List<String> row = tableOutput.get(rowIndex);
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
          String cell = row.get(i);
          if (cell.length() > maxColumnLength)
            cell = cell.substring(0, maxColumnLength - 3) + "...";
          if (cell.matches("-?\\d+(\\.\\d+)?")) {
            line.append(String.format("%" + columnLengths[i] + "s", cell));
          } else {
            line.append(String.format("%-" + columnLengths[i] + "s", cell));
          }
          if (i < row.size() - 1)
            line.append("\t| ");
        }
        rows.add(line.toString());
        if (rowIndex == 0) {
          StringBuilder divider = new StringBuilder();
          for (int i = 0; i < columnLengths.length; i++) {
            divider.append("-".repeat(columnLengths[i]));
            if (i < columnLengths.length - 1)
              divider.append("\t| ");
          }
          rows.add(divider.toString());
        }
      }

      tableOutput.clear();
      return rows;
    }

    public void printTable() {
      List<String> rows = flushTable();
      for (String row : rows)
        Logs.o(" " + row, false);
    }

    public void logWarning(String message) {
      if (logWarnings)
        System.out.println(" [-] Warning: " + message);
    }

    public void logWarning(int row, String message) {
      if (logWarnings)
        System.out.println(" [-] <w" + row + "> Warning: " + message);
    }

    public void logInfo(String message) {
      if (logInfo)
        System.out.println(" [?] Info: " + message);
    }

    public void logInfo(int row, String message) {
      if (logInfo)
        System.out.println(" [?] <r" + row + "> Info: " + message);
    }

    public void logConsoleMessage(String message) {
      System.out.println("  [>] " + message);
    }

    public void logConsoleMessage(String message, boolean usePrefixSymbol) {
      String prefix = usePrefixSymbol ? "[>]" : "   ";
      System.out.println("  " + prefix + " " + message);
    }

    public void logConsoleMessage(int row, String message) {
      System.out.println("  [>] <r" + row + "> " + message);
    }

    public void logCommandMessage(String message) {
      System.out.println("    [<] " + message);
    }

    public void logCommandMessage(int row, String message) {
      System.out.println("    [<] <r" + row + "> " + message);
    }

    String prefix = " >-", suffix = "-< ";
    String decorationCharacter = "-";
    String headerWrapperPrefix = " [ ", headerWrapperSuffix = " ] ";
    int decoLength = 32, minDecoLength = 6;
    int latestHeaderLength = 0, latestDecoLength = 0;

    public void logHeader(String header) {
      int totalHeaderLength = header.length() + headerWrapperPrefix.length() + headerWrapperSuffix.length();
      int decoRightLength = Math.max(decoLength - totalHeaderLength, minDecoLength);
      this.latestHeaderLength = totalHeaderLength;
      this.latestDecoLength = decoRightLength;
      header = headerWrapperPrefix + header + headerWrapperSuffix;
      String decoration = header + decorationCharacter.repeat(decoRightLength);
      decoration.trim();
      prefix.trim();
      suffix.trim();
      System.out.println(prefix + decoration + suffix);
    }

    public void logFooter() {
      String decoration = decorationCharacter.repeat(this.latestDecoLength + this.latestHeaderLength);
      System.out.println(prefix + decoration + suffix);
      decoration.trim();
    }

    public void logPadding() {
      System.out.println("");
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
