package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.railway;

import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;

public class SystemInitializationDirector {
  private RailwaySingletonBuilder builder;

  public SystemInitializationDirector(RailwaySingletonBuilder builder) {
    this.builder = builder;
  }

  public RailwaySingleton construct() {
    if (this.builder.loadFiles() == null) {
      Logs.o("Obzirom da nisu ucitane datoteke, sustav se ne može koristiti. Zaustavi ću pokretanje sustava.");
      return null;
    }
    this.builder.verifyTrainTracks();
    this.builder.verifyCompositions();
    this.builder.verifyTrains();
    this.builder.constructTrainTrackSegments();

    return this.builder.getResult();
  }

}
