package de.xzise.xwarp.dataconnections;

public interface IdentificationInterface<T> {

    /**
     * Determines if the given object is identificated by this identification.
     * @param object The tested object.
     * @return If the object is meant by this identification.
     */
    boolean isIdentificated(T object);
    
}
