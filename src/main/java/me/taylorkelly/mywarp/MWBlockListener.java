package me.taylorkelly.mywarp;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.SignChangeEvent;

public class MWBlockListener extends BlockListener {
    private WarpList list;
    
    
    public MWBlockListener(WarpList list) {
        this.list = list;
    }
    
    @Override
    public void onBlockRightClick(BlockRightClickEvent event) {
        Block block = event.getBlock();
        if(block.getState() instanceof Sign && SignWarp.isSignWarp((Sign) block.getState()) && WarpPermissions.signWarp(event.getPlayer())) {
            SignWarp.warpSign((Sign) block.getState(), list, event.getPlayer());
        }
    }

    @Override
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if(SignWarp.isSignWarp(event) && WarpPermissions.createSignWarp(player)) {
            player.sendMessage(ChatColor.AQUA + "Successfully created a sign warp");

        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to create a SignWarp");
            event.setCancelled(true);
        }
    }
}
