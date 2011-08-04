package de.xzise.xwarp.commands.xwarp;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonSubCommand;
import de.xzise.wrappers.permissions.BufferPermission;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.dataconnections.DataConnection;

/* TEMPORARY */
public class CDWUConvCommand extends CommonSubCommand {

    private final WarpManager wm;

    public CDWUConvCommand(WarpManager wm) {
        super("cdwuconv");
        this.wm = wm;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (XWarp.permissions.permission(sender, new BufferPermission("xwarp.admin.cdwuconv", false))) {
            int cdcnt = 0;
            int wucnt = 0;
            DataConnection data = this.wm.save();
            for (Warp warp : this.wm.getWarpObjects()) {
                if (warp.getCoolDown() == 0) {
                    warp.setCoolDown(-1);
                    data.updateCoolDown(warp);
                    cdcnt++;
                }
                if (warp.getWarmUp() == 0) {
                    warp.setWarmUp(-1);
                    data.updateWarmUp(warp);
                    wucnt++;
                }
            }
            sender.sendMessage("Updated " + cdcnt + " cooldown(s) and " + wucnt + " warmup(s)!");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to call this command.");
        }
        return true;
    }

}
