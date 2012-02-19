package de.xzise.xwarp.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.commands.wpa.CreateCommand;
import de.xzise.xwarp.signwarps.SignWarp;

public class XWPlayerListener implements Listener {

    private final WarpManager manager;
    private final PluginProperties properties;
    private final CreateCommand createCommand;

    public XWPlayerListener(WarpManager manager, PluginProperties properties, CreateCommand createCommand) {
        this.manager = manager;
        this.properties = properties;
        this.createCommand = createCommand;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (block != null && !event.isCancelled()) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getState() instanceof Sign) {
                SignWarp signWarp = new SignWarp((Sign) block.getState());
                if (signWarp.warp(this.manager, event.getPlayer())) {
                    event.setUseInteractedBlock(Result.DENY);
                    event.setCancelled(true);
                }
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK && inHand(player, Material.WOOD_SWORD)) {
                this.createCommand.hitBlock(player, new FixedLocation(block.getLocation()));
            }
        }
    }

    public static boolean inHand(Player player, Material material) {
        ItemStack stack = player.getItemInHand();
        return stack == null ? material == null : stack.getType() == material;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.isCancelled() && this.properties.isCancelWarmUpOnMovement()) {
            if (this.manager.getWarmUp().cancelWarmUp(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + "WarmUp was canceled due to movement!");
            }
        }
    }
}
