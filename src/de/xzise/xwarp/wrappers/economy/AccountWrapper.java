package de.xzise.xwarp.wrappers.economy;

/**
 * A wrapper for accounts.
 * @author Fabian Neundorf
 */
public interface AccountWrapper {

    boolean hasEnough(int price);
    void add(int price);
    
}
