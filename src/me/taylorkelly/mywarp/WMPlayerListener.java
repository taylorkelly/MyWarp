package me.taylorkelly.mywarp;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class WMPlayerListener extends PlayerListener {

    private final WarpManager manager;
    private final PluginProperties properties;

    public WMPlayerListener(WarpManager manager, PluginProperties properties) {
        this.manager = manager;
        this.properties = properties;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getState() instanceof Sign && MyWarp.permissions.permissionOr(event.getPlayer(), PermissionTypes.SIGN_WARP_GLOBAL, PermissionTypes.SIGN_WARP_INVITED, PermissionTypes.SIGN_WARP_OTHER, PermissionTypes.SIGN_WARP_OWN)) {
            SignWarp signWarp = new SignWarp((Sign) block.getState());
            if (signWarp.warp(this.manager, event.getPlayer())) {
                event.setUseInteractedBlock(Result.DENY);
                event.setCancelled(true);
            }
        }
    }
    
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.properties.isCancelWarmUpOnMovement()) {
            this.manager.getWarmUp().cancelWarmUp(event.getPlayer());
        }
    }
}
