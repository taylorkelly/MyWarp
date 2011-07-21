package de.xzise.xwarp.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.timer.WarmUp;

public class XWEntityListener extends EntityListener {
    
    private final PluginProperties properties;
    private final WarmUp warmUp;
    
    public XWEntityListener(PluginProperties properties, WarmUp warmUp) {
        this.properties = properties;
        this.warmUp = warmUp;
    }
    
    public void onEntityDamage(EntityDamageEvent event) {
        if (this.properties.isCancelWarmUpOnDamage()) {
            if (event.getEntity() instanceof Player) {
                if (this.warmUp.cancelWarmUp((Player) event.getEntity())) {
                    ((Player) event.getEntity()).sendMessage(ChatColor.RED + "WarmUp was canceled due to damage!");
                }
            }
        }
    }
}
