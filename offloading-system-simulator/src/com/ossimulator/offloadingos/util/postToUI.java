package com.ossimulator.offloadingos.util;

import android.widget.EditText;

public class postToUI implements Runnable {

	EditText log;
	String text;

	public postToUI(EditText log, String text) {
		this.log = log;
		this.text = text;
	}

	@Override
	public void run() {
		log.append(text);
	}

}