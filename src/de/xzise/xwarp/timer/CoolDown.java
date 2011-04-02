package de.xzise.xwarp.timer;

import java.util.HashMap;
import java.util.Map;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.PermissionWrapper.PermissionValues;
import de.xzise.xwarp.warpable.Warpable;

public class CoolDown {

    private final Map<CommandSender, Integer> players = new HashMap<CommandSender, Integer>();
    private final Plugin plugin;
    private final PluginProperties properties;
    
    public CoolDown(Plugin plugin, PluginProperties properties) {
        this.plugin = plugin;
        this.properties = properties;
    }
    
    public void addPlayer(Visibility visibility, CommandSender sender) {
        int time = this.cooldownTime(visibility, sender);
        if (time > 0) {
            if (this.players.containsKey(sender)) {
                this.plugin.getServer().getScheduler().cancelTask(this.players.get(sender));
            }
    
            int taskIndex = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new CoolTask(sender, this), time * 20);
            this.players.put(sender, taskIndex);
        }
    }

    public boolean playerHasCooled(CommandSender sender) {
        return !this.players.containsKey(sender);
    }

    /**
     * NOT WORKING AT THE MOMENT!
     * @return Every time: 0!
     */
    public int timeLeft(Warpable player) {
        if (players.containsKey(player)) {
            // TODO
            return 0;
        } else {
            return 0;
        }
    }
    
    public int cooldownTime(Visibility visibility, CommandSender sender) {
        PermissionValues value = PermissionValues.WARP_COOLDOWN_PRIVATE;
        switch (visibility) {
        case GLOBAL:
            value = PermissionValues.WARP_COOLDOWN_GLOBAL;
            break;
        case PUBLIC:
            value = PermissionValues.WARP_COOLDOWN_PUBLIC;
            break;
        }
        return MyWarp.permissions.getInteger(sender, value, 0);
    }
    
    public void cooledDown(CommandSender warpable) {
        if (this.properties.isCooldownNotify()) {
            warpable.sendMessage(ChatColor.AQUA + "You have cooled down, feel free to warp.");
        }
        this.players.remove(warpable);
    }
    
//    public boolean cooldownNeeded(Warp warp, CommandSender warper) {
//        if (!WarpSettings.adminsObeyWarmsCools && warper.isOp())
//                return false;
//        
//        if (warp.visibility == Visibility.PUBLIC && WarpSettings.CoolDownForPublic)
//                return true;
//            if (warp.visibility == Visibility.PRIVATE && WarpSettings.CoolDownForPrivate)
//                return true;
//                if (warp.visibility == Visibility.GLOBAL && WarpSettings.CoolDownForGlobal)
//                    return true;
//                else
//                    return false;
//        
//    }

    private static class CoolTask implements Runnable {

        private final CommandSender player;
        private final CoolDown cooldown;

        public CoolTask(CommandSender player, CoolDown cooldown) {
            this.player = player;
            this.cooldown = cooldown;
        }

        public void run() {
            this.cooldown.cooledDown(this.player);
        }
    }
    
}
