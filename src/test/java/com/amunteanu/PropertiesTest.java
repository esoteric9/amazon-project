package com.amunteanu;

import java.io.*;
import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import com.amunteanu.helpers.*;

public class PropertiesTest {

	public void testReadingProperties() throws IOException {
		Properties prop = AutoBasics.readProps("config.properties");
		System.out.println("Server name: " + prop.getProperty("server-name"));
		System.out.println("Ip address: " + prop.getProperty("ip-address"));
		System.out.println("Port: " + prop.getProperty("port"));
		System.out.println("Department name: " + prop.getProperty("department-name"));
		Assert.assertNotNull(prop);
	}

	public void testWritingProperties() {
		Properties prop = new Properties();
		prop.setProperty("username", "amunteanu");
		prop.setProperty("password", "test123");
		prop.setProperty("server-name", "am-server");
		prop.setProperty("ip-address", "104.135.101.120");
		prop.setProperty("port", "8889");
		prop.setProperty("department-name", "sales");
		AutoBasics.writeProps(prop, "test-props");
		Assert.assertNotNull(AutoBasics.readProps("test-props"));
	}

	public void testGettingPropertie() throws IOException {
		String prop = AutoBasics.getProp("config.properties", "ip-address");
		Assert.assertEquals(prop, "104.135.101.120");
	}

	public void testGettingPropertieDefaultFile() throws IOException {
		String prop = AutoBasics.getProp("ip-address");
		Assert.assertEquals(prop, "104.135.101.120");
	}

	public void testGettingInt() throws IOException {
		int prop = AutoBasics.getInt("config.properties", "port");
		Assert.assertEquals(Integer.toString(prop), "8889");
	}

	public void testGettingIntDefaultFile() throws IOException {
		int prop = AutoBasics.getInt("port");
		Assert.assertEquals(Integer.toString(prop), "8889");
	}

	@Test(dataProvider = "configData")
	public void addProperties(String key, String value, String fileName) throws IOException {
		AutoBasics.addProp(key, value, fileName);
	}

	@DataProvider
	public Object[][] configData() {
		return new Object[][] { { "search", "twotabsearchtextbox", "locators" },
				{ "search_btn", ".nav-search-submit input", "locators" }, { "top_result", "#result_0 h2", "locators" },
				{ "add_to_cart_btn", "add-to-cart-button", "locators" },
				{ "total_price", ".//*[@id='hlb-subcart']/div[1]/span/span[2]", "locators" } };
	}
}
