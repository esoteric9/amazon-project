/**
 * File Name: AutoBasics.java<br>
 * Munteanu, Alex<br>
 * Java Boot Camp Exercise<br>
 * Instructor: Jean-francois Nepton<br>
 * Created: Feb 4, 2017
 */
package com.amunteanu.helpers;

import java.io.*;
import java.util.*;
import java.util.NoSuchElementException;

import org.apache.commons.io.*;
import org.openqa.selenium.*;

/**
 * AutoBasics //ADDD (description of class)
 * <p>
 * //ADDD (description of core fields)
 * <p>
 * //ADDD (description of core methods)
 *
 * @author Munteanu, Alex
 * @version 1.0.0
 * @since 1.0
 */
public class AutoBasics {

	private static final String DEFAULT_CONFIG_FILE_NAME = "config.properties";

	public static boolean takeScreenshot(WebDriver driver, String fileName) {
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(srcFile, new File("screenshots/" + fileName + ".png"));
		} catch (IOException e) {
			System.out.println("Screenshot was not saved correctly: " + fileName + ".png");
			return false;
		}
		return true;
	}

	public static boolean isElementPresent(WebDriver driver, By by) {
		try {
			driver.findElement(by);
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}

	public static Properties readProps(String fileName) {
		Properties prop = new Properties();
		try {
			InputStream input = new FileInputStream("src/main/resources/" + fileName);
			prop.load(input);
		} catch (FileNotFoundException e) {
			System.err.println("File was not found.");
		} catch (IOException e) {
			System.err.println("Error when reading file.");
		}
		return prop;
	}

	public static boolean writeProps(Properties prop, String fileName) {
		boolean success;
		try {
			OutputStream output = new FileOutputStream("src/main/resources/" + fileName);
			prop.store(output, "Properties Added");
			success = true;
		} catch (FileNotFoundException e) {
			System.err.println("File was not found.");
			return false;
		} catch (IOException e) {
			System.err.println("Error when writting to file.");
			return false;
		}
		return success;
	}

	public static String getProp(String fileName, String key) throws IOException {
		Properties prop = new Properties();
		InputStream input = new FileInputStream("src/main/resources/" + fileName);
		prop.load(input);
		return prop.getProperty(key);
	}

	public static String getProp(String key) throws IOException {
		return getProp(DEFAULT_CONFIG_FILE_NAME, key);
	}

	public static int getInt(String fileName, String key) throws IOException {
		String input = getProp(fileName, key);
		int result = 0;
		try {
			result = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.err.println("Error when converting to int");
		}
		return result;
	}

	public static int getInt(String key) throws IOException {
		return getInt(DEFAULT_CONFIG_FILE_NAME, key);
	}

	public static void addProp(String key, String value, String fileName) {
		Properties prop = readProps(fileName);
		prop.setProperty(key, value);
		writeProps(prop, fileName);
	}

	public static void addProp(String key, String value) {
		addProp(key, value, DEFAULT_CONFIG_FILE_NAME);
	}
}
