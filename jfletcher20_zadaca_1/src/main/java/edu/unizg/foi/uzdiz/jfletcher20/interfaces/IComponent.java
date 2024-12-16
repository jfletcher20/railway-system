package edu.unizg.foi.uzdiz.jfletcher20.interfaces;

/**
 * Component interface for the Composite design pattern
 */
public interface IComponent {
    void Operation();
    int Add(IComponent component);
    int Remove(IComponent component);
    IComponent GetChild(int index);
}
