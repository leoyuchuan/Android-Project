package com.ossimulator.offloadingserver.util;

import java.io.Serializable;

/**
 * A Data that contains a String
 * 
 * @author leoyuchuan
 *
 */
public class Data implements Serializable {
	Object data;

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
	public Data(Object data) {
		this.data = data;
	}

	/**
	 * Set data
	 * 
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * Get data
	 * 
	 * @return
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Get Data Obj with specific String
	 * 
	 * @param data
	 * @return
	 */
	public static Data getData(Object data) {
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
}