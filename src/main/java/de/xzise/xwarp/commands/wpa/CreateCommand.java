package de.xzise.xwarp.commands.wpa;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.xwarp.WPAManager;
import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.WorldWrapper;
import de.xzise.xwarp.commands.DefaultSubCommand;

public class CreateCommand extends DefaultSubCommand<WPAManager> {

    private class WPAData {
        public final String owner;
        public final String creator;
        public final String name;
        public final World world;
        
        private FixedLocation loc1 = null;
        private FixedLocation loc2 = null;

        public WPAData(String name, String owner, World world) {
            this(name, owner, owner, world);
        }

        public WPAData(String name, String owner, String creator, World world) {
            this.name = name;
            this.owner = owner;
            this.creator = creator;
            this.world = world;
        }

        public boolean addLocation(FixedLocation loc) {
            if (this.loc1 == null) {
                this.loc1 = loc;
                return true;
            } else if (this.loc2 == null) {
                this.loc2 = loc;
            }
            return false;
        }

        public WarpProtectionArea build() {
            if (loc1 != null && loc2 != null) {
                return new WarpProtectionArea(new WorldWrapper(this.world), this.loc1, this.loc2, this.name, this.owner, this.creator);
            } else {
                return null;
            }
        }
    }

    private final Map<String, WPAData> wpaData = Maps.newHashMap();

    public CreateCommand(WPAManager manager, Server server) {
        super(manager, server, "create", "+", "add");
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Create new warp protection area." };
    }

    @Override
    public String getSmallHelpText() {
        return "Create protection area";
    }

    @Override
    public String getCommand() {
        return "wpa create <name>";
    }

    public void stopCreation(String playerName, CommandSender executor) {
        Player player = this.server.getPlayer(playerName);
        if (player != null) {
            if (executor == null) {
                executor = player;
            }
            WPAData data = this.wpaData.remove(player.getName()); 
            if (data != null) {
                if (player != executor) {
                    player.sendMessage("Your warp protection area creation was stoped!");
                }
                executor.sendMessage("Successfully stoped creation of '" + ChatColor.GREEN + data.name + ChatColor.WHITE + "'.");
            } else {
                if (player != executor) {
                    executor.sendMessage(ChatColor.RED + "This player doesn't create a warp protection area.");
                } else {
                    player.sendMessage(ChatColor.RED + "You don't create any warp protection area.");
                }
            }
        } else {
            executor.sendMessage(ChatColor.RED + "There is no player online with this name.");
        }
    }

    public boolean hitBlock(Player player, FixedLocation location) {
        WPAData data = this.wpaData.get(player.getName());
        if (data != null) {
            if (location.world == data.world) {
                if (data.addLocation(location)) {
                    player.sendMessage("Added first location to new protection area.");
                } else {
                    player.sendMessage("Added second location to new protection area.");
                    WarpProtectionArea wpa = data.build();
                    this.manager.addWPA(wpa);
                    player.sendMessage("Successfully created protection area '" + ChatColor.GREEN + wpa.getName() + ChatColor.WHITE + "'.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You are not in the same world as before.");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (sender instanceof Player) {
            if (parameters.length == 2) {
                Player player = (Player) sender;
                String playerName = player.getName();
                if (this.wpaData.containsKey(playerName)) {
                    sender.sendMessage(ChatColor.RED + "You already started a creations. To stop use /wpa stop-create");
                } else {
                    WPAData newWPA = new WPAData(parameters[1], playerName, player.getWorld());
                    this.wpaData.put(playerName, newWPA);
                    sender.sendMessage("Started creation. Select the border with a wooden sword.");
                }
                return true;
            } else {
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only ingame players could create protection areas.");
            return true;
        }
    }

}
