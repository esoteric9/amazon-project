/**
 * File Name: Core.java<br>
 * Munteanu, Alex<br>
 * Java Boot Camp Exercise<br>
 * Instructor: Jean-francois Nepton<br>
 * Created: Feb 11, 2017
 */
package com.amunteanu.helpers;

import java.io.*;

/**
 * Core //ADDD (description of class)
 * <p>
 * //ADDD (description of core fields)
 * <p>
 * //ADDD (description of core methods)
 *
 * @author Munteanu, Alex
 * @version 1.0.0
 * @since 1.0
 */
public class Core {

	public String getProp(String name) throws IOException {
		return AutoBasics.getProp(name);
	}

	public int getInt(String name) throws IOException {
		return AutoBasics.getInt(name);
	}

	public void addProp(String key, String value) throws IOException {
		AutoBasics.addProp(key, value);
	}

	public void addProp(String key, String value, String fileName) throws IOException {
		AutoBasics.addProp(key, value, fileName);
	}
}
