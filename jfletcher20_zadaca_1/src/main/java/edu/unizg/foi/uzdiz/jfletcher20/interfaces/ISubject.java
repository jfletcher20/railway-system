package edu.unizg.foi.uzdiz.jfletcher20.interfaces;

public interface ISubject {
    void registerObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers(String stationName);
}
