package de.xzise.xwarp.timer;

import java.util.HashMap;
import java.util.Map;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.wrappers.permission.Groups;

public class CoolDown {

    private final Map<CommandSender, CoolTask> players = new HashMap<CommandSender, CoolTask>();
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
                this.plugin.getServer().getScheduler().cancelTask(this.players.get(sender).id);
            }
    
            CoolTask task = new CoolTask(sender, this);
            int taskIndex = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, task, time * 20);
            task.setId(taskIndex);
            this.players.put(sender, task);
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
        return MyWarp.permissions.getInteger(sender, Groups.TIMERS_COOLDOWN_GROUP.get(visibility));
    }
    
    public void cooledDown(CommandSender warpable) {
        if (this.properties.isCooldownNotify()) {
            warpable.sendMessage(ChatColor.AQUA + "You have cooled down, feel free to warp.");
        }
        this.players.remove(warpable);
    }

    private static class CoolTask implements Runnable {

        private final CommandSender player;
        private final CoolDown cooldown;
        private int id;

        public CoolTask(CommandSender player, CoolDown cooldown) {
            this.player = player;
            this.cooldown = cooldown;
        }
        
        public void setId(int id) {
            this.id = id;
        }

        public void run() {
            this.cooldown.cooledDown(this.player);
        }
    }
    
}
