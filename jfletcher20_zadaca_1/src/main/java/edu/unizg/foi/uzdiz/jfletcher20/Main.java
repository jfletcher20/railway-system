package edu.unizg.foi.uzdiz.jfletcher20;

import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingleton;
import edu.unizg.foi.uzdiz.jfletcher20.system.RailwaySingletonBuilder;
import edu.unizg.foi.uzdiz.jfletcher20.system.SystemInitializationDirector;

/**
 * Main class
 */
public class Main {
  public static void main(String[] args) {
    putArgsInSingleton(args);
    var initDirector = new SystemInitializationDirector(new RailwaySingletonBuilder());
    initDirector.construct();
  }

  private static void putArgsInSingleton(String[] args) {
    RailwaySingleton.getInstance().setInitArgs(args);
  }
}
