package de.xzise.wrappers.permissions;

public interface Permission<Def> {

    public String getName();
    
    public Def getDefault();
    
}
