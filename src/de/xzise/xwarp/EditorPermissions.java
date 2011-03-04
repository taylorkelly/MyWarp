package de.xzise.xwarp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditorPermissions implements Map<Permissions, Boolean> {
	
	private final Boolean[] values;

	public EditorPermissions() {
		this.values = new Boolean[Permissions.values().length];
	}
	
	public String getPermissionString() {
		Permissions[] pms = this.getByValue(true);
		char[] editorPermissions = new char[pms.length];
		for (int j = 0; j < pms.length; j++) {
			editorPermissions[j] = pms[j].value;
		}
		return new String(editorPermissions);
	}
	
	@Override
	public int size() {
		return this.values.length;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof Permissions) {
			return this.values[((Permissions) key).id] != null;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Permissions[] getByValue(boolean value) {
		List<Permissions> result = new ArrayList<Permissions>();
		for (Entry<Permissions, Boolean> entry : this.entrySet()) {
			if (entry.getValue() == null) {
				if (!value) {
					result.add(entry.getKey());
				}
			} else if (value == entry.getValue()) {
				result.add(entry.getKey());
			}
		}
		return result.toArray(new Permissions[0]);
	}

	@Override
	public Boolean get(Object key) {
		if (key instanceof Permissions) {
			return this.values[((Permissions) key).id];
		}
		return null;
	}
	
	public boolean get(Permissions permission) {
		Boolean b = this.get((Object) permission);
		if (b != null) {
			return b;
		} else {
			return false;
		}
	}

	@Override
	public Boolean put(Permissions key, Boolean value) {
		return this.values[key.id] = value;
	}

	@Override
	public Boolean remove(Object key) {
		if (key instanceof Permissions) {
			return this.put((Permissions) key, null);
		} else {
			return null;
		}
	}

	@Override
	public void putAll(Map<? extends Permissions, ? extends Boolean> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = null;
		}
	}

	@Override
	public Set<Permissions> keySet() {
		Set<Permissions> set = new HashSet<Permissions>();
		for (Permissions permissions : Permissions.values()) {
			set.add(permissions);
		}
		return set;
	}

	@Override
	public Collection<Boolean> values() {
		return Arrays.asList(this.values);
	}
	
	private class EditorPermissionEntry implements Entry<Permissions, Boolean> {

		private Permissions p;
		private Boolean b;
		
		public EditorPermissionEntry(Permissions p, Boolean b) {
			this.p = p;
			this.b = b;
		}
		
		@Override
		public Permissions getKey() {
			return this.p;
		}

		@Override
		public Boolean getValue() {
			return this.b;
		}

		@Override
		public Boolean setValue(Boolean value) {
			Boolean old = this.b;
			this.b = value;
			return old;
		}
		
	}

	@Override
	public Set<java.util.Map.Entry<Permissions, Boolean>> entrySet() {
		Set<java.util.Map.Entry<Permissions, Boolean>> set = new HashSet<Map.Entry<Permissions,Boolean>>();
		for (Permissions permissions : Permissions.values()) {
			set.add(new EditorPermissionEntry(permissions, this.values[permissions.id]));
		}
		return set;
	}
	
	public void parseString(String string, boolean reset) {
		Set<Permissions> p = Permissions.parseString(string);
		if (reset) {
			Arrays.fill(this.values, false);
		}
		for (Permissions permissions : p) {
			this.values[permissions.id] = true;
		}
	}
	
}
