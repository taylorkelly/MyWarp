package de.xzise.xwarp.dataconnections;

import de.xzise.xwarp.WarpObject;

public final class NameIdentification<T extends WarpObject> implements IdentificationInterface<T> {

    private final String name;
    private final String owner;

    public NameIdentification(T warpObject) {
        this(warpObject.getName(), warpObject.getOwner());
    }
    
    public static <T extends WarpObject> NameIdentification<T> create(T warpObject) {
        return new NameIdentification<T>(warpObject);
    }

    public NameIdentification(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public boolean isIdentificated(T warpObject) {
        return warpObject.getName().equalsIgnoreCase(this.name) && warpObject.getOwner().equals(this.owner);
    }

}