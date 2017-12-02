package de.paranoidspectre.heroes.queuewatcher.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Screenshotter {

	private static String screenshotPath = "C:\\Users\\Jendrik\\Documents\\Heroes of the Storm\\Screenshots";

	private static Robot robby;

	public static void init() throws AWTException {
		robby = new Robot();
	}

	public static BufferedImage getScreenshot() throws IOException, InterruptedException, AWTException {

		if (robby == null) {
			init();
		}

		robby.keyPress(KeyEvent.VK_PRINTSCREEN);
		robby.keyRelease(KeyEvent.VK_PRINTSCREEN);

		Thread.sleep(300);

		System.out.println(screenshotPath);

		File folder = new File(screenshotPath);
		File[] screenshots = folder.listFiles();
		Arrays.sort(screenshots);

		if (screenshots.length <= 0) {
			return new BufferedImage((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
					(int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(), BufferedImage.TYPE_INT_RGB);
		}

		BufferedImage img = ImageIO.read(screenshots[screenshots.length - 1]);

		screenshots[screenshots.length - 1].delete();

		return img;
	}

	public static void setScreenshotPath(String path) {
		screenshotPath = path;
	}

}
