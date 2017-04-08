package com.ossimulator.offloadingserver.cryptography;

public class AES {
	Rijndael r;
	private int keyL;

	public AES(String key) throws Exception {
		r = new Rijndael();
		if (key.length() < 16) {
			while (key.length() < 16)
				key = "0" + key;
		} else if (key.length() > 16 && key.length() < 24) {
			while (key.length() < 24)
				key = "0" + key;
		} else if (key.length() > 24 && key.length() < 32) {
			while (key.length() < 32)
				key = "0" + key;
		} else if (key.length() > 32) {
			key = key.substring(0, 32);
		}
		keyL = key.length();
		r.makeKey(key.getBytes("UTF8"), keyL * 8);
	}

	public String encode(String text) throws Exception {
		StringBuilder temp = new StringBuilder();
		int count = text.length() / 16;
		if (text.length() != count * 16) {
			int num = (count + 1) * 16;
			while (text.length() < num)
				text = text + " ";
		}
		count = text.length() / 16;
		for (int i = 0; i < count; i++) {
			byte[] ciphertxt = new byte[16];
			String tempStr = text.substring(i * 16, (i + 1) * 16);
			r.encrypt(strToBytes(tempStr), ciphertxt);
			temp.append(bytesToStr(ciphertxt));
		}
		return temp.toString();
	}

	public String decode(String text) throws Exception {
		StringBuilder temp = new StringBuilder();
		int count = text.length() / 16;
		for (int i = 0; i < count; i++) {
			byte[] plaintxt = new byte[16];
			String tempStr = text.substring(i * 16, (i + 1) * 16);
			r.decrypt(strToBytes(tempStr), plaintxt);
			temp.append(bytesToStr(plaintxt));
		}
		return temp.toString().trim();
	}

	private byte[] strToBytes(String str) {
		byte[] by = new byte[str.length()];
		for (int i = 0; i < str.length(); i++) {
			by[i] = (byte) str.charAt(i);
		}
		return by;
	}

	private String bytesToStr(byte[] by) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < by.length; i++) {
			sb.append((char) (by[i] & 0xFF));
		}
		return sb.toString();
	}
}
