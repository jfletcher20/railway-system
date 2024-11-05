package hr.foi.jfletcher20.utils;

public class SystemInitializationDirector {
  private IBuilder builder;
  public SystemInitializationDirector(IBuilder builder) {
    this.builder = builder;
  }
  
  public void construct() {
      this.builder.buildPart();
  }

}
