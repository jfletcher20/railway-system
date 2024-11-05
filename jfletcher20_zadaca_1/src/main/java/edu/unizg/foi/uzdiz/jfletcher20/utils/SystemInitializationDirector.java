package edu.unizg.foi.uzdiz.jfletcher20.utils;

public class SystemInitializationDirector {
  private IBuilder builder;
  public SystemInitializationDirector(IBuilder builder) {
    this.builder = builder;
  }
  
  public Object construct() {
      this.builder.buildPart();
      return this.builder.getResult();
  }

}
