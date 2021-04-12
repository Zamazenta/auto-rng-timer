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

	public static boolean compare(BufferedImage one, BufferedImage two, int similarity) {
		if(one.getWidth() != two.getWidth() || one.getHeight() != two.getHeight()) {
			return false;
		}

		for(int x = 0; x < one.getWidth(); x++) {
			for(int y = 0; y < one.getHeight(); y++) {
				if(similarity >= 100) {
					if(one.getRGB(x, y) != two.getRGB(x, y)) {
						return false;
					}
				} else {
					Color colorOne = new Color(one.getRGB(x, y)),
							colorTwo = new Color(two.getRGB(x, y));
					int redPercent = rgbPercentage(colorOne.getRed(), colorTwo.getRed()),
							greenPercent = rgbPercentage(colorOne.getGreen(), colorTwo.getGreen()),
							bluePercent = rgbPercentage(colorOne.getBlue(), colorTwo.getBlue());

					if (redPercent < similarity
							|| greenPercent < similarity
							|| bluePercent < similarity) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static int rgbPercentage(int f1, int f2) {
		int percent = 100;
		if(f1 != f2) {
			float diff = Math.abs(f1 - f2) / 255.f;
			percent = 100 - (int)(diff * 100.f);
		}
		return percent;
	}
}
