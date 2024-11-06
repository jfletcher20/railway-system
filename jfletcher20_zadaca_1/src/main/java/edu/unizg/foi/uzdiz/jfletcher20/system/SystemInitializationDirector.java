package edu.unizg.foi.uzdiz.jfletcher20.system;

public class SystemInitializationDirector {
  private RailwaySingletonBuilder builder;

  public SystemInitializationDirector(RailwaySingletonBuilder builder) {
    this.builder = builder;
  }

  public Object construct() {
    this.builder.loadFiles().initCommandSystem().runCommandSystem();
    return this.builder.getResult();
  }

}
