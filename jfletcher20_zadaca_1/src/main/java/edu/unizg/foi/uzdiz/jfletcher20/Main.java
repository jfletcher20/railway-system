package edu.unizg.foi.uzdiz.jfletcher20;

import edu.unizg.foi.uzdiz.jfletcher20.system.GlobalClock;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingletonBuilder;
import edu.unizg.foi.uzdiz.jfletcher20.system.SystemInitializationDirector;

/**
 * Main class
 */
public class Main {
  public static boolean debugMode = true;
  public static void main(String[] args) {
    GlobalClock.initStates();
    RailwaySingleton.getInstance().setInitArgs(args);
    var initDirector = new SystemInitializationDirector(new RailwaySingletonBuilder());
    if (initDirector.construct() == null) {
      System.exit(1);
    }

    // za laku izmjenu drugim komandnim sustavima, koristi se refleksija
    Class<?> type = RailwaySingleton.PREFERRED_COMMAND_SYSTEM;
    try {
      type.getMethod("startCommandSystem").invoke(Class.forName(type.getName()).getMethod("getInstance").invoke(null));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
