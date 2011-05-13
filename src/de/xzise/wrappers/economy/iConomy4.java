package de.xzise.wrappers.economy;

import org.bukkit.plugin.Plugin;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.nijiko.coelho.iConomy.system.Bank;

public class iConomy4 implements EconomyWrapper {

    private final Bank bank;
    private final Plugin plugin;
    
    public final class Account4 implements AccountWrapper {

        private final Account account;
        
        public Account4(Account account) {
            this.account = account;
        }
        
        @Override
        public boolean hasEnough(int price) {
            return this.account.hasEnough(price);
        }

        @Override
        public void add(int price) {
            this.account.add(price);
        }
        
    }
    
    public iConomy4(Plugin plugin) {
        this.bank = iConomy.getBank();
        this.plugin = plugin;
    }
    
    @Override
    public AccountWrapper getAccount(String name) {
        if (!this.bank.hasAccount(name)) {
            this.bank.addAccount(name);
        }
        return new Account4(this.bank.getAccount(name));
    }

    @Override
    public String format(int price) {
        return this.bank.format(price);
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

}
