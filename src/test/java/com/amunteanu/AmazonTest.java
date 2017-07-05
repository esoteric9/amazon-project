package com.amunteanu;

import java.sql.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.*;
import org.testng.annotations.*;

import com.amunteanu.helpers.*;
import com.amunteanu.helpers.exceptions.DataTypesCountException;
import com.amunteanu.helpers.exceptions.DataTypesMismatchException;
import com.amunteanu.helpers.exceptions.DataTypesTypeException;
import com.amunteanu.helpers.exceptions.InvalidExcelExtensionException;

public class AmazonTest extends BasicTest {

	/**
	 * @param baseURL
	 */
	public AmazonTest() {
		super("https://www.amazon.com");
	}

	@Test(dataProvider = "csvData")
	public void testAmazon(double id, String product, double quantity, double total_price) {
		super.getDriver().findElement(By.id("twotabsearchtextbox")).clear();
		super.getDriver().findElement(By.id("twotabsearchtextbox")).sendKeys(product);
		super.getDriver().findElement(By.cssSelector(".nav-search-submit input")).click();
		super.getDriver().findElement(By.cssSelector("#result_0 h2")).click();
		Select select = new Select(this.getDriver().findElement(By.id("quantity")));
		select.selectByValue(Integer.toString(((int) quantity)));
		super.getDriver().findElement(By.id("add-to-cart-button")).click();
		Assert.assertEquals(
				this.getDriver().findElement(By.xpath(".//*[@id='hlb-subcart']/div[1]/span/span[2]")).getText(),
				"$" + total_price);
	}

	@DataProvider
	public Object[][] dbData() throws ClassNotFoundException, SQLException, DataTypesMismatchException,
			DataTypesCountException, DataTypesTypeException {
		return DataHelper.evalDatabaseTable("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:8889/amazondb", "root",
				"root", "products", 0, 0,
				new DataType[] { DataType.DOUBLE, DataType.STRING, DataType.DOUBLE, DataType.DOUBLE });
	}

	@DataProvider
	public Object[][] excel2003() throws InvalidExcelExtensionException {
		return DataHelper.getExcelFileData("src/main/resources/", "products.xls", false);
	}

	@DataProvider
	public Object[][] excelNew() throws InvalidExcelExtensionException {
		return DataHelper.getExcelFileData("src/main/resources/", "products.xlsx", false);
	}

	@DataProvider
	public Object[][] csvData() throws InvalidExcelExtensionException {
		Object[][] data = DataHelper.getTextFileData("src/main/resources/", "products.csv", TextFormat.CSV,
				new DataType[] { DataType.DOUBLE, DataType.STRING, DataType.DOUBLE, DataType.DOUBLE });
		return data;
	}
}
