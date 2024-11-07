package edu.unizg.foi.uzdiz.jfletcher20.system;

public class SystemInitializationDirector {
  private RailwaySingletonBuilder builder;

  public SystemInitializationDirector(RailwaySingletonBuilder builder) {
    this.builder = builder;
  }

  public Object construct() {
    try {
      this.builder.loadFiles().verifyCompositions().initCommandSystem().runCommandSystem();
    } catch (Exception e) {
    }
    return this.builder.getResult();
  }

}
