package me.taylorkelly.mywarp;

import com.nijiko.permissions.PermissionHandler;

public abstract class DefaultHandler extends PermissionHandler {

	@Override
	public boolean canGroupBuild(String arg0) {
		return true;
	}

	@Override
	public String getGroup(String arg0) {
		return "";
	}

	@Override
	public String getGroupPrefix(String arg0) {
		return "";
	}

	@Override
	public String getGroupSuffix(String arg0) {
		return "";
	}

	@Override
	public boolean inGroup(String arg0, String arg1) {
		return arg1.isEmpty();
	}

	@Override
	public void load() { }
}
