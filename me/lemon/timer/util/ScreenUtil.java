package me.lemon.timer.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenUtil {

	private static Robot robot;

	public static BufferedImage captureFullScreen(int screenNumber) {
		try {
			Rectangle screenRect = new Rectangle(0, 0, 0, 0);
			if(screenNumber == -1) {
				for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
					screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
				}
			} else {
				screenRect = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screenNumber].getDefaultConfiguration().getBounds();
			}
			return robot.createScreenCapture(screenRect);
		} catch(Exception e) {
			return null;
		}
	}

	public static BufferedImage captureAreaOfScreen(Rectangle area, int screenNumber) {
		try {
			Rectangle screenRect = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screenNumber].getDefaultConfiguration().getBounds();
			Rectangle desiredRect = new Rectangle((int) (screenRect.getX() + area.getX()),
					(int) (screenRect.getY() + area.getY()),
					(int) (area.getWidth()),
					(int) (area.getHeight())
			);
			return robot.createScreenCapture(desiredRect);
		} catch(Exception e) {
			return null;
		}
	}

	static {
		try { robot = new Robot(); } catch(Exception e) {}
	}
}
