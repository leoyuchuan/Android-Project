package com.ossimulator.offloadingos.util;

import java.io.Serializable;

public class Matrix implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[][] data;
	private int row;
	private int col;

	private Matrix() {
	}

	public Matrix(int row, int col, int[][] data) {
		this.row = row;
		this.col = col;
		this.data = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				this.data[i][j] = data[i][j];
			}
		}
	}

	public Matrix(String matrixData) {
		String[] rows = matrixData.split("\n");
		row = rows.length;
		col = rows[0].split(" ").length;
		data = new int[row][col];
		for (int i = 0; i < rows.length; i++) {
			String[] cols = rows[i].split(" ");
			for (int j = 0; j < cols.length; j++) {
				data[i][j] = Integer.parseInt(cols[j]);
			}
		}
	}

	public static Matrix getUnitMatrix(int n) {
		int[][] data = new int[n][n];
		for (int i = 0; i < n; i++) {
			data[i][i] = 1;
		}
		Matrix m = new Matrix();
		m.col = n;
		m.row = n;
		m.data = data;
		return m;
	}

	public void setValueAtIndex(int row, int col, int value) {
		data[row][col] = value;
	}

	public int getValueAtIndex(int row, int col) {
		return data[row][col];
	}

	public static Matrix multiply(Matrix A, Matrix B) {
		if (A.getCol() != B.getRow())
			return null;
		int row = A.getRow();
		int col = B.getCol();
		int elems = A.getCol();
		int[][] data = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				for (int k = 0; k < elems; k++) {
					data[i][j] = A.getValueAtIndex(i, k)
							* B.getValueAtIndex(k, j);
				}
			}
		}
		Matrix m = new Matrix();
		m.row = row;
		m.col = col;
		m.data = data;
		return null;
	}

	public static Matrix add(Matrix A, Matrix B) {
		if (A.getCol() != B.getCol() || A.getRow() != B.getCol())
			return null;
		int row = A.getRow();
		int col = A.getCol();
		int[][] data = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				data[i][j] = A.getValueAtIndex(i, j) + B.getValueAtIndex(i, j);
			}
		}
		Matrix m = new Matrix();
		m.row = row;
		m.col = col;
		m.data = data;
		return m;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}