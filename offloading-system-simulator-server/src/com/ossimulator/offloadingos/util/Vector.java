package com.ossimulator.offloadingos.util;

import java.io.Serializable;

public class Vector implements Serializable {
	private static final long serialVersionUID = 1L;
	public int[] data;

	private Vector() {

	}

	public static Vector getDefaultVector(int n) {
		Vector vec = new Vector();
		int[] data = new int[n];
		for (int i = 0; i < n; i++) {
			data[i] = 1;
		}
		vec.data = data;
		return vec;
	}

	public static Vector getVector(int[] data) {
		Vector vec = new Vector();
		for (int i = 0; i < data.length; i++) {
			data[i] = 1;
		}
		vec.data = data;
		return vec;
	}

	public int getSize() {
		return data.length;
	}

	public static int dotProduct(Vector a, Vector b) {
		int sum = 0;
		if (a.getSize() != b.getSize())
			return -1;
		int size = a.getSize();
		for (int i = 0; i < size; i++) {
			sum += a.data[i] * b.data[i];
		}
		return sum;
	}

	public static Vector addition(Vector a, Vector b) {
		if (a.getSize() != b.getSize())
			return null;
		int size = a.getSize();
		Vector vec = new Vector();
		int[] data = new int[size];

		for (int i = 0; i < size; i++) {
			data[i] = a.data[i] + b.data[i];
		}
		vec.data = data;
		return vec;
	}
}
