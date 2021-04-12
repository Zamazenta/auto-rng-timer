package me.lemon.timer;

import me.lemon.timer.ui.CheckingAreaSelector;
import me.lemon.timer.util.ImageUtil;
import me.lemon.timer.util.IntFilter;
import me.lemon.timer.util.ScreenUtil;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class Main {

	private int oneMSnano = 1000000;
	private int oneSnano = 1000000000;
	private Thread timerThread, soundThread;
	private boolean framesMode = false;
	private double[] framerates = new double[] {
			59.7275, 59.8261, 59.6555, 60, 30
	};

	private static Main instance;
	private Clip clip;

	public Main() {
		try {
			File audioFile = new File(".\\beep.wav");

			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			AudioFormat format = audioStream.getFormat();

			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioStream);

			clip.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static Main getInstance() {
		return instance;
	}

	public static void main(String... args) {
		instance = new Main();

		new CheckingAreaSelector();
	}

	public void playClip() {
		if(soundThread != null) {
			soundThread.stop();
		}
		(soundThread = new Thread(()-> {
			clip.setFramePosition(0);
			clip.start();
		})).start();
	}

	public void setDetectionData(Rectangle area, int screen) {
		new Thread(() -> {
			JFrame timerWindow = new JFrame("Timer");
			timerWindow.getContentPane().setLayout(new GridLayout(3, 1));
			timerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			timerWindow.setResizable(false);

			JLabel timeLabel = new JLabel("0.000");
			timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
			timeLabel.setFont(new Font("Verdana", Font.BOLD, 35));
			timerWindow.getContentPane().add(timeLabel);

			/*
				These need to be created early so that the text field can access them. Their action listeners get added later because
				the action listeners need to be able to access the text field, which isn't created until after these are created.
			 */
			JComboBox<String> timeModeBox = new JComboBox(new String[] { "MS", "Frames" });
			JComboBox<String> fpsBox = new JComboBox(new String[] { "59.7275", "59.8261", "59.6555", "60", "30" });

			JTextField textField = new JTextField("");
			textField.addKeyListener(new KeyListener() {
				//TODO: Repeating code moment! I should really make this a method but it already works fine so I don't feel like changing it now
				@Override
				public void keyTyped(KeyEvent e) {
					try {
						long ms = textField.getText().contains(",") ? Long.parseLong(textField.getText().split(",")[0])
								: Long.parseLong(textField.getText()); //Lol
						if(timeModeBox.getSelectedIndex() == 1) {
							ms /= framerates[fpsBox.getSelectedIndex()] / 1000.0;
						}
						timeLabel.setText(String.format("%.3f", ms / 1000.f));
					} catch (Exception ex) {}
				}

				@Override
				public void keyPressed(KeyEvent e) {
					try {
						long ms = textField.getText().contains(",") ? Long.parseLong(textField.getText().split(",")[0])
								: Long.parseLong(textField.getText());
						if(timeModeBox.getSelectedIndex() == 1) {
							ms /= framerates[fpsBox.getSelectedIndex()] / 1000.0;
						}
						timeLabel.setText(String.format("%.3f", ms / 1000.f));
					} catch (Exception ex) {}
				}

				@Override
				public void keyReleased(KeyEvent e) {
					try {
						long ms = textField.getText().contains(",") ? Long.parseLong(textField.getText().split(",")[0])
								: Long.parseLong(textField.getText());
						if(timeModeBox.getSelectedIndex() == 1) {
							ms /= framerates[fpsBox.getSelectedIndex()] / 1000.0;
						}
						timeLabel.setText(String.format("%.3f", ms / 1000.f));
					} catch (Exception ex) {}
				}
			});
			((PlainDocument) textField.getDocument()).setDocumentFilter(new IntFilter()); //thanks google

			//see comment above for why these arent added until now - also more repeating code lol
			timeModeBox.addActionListener((e) -> {
				try {
					long ms = textField.getText().contains(",") ? Long.parseLong(textField.getText().split(",")[0])
							: Long.parseLong(textField.getText());
					if(timeModeBox.getSelectedIndex() == 1) {
						ms /= framerates[fpsBox.getSelectedIndex()] / 1000.0;
					}
					timeLabel.setText(String.format("%.3f", ms / 1000.f));
				} catch (Exception ex) {}
			});

			fpsBox.addActionListener((e) -> {
				try {
					long ms = textField.getText().contains(",") ? Long.parseLong(textField.getText().split(",")[0])
							: Long.parseLong(textField.getText());
					if(timeModeBox.getSelectedIndex() == 1) {
						ms /= framerates[fpsBox.getSelectedIndex()] / 1000.0;
					}
					timeLabel.setText(String.format("%.3f", ms / 1000.f));
				} catch (Exception ex) {}
			});

			JComboBox<String> comboBox = new JComboBox(new String[] { "First", "Last" });

			JPanel optionsPanel = new JPanel();
			{
				optionsPanel.add(timeModeBox);
				optionsPanel.add(fpsBox);
				optionsPanel.add(textField);
				optionsPanel.add(comboBox);
			}
			optionsPanel.setLayout(new GridLayout(2, 2));
			timerWindow.getContentPane().add(optionsPanel);

			JPanel lowerPanel = new JPanel();
			{
				JPanel calibratePanel = new JPanel();
				{
					JTextField hitField = new JTextField("");
					((PlainDocument) hitField.getDocument()).setDocumentFilter(new IntFilter()); //thanks google
					calibratePanel.add(hitField);

					JButton calibrate = new JButton("Calibrate");
					calibrate.addActionListener((e) -> {
						try {
							int hit = Integer.parseInt(hitField.getText());
							//i *could* define these outside of the method and make methods to replace repeating code but... No.
							String[] inputStrings;
							if (textField.getText().contains(",")) {
								inputStrings = textField.getText().split(",");
							} else {
								inputStrings = new String[]{textField.getText()};
							}

							int target = Integer.parseInt(inputStrings[0]);
							int calibrated = target + (target - hit);
							inputStrings[0] = calibrated + ""; //lol it works
							String calibratedInput = "";
							for(int i = 0; i < inputStrings.length; i++) {
								calibratedInput += inputStrings[i] + (i < (inputStrings.length - 1) ? "," : "");
							}
							textField.setText(calibratedInput);

							//duplicate code again :)
							long ms = textField.getText().contains(",") ? Long.parseLong(textField.getText().split(",")[0])
									: Long.parseLong(textField.getText()); //Lol
							if(timeModeBox.getSelectedIndex() == 1) {
								ms /= framerates[fpsBox.getSelectedIndex()] / 1000.0;
							}
							timeLabel.setText(String.format("%.3f", ms / 1000.f));
						} catch(Exception ex) {}
					});
					calibratePanel.add(calibrate);
				}
				calibratePanel.setLayout(new GridLayout(1, 2));
				lowerPanel.add(calibratePanel);
				JButton startButton = new JButton("Start");

				//TODO: CLEAN UP THIS CODE - it works fine though so that can wait :)
				startButton.addActionListener((e) -> {
					if (timerThread != null) {
						timerThread.stop();
					}

					framesMode = timeModeBox.getSelectedIndex() == 1;

					String[] inputStrings;
					if (textField.getText().contains(",")) {
						inputStrings = textField.getText().split(",");
					} else {
						inputStrings = new String[]{textField.getText()};
					}
					long input = (long) (Long.parseLong(inputStrings[0]) / (framesMode ? framerates[fpsBox.getSelectedIndex()] / 1000.0 : 1.0));

					timeLabel.setText(String.format("%.3f", input / 1000.f));

					//I LOVE MY BRAIN!
					int count = 0;
					switch (comboBox.getSelectedIndex()) {
						case 0: {
							while (!ImageUtil.checkSolidColor(ScreenUtil.captureAreaOfScreen(area, screen), 5)) {
								try {
									Thread.sleep(0, (int) (oneSnano / framerates[fpsBox.getSelectedIndex()]));
								} catch (Exception ex) {
								}
							}
							break;
						}
						case 1: {
							boolean reset = false, passedReset = false;
							while (!passedReset) {
								try {
									boolean result = ImageUtil.checkSolidColor(ScreenUtil.captureAreaOfScreen(area, screen), 5);
									if (!result && reset) {
										passedReset = true;
									}
									reset = result;
									if (reset) {
										count++;
										System.out.println(count);
									}
									Thread.sleep(0, (int) (oneSnano / framerates[fpsBox.getSelectedIndex()]));
								} catch (Exception ex) {
								}
							}
							break;
						}
					}

					(timerThread = new Thread(() -> {
						for (int i = 0; i < inputStrings.length; i++) {
							long curInput = Long.parseLong(inputStrings[i]) * oneMSnano;
							if (framesMode) {
								curInput /= framerates[fpsBox.getSelectedIndex()] / 1000.0;
							}
							timeLabel.setText(String.format("%.3f", curInput / oneMSnano / 1000.f));
							long endTime = System.nanoTime() + curInput, beep = endTime - (oneMSnano * 2500L), update = System.nanoTime() + oneMSnano * 11;
							while (System.nanoTime() < endTime) {
								try {
									long nanoTime = System.nanoTime();
									if (nanoTime > update) {
										timeLabel.setText(String.format("%.3f", ((endTime - nanoTime) / oneMSnano) / 1000.f));
										update += oneMSnano * 11;
									}
									if (nanoTime > beep) {
										playClip();
										beep += 500L * oneMSnano;
									}

									Thread.sleep(0, oneMSnano / 5);
								} catch (Exception ex) {
								}
							}
							playClip();
						}
						timeLabel.setText(String.format("%.3f", input / 1000.f));
					})).start();
				});
				lowerPanel.add(startButton);
			}
			lowerPanel.setLayout(new GridLayout(2, 1));
			timerWindow.getContentPane().add(lowerPanel);

			timerWindow.setSize(400, 200);
			timerWindow.setVisible(true);
		}).start();
	}
}
