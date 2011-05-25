package de.xzise.wrappers.economy;

import org.bukkit.plugin.Plugin;

import com.spikensbror.bukkit.mineconomy.bank.Bank;

import de.xzise.XLogger;

public class MineConomy implements EconomyWrapper {
    
    private final com.spikensbror.bukkit.mineconomy.MineConomy plugin;
    
    public final class MineConomyAccount implements AccountWrapper {

        private final Bank bank;
        private final String name;
        
        public MineConomyAccount(Bank bank, String name) {
            this.bank = bank;
            this.name = name;
        }
        
        @Override
        public boolean hasEnough(double price) {
            return this.bank.getTotal(this.name) >= price;
        }

        @Override
        public void add(double price) {
            this.bank.add(this.name, price);
        }
        
    }
    
    public MineConomy(com.spikensbror.bukkit.mineconomy.MineConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public AccountWrapper getAccount(String name) {
        return new MineConomyAccount(this.plugin.getBank(), name);
    }

    @Override
    public String format(double price) {
        return null;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    public static class Factory implements EconomyWrapperFactory {

        @Override
        public EconomyWrapper create(Plugin plugin, XLogger logger) {
            if (plugin instanceof com.spikensbror.bukkit.mineconomy.MineConomy) {
                return new MineConomy((com.spikensbror.bukkit.mineconomy.MineConomy) plugin);
            } else {
                return null;
            }
        }
        
    }
    
}
