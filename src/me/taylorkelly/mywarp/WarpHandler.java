package me.taylorkelly.mywarp;

import org.bukkit.entity.Player;

public class WarpHandler extends DefaultHandler {

	@Override
	public boolean permission(Player arg0, String arg1) {
		if (arg1.matches("warp\\.create\\.(private|public)")
				|| arg1.equals("warp.to")) {
			return true; // Everybody can create private/public warps
		} else if (arg1.equals("warp.delete") || arg1.equals("warp.invite")
				|| arg1.equals("warp.uninvite") || arg1.equals("give")
				|| arg1.equals("message")) {
			return arg0.isOp();
		}
		return false;
	}

}
