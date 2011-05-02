package de.xzise.metainterfaces;

public interface Moveable<T extends Moveable<?>> {

    T moveX(double delta);
    T moveY(double delta);
    T moveZ(double delta);
    
}
