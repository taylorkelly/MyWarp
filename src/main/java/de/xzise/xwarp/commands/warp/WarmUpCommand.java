package de.xzise.xwarp.commands.warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class WarmUpCommand extends WarpCommand {

    public WarmUpCommand(WarpManager manager, Server server) {
        super(manager, server, "time (s)", new String[] { "warmup", "chwu" });
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Changes the warp specific warmup. The time is in seconds.", "If the time is 'default', 'def' or negative the warp uses the group/user specific warmup." };
    }

    @Override
    public String getSmallHelpText() {
        return "Change the warp specific warmup";
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        Integer time;
        if (parameters[0].equals("default") || parameters[0].equals("def")) {
            time = -1;
        } else {
            time = MinecraftUtil.tryAndGetInteger(parameters[0]);
        }
        if (time != null) {
            this.manager.setWarmup(warp, sender, time);
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid warmup time given. Only integers, 'default' and 'def' allowed.");
        }
        return true;
    }

}
