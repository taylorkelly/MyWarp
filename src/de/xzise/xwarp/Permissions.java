package de.xzise.xwarp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Permissions {
	UPDATE('l', 0),
	RENAME('r', 1),
	UNINVITE('u', 2),
	INVITE('i', 3),
	PRIVATE('+', 4),
	PUBLIC('-', 5),
	GLOBAL('!', 6),
	GIVE('g', 7),
	DELETE('d', 8),
	WARP('w', 9);
	
	public final char value;
	public final int id;

	public static final Set<Permissions> DEFAULT;
	private static final Map<Character, Permissions> CHAR_MAP = new HashMap<Character, Permissions>();
	private static final Map<Integer, Permissions> INT_MAP = new HashMap<Integer, Permissions>();
	
	private Permissions(char value, int id) {
		this.value = value;
		this.id = id;
	}
	
	static {
		DEFAULT = new HashSet<Permissions>();
		DEFAULT.add(UPDATE);
		DEFAULT.add(RENAME);
		DEFAULT.add(UNINVITE);
		DEFAULT.add(INVITE);
		DEFAULT.add(WARP);
		
		for (Permissions perm : Permissions.values()) {
			CHAR_MAP.put(perm.value, perm);
			INT_MAP.put(perm.id, perm);
		}
	}
	
	public static Permissions getById(int id) {
		return INT_MAP.get(id);
	}
	
	public static Permissions getByChar(char ch) {
		return CHAR_MAP.get(ch);
	}
	
	public static Set<Permissions> parseString(String permissions) {
		Set<Permissions> result = new HashSet<Permissions>();
		parseString(permissions, result);
		return result;
	}
	
	public static void parseString(String permissions, Set<Permissions> result) {
		result.clear();
		boolean remove = false;
		
		for (char c : permissions.toLowerCase().toCharArray()) {
			if (c == '/') {
				remove = true;
			} else if (c == '*') {
				if (remove) {
					result.removeAll(Arrays.asList(Permissions.values()));
				} else {
					result.addAll(Arrays.asList(Permissions.values()));
				}
			} else if (c == 's') {
				if (remove) {
					result.removeAll(DEFAULT);
				} else {
					result.addAll(DEFAULT);
				}
			} else {
				Permissions p = CHAR_MAP.get(c);
				if (p != null) {
					if (remove) {
						result.remove(p);
					} else {
						result.add(p);
					}
				} else {
					//TODO: How to react?
				}
			}
		}
	}
}
