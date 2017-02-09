package com.amunteanu;

import java.sql.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.*;
import org.testng.annotations.*;

import com.amunteanu.helpers.*;

public class AmazonTest extends BasicTest {

	/**
	 * @param baseURL
	 */
	public AmazonTest() {
		super("https://www.amazon.com");
	}

	@Test(dataProvider = "dp")
	public void f(String id, String product, String quantity, String total_price) {
		super.getDriver().findElement(By.id("twotabsearchtextbox")).clear();
		super.getDriver().findElement(By.id("twotabsearchtextbox")).sendKeys(product);
		super.getDriver().findElement(By.cssSelector(".nav-search-submit input")).click();
		super.getDriver().findElement(By.cssSelector("#result_0 h2")).click();
		Select select = new Select(this.getDriver().findElement(By.id("quantity")));
		select.selectByValue(quantity);
		super.getDriver().findElement(By.id("add-to-cart-button")).click();
		Assert.assertEquals(
				this.getDriver().findElement(By.xpath(".//*[@id='hlb-subcart']/div[1]/span/span[2]")).getText(),
				"$" + total_price);
	}

	@DataProvider
	public Object[][] dp() throws ClassNotFoundException, SQLException {
		return DataHelper.evalDatabaseTable("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:8889/amazondb", "root",
				"root", "products");
	}
}
