package edu.unizg.foi.uzdiz.jfletcher20.system.subsystems.railway;

import edu.unizg.foi.uzdiz.jfletcher20.interfaces.IBuilder;
import edu.unizg.foi.uzdiz.jfletcher20.system.Logs;
import edu.unizg.foi.uzdiz.jfletcher20.utils.FilesUtil;

public class RailwaySingletonBuilder implements IBuilder {

  public RailwaySingletonBuilder() {
  }

  public RailwaySingletonBuilder loadFiles() {
    if (RailwaySingleton.getInstance().getInitArgs() != null) {
      if (!FilesUtil.loadFiles(RailwaySingleton.getInstance().getInitArgs()))
        return null;
      // RailwaySingleton.getInstance().printStats();
    } else {
      Logs.e("RailwaySingletonBuilder buildPart: "
          + "RailwaySingleton instance does not have initArgs set!");
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
  
  public RailwaySingletonBuilder verifyTrains() {
    RailwaySingleton.getInstance().verifyTrains();
    return this;
  }

  @Override
  public RailwaySingleton getResult() {
    // RailwaySingleton.getInstance().printStats();
    return RailwaySingleton.getInstance();
  }


}
