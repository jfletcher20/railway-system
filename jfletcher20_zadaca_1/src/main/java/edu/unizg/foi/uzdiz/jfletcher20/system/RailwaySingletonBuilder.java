package edu.unizg.foi.uzdiz.jfletcher20.system;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IBuilder;
import edu.unizg.foi.uzdiz.jfletcher20.utils.FilesUtil;

/// The product of this builder is the Railway singleton
public class RailwaySingletonBuilder implements IBuilder {

  public RailwaySingletonBuilder() {}

  public RailwaySingletonBuilder loadFiles() {
    if (RailwaySingleton.getInstance().getInitArgs() != null) {
      var initArgs = RailwaySingleton.getInstance().getInitArgs();
      FilesUtil.loadFiles(initArgs);
      RailwaySingleton.getInstance().printStats();
    } else {
      Logs.e(
          "RailwaySingletonBuilder buildPart: RailwaySingleton instance does not have initArgs set!");
    }
    return this;
  }

  public RailwaySingletonBuilder verifyCompositions() {
    RailwaySingleton.getInstance().verifyCompositions();
    return this;
  }

  public RailwaySingletonBuilder initCommandSystem() {
    CommandSystem commandSystem = CommandSystem.getInstance();
    RailwaySingleton.getInstance().setCommandSystem(commandSystem);
    return this;
  }

  public RailwaySingletonBuilder runCommandSystem() {
    RailwaySingleton.getInstance().getCommandSystem().startCommandSystem();
    return this;
  }

  @Override
  public Object getResult() {
    RailwaySingleton.getInstance().printStats();
    return RailwaySingleton.getInstance();
  }

}
