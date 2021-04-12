package me.lemon.timer.ui;

import me.lemon.timer.Main;
import me.lemon.timer.util.ScreenUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

//This is repurposed from my autocounter project
public class CheckingAreaSelector {

	public CheckingAreaSelector() {
		BufferedImage fullImage = ScreenUtil.captureFullScreen(0);

		JFrame frame = new JFrame("Image Setup");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		SelectionPanel panel = new SelectionPanel(frame);
		panel.setImage(fullImage);
		frame.setContentPane(panel);

		frame.setSize(1280, 720);
		frame.setVisible(true);
	}

	private class SelectionPanel extends JPanel {

		private JFrame parent;
		private BufferedImage image;
		private int screen = 0;
		private float lX = 0, lY = 0, rX = 0.5f, rY = 0.5f;

		public SelectionPanel(JFrame parentF) {
			this.parent = parentF;
			setFocusable(true);
			this.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) { }

				@Override public void mousePressed(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						lX = (1.f * e.getX()) / (1.f * getWidth());
						lY = (1.f * e.getY()) / (1.f * getHeight());
					} else if(e.getButton() == MouseEvent.BUTTON3) {
						rX = (1.f * e.getX()) / (1.f * getWidth());
						rY = (1.f * e.getY()) / (1.f * getHeight());
					}
					repaint();
				}

				@Override public void mouseReleased(MouseEvent e) { }

				@Override public void mouseEntered(MouseEvent e) { }

				@Override public void mouseExited(MouseEvent e) { }
			});
			this.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					for(int i = KeyEvent.VK_1; i <= KeyEvent.VK_9; i++) {
						if(e.getKeyCode() == i) {
							screen = (i - (KeyEvent.VK_1 - 1)) - 1;
							BufferedImage image = ScreenUtil.captureFullScreen(screen);
							if(image != null) {
								setImage(image);
							}
							repaint();
						}
					}
					for(int i = KeyEvent.VK_NUMPAD1; i <= KeyEvent.VK_NUMPAD9; i++) {
						if(e.getKeyCode() == i) {
							screen = (i - (KeyEvent.VK_NUMPAD1 - 1)) - 1;
							BufferedImage image = ScreenUtil.captureFullScreen(screen);
							if(image != null) {
								setImage(image);
							}
							repaint();
						}
					}
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						float fX = lX > rX ? rX : lX;
						float fY = lY > rY ? rY : lY;
						float width = Math.abs(image.getWidth() * lX - image.getWidth() * rX);
						float height = Math.abs(image.getHeight() * lY - image.getHeight() * rY);
						float x = image.getWidth() * fX;
						float y = image.getHeight() * fY;
						BufferedImage subImage = image.getSubimage((int)x, (int)y, (int)width, (int)height);
						Main.getInstance().setDetectionData(new Rectangle((int)x, (int)y, (int)width, (int)height), screen, subImage);
						parent.dispose();
						System.gc();

					}
				}

				@Override public void keyReleased(KeyEvent e) { }
				@Override public void keyTyped(KeyEvent e) { }
			});

		}

		public void setImage(BufferedImage image) {
			this.image = image;
		}

		@Override
		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);

			if(image != null) {
				graphics.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);

				float fX = lX > rX ? rX : lX;
				float fY = lY > rY ? rY : lY;
				int width = (int)Math.abs(this.getWidth() * lX - this.getWidth() * rX);
				int height = (int)Math.abs(this.getHeight() * lY - this.getHeight() * rY);

				graphics.setColor(Color.RED);
				graphics.drawRect((int)(this.getWidth() * fX), (int)(this.getHeight() * fY), width, height);
			}
		}

	}

}
