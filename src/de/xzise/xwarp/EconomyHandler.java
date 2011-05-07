package de.xzise.xwarp;

import java.util.HashMap;
import java.util.Map;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.wrappers.economy.AccountWrapper;
import de.xzise.xwarp.wrappers.economy.BOSEcon0;
import de.xzise.xwarp.wrappers.economy.EconomyWrapper;
import de.xzise.xwarp.wrappers.economy.EconomyWrapperFactory;
import de.xzise.xwarp.wrappers.economy.Essentials;
import de.xzise.xwarp.wrappers.economy.iConomyFactory;

public class EconomyHandler {
    
    public enum PayResult {
        /** The price was paid. */
        PAID,
        /** The price couldn't paid, but not because the player hasn't enough. */
        UNABLE,
        /** The price couldn't paid, because the player hasn't enough. */
        NOT_ENOUGH;
    }
    
    public static final Map<String, EconomyWrapperFactory> FACTORIES = new HashMap<String, EconomyWrapperFactory>();
    
    static {
        FACTORIES.put("BOSEconomy", new BOSEcon0.Factory());
        FACTORIES.put("iConomy", new iConomyFactory());
        FACTORIES.put("Essentials", new Essentials.Factory());
    }
    
    public static final AccountWrapper NULLARY_ACCOUNT = new AccountWrapper() {
        
        @Override
        public boolean hasEnough(int price) {
            return false;
        }
        
        @Override
        public void add(int price) {}
    };
    
    private EconomyWrapper economy;
    //TODO: Add option
    private AccountWrapper tax = NULLARY_ACCOUNT;
    private PluginProperties properties;

    public EconomyHandler(PluginProperties properties) {
        this.properties = properties;
    }

    /**
     * Pays for an action if the sender has enough money. If the sender is not a player no money will be transfered.
     * @param sender The paying sender.
     * @param reciever Optional reciever of the price. If null only the basic price is to pay.
     * @param price The amount of money which the reciever get.
     * @param basic The basic price like an tax.
     * @return If the price could be paid or if there was nothing to pay.
     */
    public PayResult pay(CommandSender sender, String reciever, int price, int basic) {
        if (this.economy != null) {
           Player player = MinecraftUtil.getPlayer(sender);
           if (player != null) {
               AccountWrapper executor = this.getAccount(player.getName());
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
                       AccountWrapper owner = this.getAccount(reciever);
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
    
    private final AccountWrapper getAccount(String name) {
        return this.economy.getAccount(name);
    }
    
    public PayResult pay(CommandSender sender, int basic) {
        return this.pay(sender, null, 0, basic);
    }
    
    public boolean isActive() {
        return this.economy != null;
    }
    
    public String format(int price) {
        if (this.economy != null) {
            return this.economy.format(price);
        } else {
            return "";
        }
    }
    
    public void init(PluginManager pluginManager) {
        for (String string : FACTORIES.keySet()) {
            this.init(pluginManager.getPlugin(string));
            if (this.economy != null) {
                return;
            }
        }
    }

    public void init(Plugin plugin) {
        if (plugin != null && this.economy == null) {
            PluginDescriptionFile pdf = plugin.getDescription();
            String economyPlugin = this.properties.getEconomyPlugin();
            if (!MinecraftUtil.isSet(economyPlugin) || (pdf.getName().equalsIgnoreCase(economyPlugin))) {
                EconomyWrapperFactory factory = FACTORIES.get(pdf.getName());
                if (factory != null) {
                    if (plugin.isEnabled()) {
                    try {
                        this.economy = factory.create(plugin);
                    } catch (Exception e) {
                        //TODO: Better exception handling
                        this.economy = null;
                    }
                    if (this.economy == null) {
                        MyWarp.logger.warning("Invalid economy system found: " + pdf.getFullName());
                    } else {
                        String baseAccount = this.properties.getEconomyBaseAccount();
                        if (MinecraftUtil.isSet(baseAccount)) {
                            this.tax = this.economy.getAccount(baseAccount);
                        } else {
                            this.tax = NULLARY_ACCOUNT;
                        }
                        MyWarp.logger.info("Linked with economy system: " + pdf.getFullName());
                    }
                    } else {
                        MyWarp.logger.warning("Doesn't link to disabled economy system: " + pdf.getFullName());
                    }
                }
            }
        }
    }
    
    public boolean unload(Plugin plugin) {
        if (this.economy != null && plugin == this.economy.getPlugin()) {
            this.economy = null;
            MyWarp.logger.info("Deactivated economy system.");
            return true;
        } else {
            return false;
        }
    }
}
