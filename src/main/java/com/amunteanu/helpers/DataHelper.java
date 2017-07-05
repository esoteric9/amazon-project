/**
 * File Name: DataHelper.java<br>
 * Munteanu, Alex<br>
 * Java Boot Camp Exercise<br>
 * Instructor: Jean-francois Nepton<br>
 * Created: Jan 30, 2017
 */
package com.amunteanu.helpers;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;

import org.apache.log4j.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import com.amunteanu.helpers.exceptions.BooleanFormatException;
import com.amunteanu.helpers.exceptions.CharacterCountFormatException;
import com.amunteanu.helpers.exceptions.DataTypesCountException;
import com.amunteanu.helpers.exceptions.DataTypesMismatchException;
import com.amunteanu.helpers.exceptions.DataTypesTypeException;
import com.amunteanu.helpers.exceptions.InvalidExcelExtensionException;

/**
 * DataHelper //ADDD (description of class)
 * <p>
 * //ADDD (description of core fields)
 * <p>
 * //ADDD (description of core methods)
 *
 * @author Munteanu, Alex
 * @version 1.0.0
 * @since 1.0
 */
public class DataHelper {

	private Object[][] data;

	public DataHelper(Object[][] data) {
		super();
		this.data = data;
	}

	private static Logger logger = Logger.getLogger(DataHelper.class);

	public static Logger getLogger() {
		return logger;
	}

