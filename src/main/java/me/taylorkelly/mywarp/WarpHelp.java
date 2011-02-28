package me.taylorkelly.mywarp;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.taylorkelly.help.Help;
import org.bukkit.plugin.Plugin;

class WarpHelp {

    public static void initialize(Plugin plugin) {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            Logger log = Logger.getLogger("Minecraft");
            Help helpPlugin = ((Help) test);
            helpPlugin.registerCommand("warp help", "Help for all /warp commands", plugin, true);
            helpPlugin.registerCommand("warp [name]", "Warp to [name]", plugin, "mywarp.warp.basic.warp");
            helpPlugin.registerCommand("warp [player] [name]", "Warp [player] to [name]", plugin, "mywarp.admin");
            helpPlugin.registerCommand("warp create [name]", "Create a new warp called [name]", plugin, "mywarp.warp.basic.createpublic", "mywarp.warp.basic.createprivate");
            helpPlugin.registerCommand("warp pcreate [name]", "Create a new private warp called [name]", plugin, "mywarp.warp.basic.createprivate");
            helpPlugin.registerCommand("warp delete [name]", "Deletes the warp [name]", plugin, "mywarp.warp.basic.delete");
            helpPlugin.registerCommand("warp welcome [name]", "Change the welcome message for [name]", plugin, "mywarp.warp.basic.welcome");
            helpPlugin.registerCommand("warp point [name]", "Point your compass to [name]", plugin, "mywarp.warp.basic.compass");
            helpPlugin.registerCommand("warp list (#)", "List the warps you can visit", plugin, "mywarp.warp.basic.list");
            helpPlugin.registerCommand("warp search [query]", "Searches for warps related to [query]", plugin, "mywarp.warp.basic.search");
            helpPlugin.registerCommand("warp give [player] [name]", "Give your warp [name] to [player]", plugin, "mywarp.warp.soc.give");
            helpPlugin.registerCommand("warp invite [player] [name]", "Invite [player] to [name]", plugin, "mywarp.warp.soc.invite");
            helpPlugin.registerCommand("warp uninvite [player] [name]", "Uninvite [player] from [name]", plugin, "mywarp.warp.soc.uninvite");
            helpPlugin.registerCommand("warp public [name]", "Make [name] a public warp", plugin, "mywarp.warp.soc.public");
            helpPlugin.registerCommand("warp private [name]", "Make [name] a private warp", plugin, "mywarp.warp.soc.private");
            helpPlugin.registerCommand("warp convert", "Converts your warps from warps.txt", plugin, "mywarp.admin");

            log.log(Level.INFO, "[MYWARP] 'Help' support enabled.");
        } else {
            Logger log = Logger.getLogger("Minecraft");
            log.log(Level.WARNING, "[MYWARP] 'Help' isn't detected. No /help support.");
        }
    }
}
