package de.xzise.xwarp;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.nijiko.coelho.iConomy.system.Bank;

import de.xzise.MinecraftUtil;

public class EconomyWrapper {

    /**
     * Wrap the Account class of iConomy. If no account is set, it will add nothing.
     * @author Fabian Neundorf
     */
    private class AccountWrapper {
        private final Account account;
        
        public AccountWrapper(Account account) {
            this.account = account;
        }
        
        public AccountWrapper() {
            this(null);
        }
        
        public void add(int amount) {
            if (this.account != null) {
                this.account.add(amount);
            }
        }
    }
    
    public enum PayResult {
        /** The price was paid. */
        PAID,
        /** The price couldn't paid, but not because the player hasn't enough. */
        UNABLE,
        /** The price couldn't paid, because the player hasn't enough. */
        NOT_ENOUGH;
    }
    
    private Bank bank;
    //TODO: Add option
    private AccountWrapper tax = new AccountWrapper();

    /**
     * Pays for an action if the sender has enough money. If the sender is not a player no money will be transfered.
     * @param sender The paying sender.
     * @param reciever Optional reciever of the price. If null only the basic price is to pay.
     * @param price The amount of money which the reciever get.
     * @param basic The basic price like an tax.
     * @return If the price could be paid or if there was nothing to pay.
     */
    public PayResult pay(CommandSender sender, String reciever, int price, int basic) {
        if (this.bank != null) {
           Player player = MinecraftUtil.getPlayer(sender);
           if (player != null) {
               Account executor = this.getAccount(player.getName());
               if (price + basic == 0) {
                   return PayResult.PAID;
               } else
               // Not negative
               //TODO: Add option if allow
//               if (executor.getBalance() >= price + basic) {
               if (executor.hasEnough(price + basic)) {    
                   executor.add(-price -basic);
                   this.tax.add(basic);
                   if (MinecraftUtil.isSet(reciever)) {
                       Account owner = this.getAccount(reciever);
                       owner.add(price);
                   }
                   return PayResult.PAID;
               } else {
                   return PayResult.NOT_ENOUGH;
               }
           } else {
               MyWarp.logger.info("Couldn't pay action, because the executor is not a player.");
           }
        } else if (price > 0) {
            sender.sendMessage(ChatColor.RED + "You should pay for this warp. But no iConomy found.");
        }
        return PayResult.UNABLE;
    }
    
    private final Account getAccount(String name) {
        if (!this.bank.hasAccount(name)) {
            this.bank.addAccount(name);
        }
        return this.bank.getAccount(name);
    }
    
    public PayResult pay(CommandSender sender, int basic) {
        return this.pay(sender, null, 0, basic);
    }
    
    public boolean isActive() {
        return this.bank != null;
    }
    
    public String format(int price) {
        if (this.bank != null) {
            return this.bank.format(price);
        } else {
            return "";
        }
    }

    public void init(Plugin plugin) {
        this.bank = null;
        if (plugin != null && plugin instanceof iConomy) {
            if (plugin.isEnabled()) {
                this.bank = iConomy.getBank();
                MyWarp.logger.info("iConomy enabled.");
            } else {
                MyWarp.logger.info("Economy system found, but not enabled. Use defaults.");
            }
        } else {
            MyWarp.logger.warning("Economy system not found. Use defaults.");
        }
    }
}
