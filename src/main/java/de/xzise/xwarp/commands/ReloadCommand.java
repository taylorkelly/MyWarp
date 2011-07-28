package de.xzise.xwarp.commands;

import java.util.Collection;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.Manager;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

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
            if (MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD)) {
                this.properties.read();
                this.economy.reloadConfig(this.properties.getEconomyPlugin(), this.properties.getEconomyBaseAccount());
                for (Manager<?> manager : this.managers) {
                    manager.reload();
                }
                sender.sendMessage("Reload successfully!");
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
        return MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD);
    }

}
