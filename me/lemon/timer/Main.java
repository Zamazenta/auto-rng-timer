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
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

	private static Main instance;
	private Clip clip;
	private Rectangle area;
	private int screen;
	private BufferedImage matchImage;
	private JLabel timerLabel;
	private JComboBox<String> timeModeBox, fpsBox, modeBox;
	private JTextField targetField, beepsField;
	private JButton startButton;

	private Thread timerThread, soundThread;
	private boolean framesMode = false;

	private final int oneMSnano = 1000000;
	private final int oneSnano = 1000000000;
	private final double[] framerates = new double[] {
			59.7275, 59.8261, 59.6555, 60, 30
	};

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

	public void createTimerWindow(Rectangle area, int screen, BufferedImage matchImage) {
		this.area = area;
		this.screen = screen;
		this.matchImage = matchImage;

		JFrame timerWindow = new JFrame("Timer");
		timerWindow.getContentPane().setLayout(new GridLayout(3, 1));
		timerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timerWindow.setResizable(false);

		this.timerLabel = new JLabel("0.000");
		this.timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.timerLabel.setFont(new Font("Verdana", Font.BOLD, 35));
		timerWindow.getContentPane().add(this.timerLabel);

		JPanel optionsPanel = new JPanel();
		{
			JPanel upperInputPanel = new JPanel();
			{
				this.beepsField = new JTextField("6");
				((PlainDocument) this.beepsField.getDocument()).setDocumentFilter(new IntFilter());
				upperInputPanel.add(this.beepsField);

				this.timeModeBox = new JComboBox<>(new String[]{"MS", "Frames"});
				this.timeModeBox.addActionListener((e) -> updateTimeLabel());
				upperInputPanel.add(this.timeModeBox);

				this.fpsBox = new JComboBox<>(new String[]{"59.7275", "59.8261", "59.6555", "60", "30"});
				this.fpsBox.addActionListener((e) -> updateTimeLabel());
				upperInputPanel.add(this.fpsBox);
			}
			upperInputPanel.setLayout(new GridLayout(1, 3));
			optionsPanel.add(upperInputPanel);

			JPanel lowerInputPanel = new JPanel();
			{
				this.targetField = new JTextField("");
				this.targetField.addKeyListener(new KeyListener() {
					@Override public void keyTyped(KeyEvent e) { updateTimeLabel(); }
					@Override public void keyPressed(KeyEvent e) { updateTimeLabel(); }
					@Override public void keyReleased(KeyEvent e) { updateTimeLabel(); }
				});
				((PlainDocument) this.targetField.getDocument()).setDocumentFilter(new IntFilter());
				lowerInputPanel.add(this.targetField);

				this.modeBox = new JComboBox<>(new String[]{"First Reset Frame", "Last Reset Frame", "Match Selection"});
				lowerInputPanel.add(this.modeBox);
			}
			lowerInputPanel.setLayout(new GridLayout(1, 2));
			optionsPanel.add(lowerInputPanel);
		}
		optionsPanel.setLayout(new GridLayout(2, 1));
		timerWindow.getContentPane().add(optionsPanel);

		JPanel lowerPanel = new JPanel();
		{
			JPanel calibratePanel = new JPanel();
			{
				JTextField hitField = new JTextField("");
				((PlainDocument) hitField.getDocument()).setDocumentFilter(new IntFilter());
				calibratePanel.add(hitField);

				JButton calibrate = new JButton("Calibrate");
				calibrate.addActionListener((e) -> {
					try {
						int hit = Integer.parseInt(hitField.getText());
						String[] inputStrings = getUserInput();

						int target = Integer.parseInt(inputStrings[0]);
						int calibrated = target + (target - hit);
						inputStrings[0] = calibrated + ""; //lol it works
						String calibratedInput = "";
						for(int i = 0; i < inputStrings.length; i++) {
							calibratedInput += inputStrings[i] + (i < (inputStrings.length - 1) ? "," : "");
						}
						this.targetField.setText(calibratedInput);

						this.updateTimeLabel();
					} catch(Exception ex) {}
				});
				calibratePanel.add(calibrate);
			}
			calibratePanel.setLayout(new GridLayout(1, 2));
			lowerPanel.add(calibratePanel);

			this.startButton = new JButton("Start");
			this.startButton.addActionListener((e) -> {
				if (this.timerThread != null) {
					this.timerThread.stop();
				}
				this.startButton.setText("Restart");
				this.startTimerThread();
			});
			lowerPanel.add(this.startButton);
		}
		lowerPanel.setLayout(new GridLayout(2, 1));
		timerWindow.getContentPane().add(lowerPanel);

		timerWindow.setSize(400, 200);
		timerWindow.setVisible(true);
	}

	private void updateTimeLabel() {
		try {
			long ms = targetField.getText().contains(",") ? Long.parseLong(targetField.getText().split(",")[0])
					: Long.parseLong(targetField.getText()); //Lol
			if(timeModeBox.getSelectedIndex() == 1) {
				ms /= framerates[fpsBox.getSelectedIndex()] / 1000.0;
			}
			this.timerLabel.setText(String.format("%.3f", ms / 1000.f));
		} catch (Exception ex) {}
	}

	public String[] getUserInput() {
		String[] inputStrings;
		if (this.targetField.getText().contains(",")) {
			inputStrings = this.targetField.getText().split(",");
		} else {
			inputStrings = new String[]{ this.targetField.getText() };
		}
		return inputStrings;
	}

	public void waitToStartTimer() {
		switch (this.modeBox.getSelectedIndex()) {
			case 0: {
				while (!ImageUtil.checkSolidColor(ScreenUtil.captureAreaOfScreen(this.area, this.screen), 5)) {
					try {
						Thread.sleep(0, oneSnano / 120);
					} catch (Exception ex) {}
				}
				break;
			}
			case 1: {
				boolean reset = false, passedReset = false;
				while (!passedReset) {
					try {
						boolean result = ImageUtil.checkSolidColor(ScreenUtil.captureAreaOfScreen(this.area, this.screen), 5);
						if (!result && reset) {
							passedReset = true;
						}
						reset = result;
						Thread.sleep(0, oneSnano / 120);
					} catch (Exception ex) {}
				}
				break;
			}
			case 2: {
				while(!ImageUtil.compare(matchImage, ScreenUtil.captureAreaOfScreen(this.area, this.screen), 95)) {
					try {
						Thread.sleep(0, oneSnano / 120);
					} catch(Exception ex) {}
				}
				break;
			}
		}
	}

	public void startTimerThread() {
		(this.timerThread = new Thread(() -> {
			this.framesMode = this.timeModeBox.getSelectedIndex() == 1;
			double fps = this.framerates[this.fpsBox.getSelectedIndex()];
			int beeps = Integer.parseInt(this.beepsField.getText()) - 1;
			String[] inputStrings = getUserInput();

			this.waitToStartTimer();

			for (String inputString : inputStrings) {
				long curInput = Long.parseLong(inputString);
				if (this.framesMode) {
					curInput /= fps / 1000.0;
				}

				this.timerLabel.setText(String.format("%.3f", curInput / 1000.f));
				long endTime = System.currentTimeMillis() + curInput, beep = endTime - (500L * beeps),
						update = System.currentTimeMillis() + 11, curTime;

				while ((curTime = System.currentTimeMillis()) < endTime) {
					try {
						if (curTime >= update) {
							this.timerLabel.setText(String.format("%.3f", (endTime - curTime) / 1000.f));
							update += 11L;
						}
						if (curTime >= beep) {
							playClip();
							beep += 500L;
						}

						Thread.sleep(0, oneMSnano / 5);
					} catch (Exception ex) { }
				}
				playClip();
			}
			this.updateTimeLabel();
			this.startButton.setText("Start");
		})).start();
	}
}
