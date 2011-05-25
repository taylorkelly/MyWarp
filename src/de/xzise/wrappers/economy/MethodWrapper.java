package de.xzise.wrappers.economy;

import org.bukkit.plugin.Plugin;

import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Method.MethodAccount;

public class MethodWrapper implements EconomyWrapper {

    private final Method method;
    private final Plugin plugin;
    
    public final class MethodAcc implements AccountWrapper {
        
        private final MethodAccount method;
        
        public MethodAcc(MethodAccount method) {
            this.method = method;
        }

        @Override
        public boolean hasEnough(double price) {
            return this.method.hasEnough(price);
        }

        @Override
        public void add(double price) {
            this.method.add(price);
        }
        
    }
    
    public MethodWrapper(Method method, Plugin plugin) {
        this.method = method;
        this.plugin = plugin;
    }
    
    @Override
    public AccountWrapper getAccount(String name) {
        return new MethodAcc(this.method.getAccount(name));
    }

    @Override
    public String format(double price) {
        return this.method.format(price);
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

}
