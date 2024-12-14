package edu.unizg.foi.uzdiz.jfletcher20.system;

public class SystemInitializationDirector {
  private RailwaySingletonBuilder builder;

  public SystemInitializationDirector(RailwaySingletonBuilder builder) {
    this.builder = builder;
  }

  public RailwaySingleton construct() {
      this.builder.loadFiles();
      this.builder.verifyTrainTracks();
      this.builder.verifyCompositions();
    return this.builder.getResult();
  }

}
