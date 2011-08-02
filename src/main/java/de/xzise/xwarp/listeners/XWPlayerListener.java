package de.xzise.xwarp.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.commands.wpa.CreateCommand;
import de.xzise.xwarp.signwarps.SignWarp;

public class XWPlayerListener extends PlayerListener {

    private final WarpManager manager;
    private final PluginProperties properties;
    private final CreateCommand createCommand;

    public XWPlayerListener(WarpManager manager, PluginProperties properties, CreateCommand createCommand) {
        this.manager = manager;
        this.properties = properties;
        this.createCommand = createCommand;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        event.getPlayer().sendMessage("Hit! block == null ? " + (block == null) + " cancelled ? " + event.isCancelled());
        if (block != null && !event.isCancelled()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getState() instanceof Sign) {
                SignWarp signWarp = new SignWarp((Sign) block.getState());
                if (signWarp.warp(this.manager, event.getPlayer())) {
                    event.setUseInteractedBlock(Result.DENY);
                    event.setCancelled(true);
                }    
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                this.createCommand.hitBlock(event.getPlayer(), new FixedLocation(block.getLocation()));
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.properties.isCancelWarmUpOnMovement()) {
            if (this.manager.getWarmUp().cancelWarmUp(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + "WarmUp was canceled due to movement!");
            }
        }
    }
}
