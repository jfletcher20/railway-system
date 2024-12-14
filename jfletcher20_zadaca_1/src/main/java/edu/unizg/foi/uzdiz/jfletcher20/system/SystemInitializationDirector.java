package edu.unizg.foi.uzdiz.jfletcher20.system;

public class SystemInitializationDirector {
  private RailwaySingletonBuilder builder;

  public SystemInitializationDirector(RailwaySingletonBuilder builder) {
    this.builder = builder;
  }

  public RailwaySingleton construct() {
    try {
      this.builder.loadFiles().verifyTrainTracks().verifyCompositions();
    } catch (Exception e) {
    }
    return this.builder.getResult();
  }

}
