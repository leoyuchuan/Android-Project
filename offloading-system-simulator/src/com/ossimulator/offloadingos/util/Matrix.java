package com.ossimulator.offloadingos.util;

public class Matrix {
	private double[][] data;
	private int row;
	private int col;

	private Matrix() {

	}

	public Matrix(int row, int col, double[][] data) {
		this.row = row;
		this.col = col;
		this.data = new double[row][col];
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
		data = new double[row][col];
		for (int i = 0; i < rows.length; i++) {
			String[] cols = rows[i].split(" ");
			for (int j = 0; j < cols.length; j++) {
				data[i][j] = Double.parseDouble(cols[j]);
			}
		}
	}

	public static Matrix getRandomMatrix(int row, int col, double min,
			double max) {
		Matrix tmp;
		double[][] data = new double[row][col];

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				data[i][j] = min + Math.random() * (max - min);
			}
		}

		tmp = new Matrix(row, col, data);
		return tmp;
	}

	public static Matrix getRandomSquareMatrix(int n, double min, double max) {
		Matrix tmp;
		double[][] data = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				data[i][j] = min + Math.random() * (max - min);
			}
		}

		tmp = new Matrix(n, n, data);
		return tmp;
	}

	public void setValueAtIndex(int row, int col, double value) {
		data[row][col] = value;
	}

	public double getValueAtIndex(int row, int col) {
		return data[row][col];
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				sb.append(data[i][j] + " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	public String toString(int precision){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				sb.append(String.format("%1$.2f", data[i][j]) + " ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public static Matrix toMatrix(String matrixData) {
		Matrix tmp;
		int row, col;
		String[] rows = matrixData.split("\n");
		row = rows.length;
		col = rows[0].split(" ").length;
		double[][] data = new double[row][col];

		for (int i = 0; i < rows.length; i++) {
			String[] cols = rows[i].split(" ");
			for (int j = 0; j < cols.length; j++) {
				data[i][j] = Double.parseDouble(cols[j]);
			}
		}

		tmp = new Matrix(row, col, data);
		return tmp;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}