package de.xzise.wrappers.economy;

/**
 * A wrapper for accounts.
 * @author Fabian Neundorf
 */
public interface AccountWrapper {

    boolean hasEnough(double price);
    void add(double price);
    
}
