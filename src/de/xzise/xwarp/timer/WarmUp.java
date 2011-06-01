package de.xzise.xwarp.timer;

import java.util.HashMap;
import java.util.Map;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.wrappers.permission.Groups;

public class WarmUp {

    private Map<CommandSender, Integer> players = new HashMap<CommandSender, Integer>();
    private final Plugin plugin;
    private final PluginProperties properties;
    private final CoolDown down;

    public WarmUp(Plugin plugin, PluginProperties properties, CoolDown down) {
        this.plugin = plugin;
        this.properties = properties;
        this.down = down;
    }

    public void addPlayer(CommandSender warper, Warpable warped, Warp warp) {
        int warmup = this.warmupTime(warp.visibility, warper);
        if (warmup > 0) {
            if (this.properties.isWarmupNotify()) {
                warper.sendMessage(ChatColor.AQUA + "You will have to warm up for " + warmup + " secs");
            }
            int taskIndex = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new WarmTask(warper, warped, warp, this), warmup * 20);
            this.players.put(warper, taskIndex);
        } else {
            this.sendPlayer(warper, warped, warp);
        }
    }
    
    public boolean cancelWarmUp(CommandSender warper) {
        //TODO: Only remove, if warp itself?
        if (this.players.containsKey(warper)) {
            this.plugin.getServer().getScheduler().cancelTask(this.players.get(warper));
            this.players.remove(warper);
            return true;
        } else {
            return false;
        }
    }

    public int warmupTime(Visibility visibility, CommandSender warper) {
        return MyWarp.permissions.getInteger(warper, Groups.TIMERS_WARMUP_GROUP.get(visibility));
    }

    // public static boolean warmupNeeded(Warp warp, Player player) {
    // if (!WarpSettings.adminsObeyWarmsCools && player.isOp())
    // return false;
    //
    // if (warp.visibility == Visibility.GLOBAL && WarpSettings.WarmUpForGlobal)
    // return true;
    // if (warp.visibility == Visibility.PRIVATE &&
    // WarpSettings.WarmUpForPrivate)
    // return true;
    // if (warp.visibility == Visibility.PUBLIC && WarpSettings.WarmUpForPublic)
    // return true;
    // return false;
    // }

    public boolean playerHasWarmed(CommandSender warper) {
        return this.players.containsKey(warper);
    }

    private void sendPlayer(CommandSender warper, Warpable warped, Warp warp) {
        if (warped.teleport(warp.getLocation().toLocation())) {
            warped.sendMessage(ChatColor.AQUA + warp.welcomeMessage);
            this.down.addPlayer(warp.visibility, warper);
            this.players.remove(warper);
            if (!warped.equals(warper)) {
                warper.sendMessage("Sucessfully warped '" + ChatColor.GREEN + MinecraftUtil.getName(warped) + ChatColor.WHITE + "'");
            }
        } else {
            warper.sendMessage(ChatColor.RED + "Unable to warp.");
        }
    }

    private static class WarmTask implements Runnable {
        private CommandSender player;
        private Warpable warped;
        private Warp warp;
        private WarmUp warmUp;

        public WarmTask(CommandSender player, Warpable warped, Warp warp, WarmUp warmUp) {
            this.player = player;
            this.warped = warped;
            this.warp = warp;
            this.warmUp = warmUp;
        }

        public void run() {
            this.warmUp.sendPlayer(player, warped, warp);
        }
    }

}