	// Static method
	public static String displayData(Object[][] data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				sb.append(data[i][j] + " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	// Instance method
	public String displayData() {
		return displayData(this.data);
	}

	/**
	 * Method to read a database table and get data from it
	 *
	 * @param driverClassString
	 * @param databaseStringUrl
	 * @param username
	 * @param password
	 * @param tableName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws DataTypesMismatchException
	 * @throws DataTypesCountException
	 * @throws DataTypesTypeException
	 */
	public static Object[][] evalDatabaseTable(String driverClassString, String databaseStringUrl, String username,
			String password, String tableName) throws ClassNotFoundException, SQLException, DataTypesMismatchException,
			DataTypesCountException, DataTypesTypeException {
		// Method calls overloaded method which sets no offset for col or row in
		// the case you wanted to offset your data retrieved based on a column
		// or row offset
		return evalDatabaseTable(driverClassString, databaseStringUrl, username, password, tableName, 0, 0, null);
	}

	/**
	 * Method to read database table with implementation for a row and column
	 * offset.
	 *
	 * @param driverClassString
	 * @param databaseStringUrl
	 * @param username
	 * @param password
	 * @param tableName
	 * @param rowOffset
	 * @param colOffset
	 * @param dataTypes
	 * @return
	 * @throws DataTypesMismatchException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws DataTypesCountException
	 * @throws DataTypesTypeException
	 */
	public static Object[][] evalDatabaseTable(String driverClassString, String databaseStringUrl, String username,
			String password, String tableName, int rowOffset, int colOffset, DataType[] dataTypes)
			throws DataTypesMismatchException, ClassNotFoundException, SQLException, DataTypesCountException,
			DataTypesTypeException {
		// 2D Array of Objects to hold data object to be returned
		Object[][] myData;
		ArrayList<Object> myArrayData = new ArrayList<Object>();
		Class.forName(driverClassString);
		Connection dbconn = DriverManager.getConnection(databaseStringUrl, username, password);
		Statement stmt = dbconn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from " + tableName);
		int numOfColumns = rs.getMetaData().getColumnCount();
		if (dataTypes != null) {
			if (dataTypes.length != numOfColumns) {
				throw new DataTypesCountException();
			}
		}
		int curRow = 1;
		while (rs.next()) {
			if (curRow > rowOffset) {
				Object[] rowData = new Object[numOfColumns - colOffset];
				for (int i = 0, j = colOffset; i < rowData.length; i++) {
					try {
						switch (dataTypes[i]) {
						case STRING:
							rowData[i] = rs.getString(i + colOffset + 1);
							break;
						case INT:
							rowData[i] = rs.getInt(i + colOffset + 1);
							break;
						case DOUBLE:
							rowData[i] = rs.getDouble(i + colOffset + 1);
							break;
						default:
							break;
						}
					} catch (Exception e) {
						System.out.println("Error in conversion...");
						e.printStackTrace();
						throw new DataTypesTypeException();
					}
				}
				myArrayData.add(rowData);
			}
			curRow++;
		}
		myData = new Object[myArrayData.size()][];
		for (int i = 0; i < myData.length; i++) {
			myData[i] = (Object[]) myArrayData.get(i);
		}
		// Step 5
		rs.close();
		stmt.close();
		dbconn.close();
		return myData;
	}

	/**
	 * Method to read an excel file in both the old format of excel and the
	 * newer one.
	 *
	 * @param fileLocation
	 * @param fileName
	 * @param hasLabels
	 * @return
	 * @throws InvalidExcelExtensionException
	 */
	public static Object[][] getExcelFileData(String fileLocation, String fileName, Boolean hasLabels)
			throws InvalidExcelExtensionException {
		Object[][] resultsObject;
		String[] fileNameParts = fileName.split("[.]");
		String extension = fileNameParts[fileNameParts.length - 1];
		ArrayList<Object> results = null;
		if (extension.equalsIgnoreCase("xlsx")) {
			results = getNewExcelFileResults(fileLocation, fileName, hasLabels);
		} else if (extension.equalsIgnoreCase("xls")) {
			results = getOldExcelFileResults(fileLocation, fileName, hasLabels);
		} else {
			throw new InvalidExcelExtensionException();
		}
		resultsObject = new Object[results.size()][];
		results.toArray(resultsObject);
		return resultsObject;
	}

	/**
	 * Private method to get data from a new type of Excel file.
	 *
	 * @param fileLocation
	 * @param fileName
	 * @param hasLabels
	 * @return
	 * @throws IOException
	 */
	private static ArrayList<Object> getNewExcelFileResults(String fileLocation, String fileName, Boolean hasLabels) {
		ArrayList<Object> results = new ArrayList<Object>();
		try {
			String fullFilePath = fileLocation + fileName;
			InputStream newExcelFormatFile = new FileInputStream(new File(fullFilePath));
			XSSFWorkbook workbook = new XSSFWorkbook(newExcelFormatFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			if (hasLabels) {
				rowIterator.next();
			}
			while (rowIterator.hasNext()) {
				ArrayList<Object> rowData = new ArrayList<Object>();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						System.out.print(cell.getBooleanCellValue() + "\t\t\t");
						rowData.add(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print(cell.getNumericCellValue() + "\t\t\t");
						rowData.add(cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "\t\t\t");
						rowData.add(cell.getStringCellValue());
						break;
					}
				}
				Object[] rowDataObject = new Object[rowData.size()];
				rowData.toArray(rowDataObject);
				results.add(rowDataObject);
				System.out.println("");
			}
			newExcelFormatFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Private method to get data from an old type of Excel file.
	 *
	 * @param fileLocation
	 * @param fileName
	 * @param hasLabels
	 * @return
	 */
	private static ArrayList<Object> getOldExcelFileResults(String fileLocation, String fileName, Boolean hasLabels) {
		ArrayList<Object> results = new ArrayList<Object>();
		try {
			String fullFilePath = fileLocation + fileName;
			InputStream newExcelFormatFile = new FileInputStream(new File(fullFilePath));
			HSSFWorkbook workbook = new HSSFWorkbook(newExcelFormatFile);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			if (hasLabels) {
				rowIterator.next();
			}
			while (rowIterator.hasNext()) {
				ArrayList<Object> rowData = new ArrayList<Object>();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						System.out.print(cell.getBooleanCellValue() + "\t\t\t");
						rowData.add(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print(cell.getNumericCellValue() + "\t\t\t");
						rowData.add(cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "\t\t\t");
						rowData.add(cell.getStringCellValue());
						break;
					}
				}
				Object[] rowDataObject = new Object[rowData.size()];
				rowData.toArray(rowDataObject);
				results.add(rowDataObject);
				System.out.println("");
			}
			newExcelFormatFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Method to read text file in various format styles and also allows
	 * DataTypes to be specified
	 *
	 * @param fileLocation
	 * @param fileName
	 * @param textFormat
	 * @param hasLabels
	 * @param dataTypes
	 * @return
	 */
	public static Object[][] getTextFileData(String fileLocation, String fileName, TextFormat textFormat,
			Boolean hasLabels, DataType[] dataTypes) {
		Object[][] data;
		ArrayList<String> lines = openFileAndCollectData(fileLocation, fileName);
		switch (textFormat) {
		case CSV:
			data = parseCSVData(lines, hasLabels, dataTypes);
			break;
		case XML:
			data = parseXMLData(lines, hasLabels);
			break;
		case TAB:
			data = parseTabData(lines, hasLabels);
			break;
		case JSON:
			data = parseJSONData(lines, hasLabels);
			break;
		default:
			data = null;
			break;
		}
		return data;
	}

	/**
	 * Overloaded method to read text file formatted in CSV style.
	 *
	 * @param fileName
	 * @return
	 */
	public static Object[][] getTextFileData(String fileName) {
		return getTextFileData("src/main/resources/", fileName, TextFormat.CSV, false, null);
	}

	/**
	 * Overloaded method to read text file in various format styles.
	 *
	 * @param fileLocation
	 * @param fileName
	 * @param textFormat
	 * @return
	 */
	public static Object[][] getTextFileData(String fileLocation, String fileName, TextFormat textFormat) {
		return getTextFileData(fileLocation, fileName, textFormat, false, null);
	}

	/**
	 * Overloaded method to read text file in various format styles and also
	 * allows DataTypes to be specified and also setting no labels.
	 *
	 * @param fileLocation
	 * @param fileName
	 * @param textFormat
	 * @param dataTypes
	 * @return
	 */
	public static Object[][] getTextFileData(String fileLocation, String fileName, TextFormat textFormat,
			DataType[] dataTypes) {
		return getTextFileData(fileLocation, fileName, textFormat, false, dataTypes);
	}

	/**
	 * Private method to convert data based on supplied DataType.
	 *
	 * @param parameter
	 * @param dataType
	 * @return
	 * @throws BooleanFormatException
	 * @throws CharacterCountFormatException
	 */
	private static Object convertDataType(String parameter, DataType dataType)
			throws BooleanFormatException, CharacterCountFormatException {
		Object data = null;
		try {
			switch (dataType) {
			case STRING:
				data = parameter;
				break;
			case CHAR:
				if (parameter.length() > 1) {
					throw new CharacterCountFormatException();
				}
				data = parameter.charAt(0);
				break;
			case DOUBLE:
				data = Double.parseDouble(parameter);
				break;
			case FLOAT:
				data = Float.parseFloat(parameter);
				break;
			case INT:
				data = Integer.parseInt(parameter);
				break;
			case BOOLEAN:
				if (parameter.equalsIgnoreCase("true") | parameter.equalsIgnoreCase("false")) {
					data = Boolean.parseBoolean(parameter);
				} else {
					throw new BooleanFormatException();
				}
				break;
			default:
				break;
			}
		} catch (NumberFormatException | BooleanFormatException | CharacterCountFormatException e) {
			System.out.println("Converting data.. to... " + dataType + "(" + parameter + ")");
		}
		return data;
	}

	/**
	 * Private method to open a text file and collect data lines as an ArrayList
	 * collection of lines.
	 *
	 * @param fileLocation
	 * @param fileName
	 * @return
	 */
	private static ArrayList<String> openFileAndCollectData(String fileLocation, String fileName) {
		String fullFilePath = fileLocation + fileName;
		ArrayList<String> dataLines = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(fullFilePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			while (line != null) {
				dataLines.add(line);
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fullFilePath + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fullFilePath + "'");
		}
		return dataLines;
	}

	/**
	 * Private method to get parse data formatted in CSV style.
	 *
	 * @param lines
	 * @param hasLabels
	 * @param dataTypes
	 * @return
	 */
	private static Object[][] parseCSVData(ArrayList<String> lines, boolean hasLabels, DataType[] dataTypes) {
		ArrayList<Object> results = new ArrayList<Object>();
		if (hasLabels) {
			lines.remove(0);
		}
		String pattern = "(,*)([.a-zA-Z0-9\\s-]+)(,*)";
		Pattern r = Pattern.compile(pattern);
		for (int i = 0; i < lines.size(); i++) {
			int curDataType = 0;
			ArrayList<Object> curMatches = new ArrayList<Object>();
			Matcher m = r.matcher(lines.get(i));
			while (m.find()) {
				if (dataTypes.length > 0) {
					try {
						curMatches.add(convertDataType(m.group(2).trim(), dataTypes[curDataType]));
					} catch (Exception e) {
						// System.out.println("DataTypes provided do not match
						// parsed data results.");
					}
				} else {
					curMatches.add(m.group(2).trim());
				}
				curDataType++;
			}
			Object[] resultsObj = new Object[curMatches.size()];
			curMatches.toArray(resultsObj);
			results.add(resultsObj);
		}
		Object[][] resultsObj = new Object[results.size()][];
		results.toArray(resultsObj);
		return resultsObj;
	}

	/**
	 * Private method to get parse data formatted in JSON style.
	 *
	 * @param lines
	 * @param hasLabels
	 * @return
	 */
	private static Object[][] parseJSONData(ArrayList<String> lines, Boolean hasLabels) {
		// TODO Create an implementation to handle JSON formatted documents
		return null;
	}

	/**
	 * Private method to get parse data formatted in Tab style.
	 *
	 * @param lines
	 * @param hasLabels
	 * @return
	 */
	private static Object[][] parseTabData(ArrayList<String> lines, Boolean hasLabels) {
		// TODO Create an implementation to handle Tab formatted documents
		return null;
	}

	/**
	 * Private method to get parse data formatted in XML style.
	 *
	 * @param lines
	 * @param hasLabels
	 * @return
	 */
	private static Object[][] parseXMLData(ArrayList<String> lines, Boolean hasLabels) {
		// TODO Create an implementation to handle XML formatted documents
		return null;
	}
}
