package de.xzise.wrappers.permissions;

public final class BufferPermission implements Permission<Boolean> {

    private final String name;
    private final boolean def;

    public BufferPermission(String name, boolean def) {
        this.name = name;
        this.def = def;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean getDefault() {
        return this.def;
    }

}