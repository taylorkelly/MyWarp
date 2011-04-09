package me.taylorkelly.mywarp;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.EditorPermissions;
import de.xzise.xwarp.Permissions;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.warpable.WarpablePlayer;
import de.xzise.xwarp.warpable.WarperFactory;

public class Warp {
	
	public enum Visibility {
		PRIVATE(0),
		PUBLIC(1),
		GLOBAL(2);
		
		public final int level;
		
		private Visibility(int level) {
			this.level = level;
		}
		
		public static Visibility parseLevel(int level) {
			for (Visibility visibility : Visibility.values()) {
				if (visibility.level == level) {
					return visibility;
				}
			}
			return null;
		}
	}
	
	public int index;
	public String name;
	public String creator;
	private Location location;
	private String owner;
	public Visibility visibility;
	public String welcomeMessage;
	public Map<String, EditorPermissions> editors;

	public static int nextIndex = 1;

	public Warp(int index, String name, String creator, String owner, Location location, Visibility visibility,
			Map<String, EditorPermissions> permissions, String welcomeMessage) {
		this.index = index;
		this.name = name;
		this.creator = creator;
		this.location = location.clone();
		this.visibility = visibility;
		if (permissions == null) {
			this.editors = new HashMap<String, EditorPermissions>();
		} else {
			this.editors = new HashMap<String, EditorPermissions>(permissions);
		}
		this.welcomeMessage = welcomeMessage;
		if (index > nextIndex)
			nextIndex = index;
		nextIndex++;
	}
	
	public Warp(String name, String creator, String owner, Location location) {
		this(nextIndex, name, creator, owner, location, Visibility.PUBLIC, null, "Welcome to '" + name + "'");
	}

	public Warp(String name, Player creator) {
		this(name, creator.getName(), creator.getName(), creator.getLocation());
	}

	public Warp(String name, Location location) {
		this(name, "", "No Player", location);
	}
	
	public boolean playerIsInvited(String name) {
		EditorPermissions ep = this.editors.get(name.toLowerCase());
		if (ep != null) {
			return ep.get(Permissions.WARP);
		} else {
			return false;
		}
	}

	public boolean playerCanWarp(CommandSender sender, boolean viaSign) {
	    String name = null;
	    if (sender instanceof WarpablePlayer) {
	        name = ((WarpablePlayer) sender).getName();
	    } else if (sender instanceof Player) {
	        name = ((Player) sender).getName();
	    }
	    
		if (name != null && this.owner.equals(name) && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_OWN : PermissionTypes.TO_OWN))
			return true;
		if (name != null && this.playerIsInvited(name) && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_INVITED : PermissionTypes.TO_INVITED))
			return true;
		if (this.visibility == Visibility.PUBLIC && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_OTHER : PermissionTypes.TO_OTHER))
			return true;
		if (this.visibility == Visibility.GLOBAL && MyWarp.permissions.permission(sender, viaSign ? PermissionTypes.SIGN_WARP_GLOBAL : PermissionTypes.TO_GLOBAL))
			return true;
		return MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_TO_ALL);
	}
	
	public boolean playerCanWarp(Warpable player) {
		//TODO: More elegant version?
		return playerCanWarp(player, true) || playerCanWarp(player, false);
	}
	
	public void setLocation(Positionable positionable) {
	    this.location = positionable.getLocation().clone();
	}
	
	public void rename(String newName) {
		this.name = newName;
	}
	
	public boolean isOwn(String name) {
	    return this.owner.equals(name);
	}

	public void invite(String player) {
		this.getPermissions(player).put(Permissions.WARP, true);
	}

	public void uninvite(String inviteeName) {
		this.getPermissions(inviteeName).put(Permissions.WARP, false);
	}

	public boolean playerCanModify(Player player, Permissions permission) {
		if (this.isOwn(player.getName()))
			return true;
		EditorPermissions ep = this.editors.get(player.getName().toLowerCase());
		if (ep != null) {
			return ep.get(permission);
		}
		return false;
	}
	
    public boolean listWarp(CommandSender sender) {

        // Admin permissions
        if (MyWarp.permissions.hasAdminPermission(sender))
            return true;

        Warpable warpable = WarperFactory.getWarpable(sender);

        if (warpable != null) {
            // Can warp
            if (this.playerCanWarp(warpable))
                return true;
            if (sender instanceof WarpablePlayer) {
                // Creator permissions
                if (this.isOwn(((WarpablePlayer) sender).getName()))
                    return true;
            }
        }

        return false;
    }

	public void setCreator(String giveeName) {
		this.creator = giveeName;
	}
	
	public void setMessage(String message) {
		this.welcomeMessage = message;
	}
	
	public Location getLocation() {
		return this.location.clone();
	}
	
	public boolean isValid() {
		return this.location.getWorld() != null;
	}
	
	public String getOwner() {
	    return this.owner;
	}
	
	public void setOwner(String owner) {
	    this.owner = owner;
	}
	
	public void setLocation(Location location) {
		this.location = location.clone();
	}

	public EditorPermissions getEditorPermissions(String name) {
		EditorPermissions player = this.editors.get(name.toLowerCase());
		if (player == null) {
			return null;
		}
		return player;
	}
	
	public String[] getEditors() {
		return this.editors.keySet().toArray(new String[0]);
	}
	
	private EditorPermissions getPermissions(String name) {
		EditorPermissions player = this.editors.get(name.toLowerCase());
		if (player == null) {
			player = new EditorPermissions();
			this.editors.put(name.toLowerCase(), player);
		}
		return player;
	}
	
	public void addEditor(String name, String permissions) {
		this.getPermissions(name).parseString(permissions, true);
	}
	
	public void removeEditor(String name) {
		this.editors.remove(name.toLowerCase());
	}
	
	public static final WarpComparator WARP_NAME_COMPARATOR = new WarpComparator() {

		@Override
		public int compare(Warp warp1, Warp warp2) {
			return warp1.name.compareTo(warp2.name);
		}
		
	};
	
	public static final WarpComparator WARP_INDEX_COMPARATOR = new WarpComparator() {
		
		@Override
		public int compare(Warp warp1, Warp warp2) {
			return new Integer(warp1.index).compareTo(warp2.index);
		}
	};
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Warp) {
			Warp w = (Warp) o;
			return w.name.equals(this.name) && w.owner.equals(this.owner);
		} else {
			return false;
		}
	}
}

interface WarpComparator extends Comparator<Warp> {}
