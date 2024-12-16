package edu.unizg.foi.uzdiz.jfletcher20.interfaces;

/**
 * Leaf interface for the Composite design pattern
 */
public abstract class Leaf implements IComponent {
    @Override
    public abstract void Operation();
    @Override
    public final int Add(IComponent component) {
        return -1;
    }
    @Override
    public final int Remove(IComponent component) {
        return -1;
    }
    @Override
    public final IComponent GetChild(int index) {
        return null;
    }
}
