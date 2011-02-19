package de.xzise;

public final class MinecraftUtil {
	
	public static final int MAX_LINES_VISIBLE = 10;
	
	private MinecraftUtil() {}
	
	/**
	 * Tries to convert a string into an integer. If the string is invalid it returns <code>null</code>.
	 * @param string The string to be parsed.
	 * @return The value if the string is valid, otherwise <code>null</code>.
	 */
	public static Integer tryAndGetInteger(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
}
