package edu.unizg.foi.uzdiz.jfletcher20.system;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IBuilder;
import edu.unizg.foi.uzdiz.jfletcher20.utils.FilesUtil;

/// The product of this builder is the Railway singleton
public class RailwaySingletonBuilder implements IBuilder {

  public RailwaySingletonBuilder() {  }

  @Override
  public void buildPart() {
    if (RailwaySingleton.getInstance().getInitArgs() != null) {
      var initArgs = RailwaySingleton.getInstance().getInitArgs();
      FilesUtil.loadFiles(initArgs);
      CommandSystem commandSystem = CommandSystem.getInstance();
      RailwaySingleton.getInstance().setCommandSystem(commandSystem);
      RailwaySingleton.getInstance().printStats();
      RailwaySingleton.getInstance().getCommandSystem().startCommandSystem();
    } else {
      Logs.e("RailwaySingletonBuilder buildPart: RailwaySingleton instance does not have initArgs set!");
    }
  }

  @Override
  public Object getResult() {
    RailwaySingleton.getInstance().printStats();
    return RailwaySingleton.getInstance();
  }

}
