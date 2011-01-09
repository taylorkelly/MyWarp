package org.angelsl.minecraft.randomshit.fontwidth;

/**
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE Version 2, December 2004
 * Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 * 
 * Everyone is permitted to copy and distribute verbatim or modified copies of
 * this license document, and changing it is allowed as long as the name is
 * changed.
 * 
 * DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE TERMS AND CONDITIONS FOR COPYING,
 * DISTRIBUTION AND MODIFICATION
 * 
 * 0. You just DO WHAT THE FUCK YOU WANT TO.
 */
public class MinecraftFontWidthCalculator {

	private static String weirdHardcodedShitIFoundInMinecraft = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»";
	private static int[] charWidth = {1,9,9,8,8,8,8,7,9,8,9,9,8,9,9,9,8,8,8,8,9,9,8,9,8,8,8,8,8,9,9,9,4,2,5,6,6,6,6,3,5,5,5,6,2,6,2,6,6,6,6,6,6,6,6,6,6,6,2,2,5,6,5,6,7,6,6,6,6,6,6,6,6,4,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,4,6,4,6,6,3,6,6,6,6,6,5,6,6,2,6,5,3,6,6,6,6,6,6,6,4,6,6,6,6,6,6,5,2,5,7,6,6,6,6,6,6,6,6,6,6,6,6,4,6,3,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,4,6,6,3,6,6,6,6,6,6,6,7,6,6,6,2,6,6,8,9,9,6,6,6,8,8,6,8,8,8,8,8,6,6,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,6,9,9,9,5,9,9,8,7,7,8,7,8,8,8,7,8,8,7,9,9,6,7,7,7,7,7,9,6,7,8,7,6,6,9,7,6,7,1};


	// taken directly from notchcode. enjoy
	public static int getStringWidth(String s) {
		if (s == null) {
			return 0;
		}
		int i = 0;
		for (int j = 0; j < s.length(); j++) {
			if (s.charAt(j) == '\247') {
				j++;
				continue;
			}
			int k = weirdHardcodedShitIFoundInMinecraft.indexOf(s.charAt(j));
			if (k >= 0) {
				i += charWidth[k + 32];
			}
		}

		return i;
	}
}
