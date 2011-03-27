package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class WMPlayerListener extends PlayerListener {

    private final WarpManager manager;

    public WMPlayerListener(WarpManager manager) {
        this.manager = manager;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block.getState() instanceof Sign && MyWarp.permissions.permissionOr(event.getPlayer(), PermissionTypes.SIGN_WARP_GLOBAL, PermissionTypes.SIGN_WARP_INVITED, PermissionTypes.SIGN_WARP_OTHER, PermissionTypes.SIGN_WARP_OWN)) {
            SignWarp signWarp = new SignWarp((Sign) block.getState());
            signWarp.warp(this.manager, event.getPlayer());
            event.setUseInteractedBlock(Result.DENY);
            event.setCancelled(true);
        }
    }

    public static String[] parseCommand(String line) {
        return parseLine(line, ' ');
    }

    /**
     * Parses a command line. Reads with quotes/escaping.
     * 
     * <ul>
     * <li>Example 1:
     * <ul>
     * <li>/warp create "hello world"</li>
     * <li>/warp create hello\ world</li>
     * </ul>
     * produces:
     * <ol>
     * <li>/warp</li>
     * <li>create</li>
     * <li>hello world</li>
     * </ol>
     * </li>
     * </ul>
     * 
     * @param line
     *            The command line.
     * @return The parsed segments.
     */
    public static String[] parseLine(String line, char delimiter) {
        boolean quoted = false;
        boolean escaped = false;
        int lastStart = 0;
        int word = 0;
        String value = "";
        List<String> values = new ArrayList<String>(2);
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escaped) {
                escaped = false;
                switch (c) {
                case 's':
                    value += ' ';
                    break;
                default:
                    value += c;
                    break;
                }
            } else {
                switch (c) {
                case '"':
                    quoted = !quoted;
                    break;
                case '\\':
                    escaped = true;
                    break;
                default:
                    if (c == delimiter && !quoted) {
                        if (lastStart < i) {
                            values.add(value);
                            value = "";
                            word++;
                        }
                        lastStart = i + 1;
                    } else {
                        value += c;
                    }
                    break;
                }
            }
        }
        if (!value.isEmpty()) {
            values.add(value);
        }
        return values.toArray(new String[0]);
    }
}
