package com.ossimulator.offloadingos.util;

/**
 * A Data that contains a String
 * 
 * @author leoyuchuan
 *
 */
public class Data {
	String data;

	/**
	 * Initialize an empty data.
	 */
	private Data() {
		data = "";
	}

	/**
	 * Initialize a specific data
	 * 
	 * @param data
	 */
	public Data(String data) {
		this.data = data;
	}

	/**
	 * Set data
	 * 
	 * @param data
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Get data
	 * 
	 * @return
	 */
	public String getData() {
		return data;
	}

	/**
	 * Get Data Obj with specific String
	 * 
	 * @param data
	 * @return
	 */
	public static Data getData(String data) {
		return new Data(data);
	}

	/**
	 * Get Data Obj with empty String
	 * 
	 * @return
	 */
	public static Data getEmptyData() {
		return new Data();
	}

	/**
	 * Return the containing String
	 */
	public String toString() {
		return data;
	}
}
