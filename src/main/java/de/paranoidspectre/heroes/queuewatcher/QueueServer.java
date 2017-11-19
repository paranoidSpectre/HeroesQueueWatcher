package de.paranoidspectre.heroes.queuewatcher;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import de.paranoidspectre.heroes.queuewatcher.util.Screenshotter;
import info.clearthought.layout.TableLayout;

public class QueueServer extends JFrame {

	public static void main(String[] args) {
		try {
			new QueueServer();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private BufferedImage refimg;
	private Robot robot;
	private GraphicsConfiguration config;

	private JTextField pathField;

	private double[][] sizes = { { 0.3, TableLayout.FILL }, { 0.5, 0.5 } };

	// More information: sparkjava.com
	public QueueServer() throws IOException, AWTException {

		this.setSize(450, 90);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setLayout(new TableLayout(sizes));

		this.add(createLabel("Path to Screenshots"), "0,0");
		this.add(pathField = createTextfield(), "1,0");
		this.add(createLabel("connect to:"), "0,1");
		this.add(createLabel(InetAddress.getLocalHost().getHostAddress() + ":8080"), "1,1");

		this.setVisible(true);

		refimg = ImageIO.read(this.getClass().getResourceAsStream("/img/refimg.png"));

		port(8080);
		staticFileLocation("/files");
		get("/queuestate", (req, res) -> getQueueState());
	}

	private boolean getQueueState() {
		try {
			Screenshotter.setScreenshotPath(getPath());
			double avgDiff = calcAvgDifference(Screenshotter.getScreenshot());
			System.out.println(avgDiff);
			if (avgDiff < 0.15) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			GregorianCalendar c = new GregorianCalendar();
			File log = new File(System.getProperty("user.dir") + "/error_log_" + c.get(GregorianCalendar.YEAR) + "-"
					+ (c.get(GregorianCalendar.MONTH) + 1) + "-" + c.get(GregorianCalendar.DAY_OF_MONTH) + "-"
					+ c.get(GregorianCalendar.HOUR_OF_DAY) + "-" + (c.get(GregorianCalendar.MINUTE) + 1) + "-"
					+ c.get(GregorianCalendar.SECOND) + ".log");
			log.getParentFile().mkdirs();
			try {
				log.createNewFile();
				PrintWriter pw = new PrintWriter(log);
				pw.write("something went wrong:\r\n");
				e.printStackTrace(pw);
				pw.flush();
				pw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.exit(1);
			}

			JOptionPane.showMessageDialog(this,
					"something went wrong, please send the log and a\r\ndescription what you have done to paranoidesport@gmail.com");
			System.exit(1);
			return false;
		}
	}

	private double calcAvgDifference(BufferedImage imageToCheck) throws IOException {

		int amountOfPixels = refimg.getWidth() * refimg.getHeight();

		double totalDiff = 0;

		for (int i = 0; i < refimg.getWidth(); i++) {
			for (int j = 0; j < refimg.getHeight(); j++) {
				totalDiff += calcSingleDiff(refimg.getRGB(i, j), imageToCheck.getRGB(i, j));
			}
		}

		return totalDiff / amountOfPixels;
	}

	private double calcSingleDiff(int rgbSource, int rgbTarget) {
		double diff = 0;

		double[] rgbSourceArray = splitRGB(rgbSource);
		double[] rgbTargetArray = splitRGB(rgbTarget);

		for (int i = 0; i < 3; i++) {
			diff += Math.abs(rgbSourceArray[i] - rgbTargetArray[i]) / 255;
		}

		return diff;// / 3;
	}

	private static double[] splitRGB(int rgb) {

		double r = (rgb >>> 16) & 0xFF;
		double g = (rgb >>> 8) & 0xFF;
		double b = rgb & 0xFF;

		return new double[] { r, g, b };
	}

	private JLabel createLabel(String caption) {
		return new JLabel(caption);
	}

	private JTextField createTextfield() {
		return new JTextField();
	}

	private String getPath() {
		return pathField.getText();
	}
}
