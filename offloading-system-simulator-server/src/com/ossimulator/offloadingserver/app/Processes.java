package com.ossimulator.offloadingserver.app;

import java.io.IOException;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import com.ossimulator.offloadingos.util.Matrix;
import com.ossimulator.offloadingos.util.Vector;
import com.ossimulator.offloadingserver.cryptography.AES;

public class Processes {
	public static String Example() {
		int sum = 0;
		for (int i = 0; i < 10; i++) {
			sum += i;
		}
		return String.valueOf(sum);
	}

	/**
	 * Matrix Add By itself for one time
	 * 
	 * @param data
	 * @return
	 */
	public static Matrix MatrixAddition(Object data) {
		Matrix m = (Matrix) data;
		m = Matrix.add(m, m);
		return m;
	}

	public static String AESEncryption(Object data) {
		try {
			AES aes = new AES("KEYTOENCRYPT");
			return aes.encode((String) data);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static double[] ImageRecognition(Object data) throws IOException {
		int SCENE_WIDTH = 180;
		int SCENE_HEIGHT = 240;

		Mat img_scene = new Mat(SCENE_HEIGHT, SCENE_WIDTH, CvType.CV_8UC1);
		img_scene.put(0, 0, (byte[]) data);

		Mat circle = new Mat();
		Imgproc.GaussianBlur(img_scene, img_scene, new Size(5, 5), 1, 1);
		Imgproc.HoughCircles(img_scene, circle, Imgproc.CV_HOUGH_GRADIENT, 1,
				50, 80, 10, 20, 0);

		circle.convertTo(circle, CvType.CV_64F);

		double[] bytes = new double[3];
		circle.get(0, 0, bytes);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static Vector M1(Object data) {
		Vector m = (Vector) data;
		for (int i = 0; i < 250; i++) {
			m = Vector.addition(m, m);
		}
		return m;
	}

	public static Vector M2(Object data) {
		Vector m = (Vector) data;
		for (int i = 0; i < 135; i++) {
			m = Vector.addition(m, m);
		}
		return m;
	}

	public static Matrix M3(Object data) {
		Matrix m = (Matrix) data;
		for (int n = 0; n < 10; n++) {
			m = Matrix.add(m, m);
		}
		return m;
	}

	public static Matrix M4(Object data) {
		Matrix m = (Matrix) data;
		for (int n = 0; n < 10; n++) {
			m = Matrix.add(m, m);
		}
		return m;
	}

	public static Matrix M5(Object data) {
		Matrix m = (Matrix) data;
		m = Matrix.multiply(m, m);
		return m;
	}

	public static Matrix M6(Object data) {
		Matrix m = (Matrix) data;
		m = Matrix.multiply(m, m);
		return m;
	}

	public static Matrix M7(Object data) {
		Matrix m = (Matrix) data;
		for (int n = 0; n < 10; n++) {
			m = Matrix.multiply(m, m);
		}
		return m;
	}

	public static Matrix M8(Object data) {
		Matrix m = (Matrix) data;
		for (int n = 0; n < 10; n++) {
			m = Matrix.multiply(m, m);
		}
		return m;
	}
}
