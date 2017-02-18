package com.amunteanu.helpers;

import java.io.*;
import java.util.concurrent.*;

import org.apache.log4j.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.ie.*;
import org.testng.annotations.*;

public class BasicTest extends Core {

	private String baseURL;

	private WebDriver driver;

	private Logger log;

	public BasicTest(String baseURL) {
		super();
		this.baseURL = baseURL;
		this.log = Logger.getLogger(BasicTest.class);
		this.log.info("created BasicTest object through constructor");
	}

	public String getBaseURL() {
		return this.baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public WebDriver getDriver() {
		return this.driver;
	}

	public Logger getLog() {
		return this.log;
	}

	@BeforeMethod(groups = "firefox")
	public void setupFirefox() {
		this.driver = new FirefoxDriver();
		this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		this.driver.get(this.baseURL);
	}

	@BeforeMethod(groups = "chrome")
	public void setupChrome() {
		System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
		this.driver = new ChromeDriver();
		this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		this.driver.get(this.baseURL);
	}

	@BeforeMethod(groups = "ie")
	public void setupIE() {
		this.driver = new InternetExplorerDriver();
		this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		this.driver.get(this.baseURL);
	}

	@AfterMethod
	public void tearDown() {
		this.driver.quit();
	}

	@Override
	public String getProp(String name) throws IOException {
		return AutoBasics.getProp(name);
	}

	@Override
	public int getInt(String name) throws IOException {
		return AutoBasics.getInt(name);
	}
}
