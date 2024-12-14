package edu.unizg.foi.uzdiz.jfletcher20.system;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IBuilder;
import edu.unizg.foi.uzdiz.jfletcher20.utils.FilesUtil;

public class RailwaySingletonBuilder implements IBuilder {

  public RailwaySingletonBuilder() {
  }

  public RailwaySingletonBuilder loadFiles() {
    if (RailwaySingleton.getInstance().getInitArgs() != null) {
      var initArgs = RailwaySingleton.getInstance().getInitArgs();
      if (!FilesUtil.loadFiles(initArgs)) {
        return null;
      }
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

  public RailwaySingletonBuilder verifyTrainTracks() {
    RailwaySingleton.getInstance().verifyTrainTracks();
    return this;
  }

  @Override
  public RailwaySingleton getResult() {
    RailwaySingleton.getInstance().printStats();
    return RailwaySingleton.getInstance();
  }

}
