package de.xzise.xwarp.wrappers.economy;

public interface EconomyWrapper {

    AccountWrapper getAccount(String name);
    String format(int price);
    
}
