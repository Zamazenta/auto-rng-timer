package me.lemon.timer.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {

	public static boolean checkSolidColor(BufferedImage img, int maxDiff) {
		for(int x = 0; x < img.getWidth(); x++) {
			for(int y = 0; y < img.getHeight(); y++) {
				Color color = new Color(img.getRGB(x, y));
				int r = color.getRed(), g = color.getGreen(), b = color.getBlue();

				if(Math.abs(r - g) > maxDiff || Math.abs(r - b) > maxDiff || Math.abs(g - b) > maxDiff) {
					return false;
				}
			}
		}
		return true;
	}
}
