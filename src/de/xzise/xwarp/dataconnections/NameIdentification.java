package de.xzise.xwarp.dataconnections;

import me.taylorkelly.mywarp.Warp;

public final class NameIdentification implements IdentificationInterface {

    private final String name;
    private final String owner;

    public NameIdentification(Warp warp) {
        this(warp.name, warp.getOwner());
    }

    public NameIdentification(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public boolean isIdentificated(Warp warp) {
        return warp.name.equalsIgnoreCase(this.name) && warp.getOwner().equals(this.owner);
    }

}