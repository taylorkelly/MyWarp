package de.xzise.xwarp.commands.xwarp;

import java.util.Collection;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.Manager;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.wrappers.permission.GeneralPermissions;

public class ReloadCommand extends CommonHelpableSubCommand {

    private final Collection<Manager<?>> managers;
    private final PluginProperties properties;
    private final EconomyHandler economy;

    public ReloadCommand(EconomyHandler economy, PluginProperties properties, Manager<?>... manager) {
        super("reload");
        this.managers = ImmutableList.copyOf(manager);
        this.properties = properties;
        this.economy = economy;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            if (XWarp.permissions.permission(sender, GeneralPermissions.RELOAD)) {
                DataConnection data = this.properties.getDataConnection();
                if (data != null) {
                    data.free();
                }
                this.properties.read();
                data = this.properties.getDataConnection();
                try {
                    if (!data.load(this.properties.getDataConnectionFile())) {
                        XWarp.logger.severe("Could not load data!");
                        sender.sendMessage(ChatColor.RED + "Reload failed!");
                    } else {
                        this.economy.reloadConfig(this.properties.getEconomyPlugin(), this.properties.getEconomyBaseAccount());
                        XWarp.permissions.setPluginName(this.properties.getPermissionsPlugin());
                        XWarp.permissions.load();
                        for (Manager<?> manager : this.managers) {
                            manager.reload(data);
                        }
                        sender.sendMessage(ChatColor.GREEN + "Reload successfully!");
                    }
                } catch (Exception e) {
                    XWarp.logger.severe("Exception while reloading!", e);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You have no permission to reload.");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Reloads xWarp's settings and warps." };
    }

    @Override
    public String getSmallHelpText() {
        return "Reloads xWarp.";
    }

    @Override
    public String getCommand() {
        return "warp reload";
    }

    @Override
    public boolean listHelp(CommandSender sender) {
        return XWarp.permissions.permission(sender, GeneralPermissions.RELOAD);
    }

}
