package com.ossimulator.offloadingserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.opencv.core.Core;

import com.ossimulator.offloadingserver.app.Processes;
import com.ossimulator.offloadingserver.task.Task;
import com.ossimulator.offloadingserver.util.Data;

public class Server {

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		ArrayList<Task> taskList = new ArrayList<Task>();
		int port = 8000;

		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				System.out.println("Waiting for connection...");
				Socket client = server.accept();
				System.out.println(client.getInetAddress() + " Connected...\n");
				RequestHandler rh = new RequestHandler(client, taskList);
				new Thread(rh).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class RequestHandler implements Runnable {
	private Socket client;
	ArrayList<Task> taskList;

	public RequestHandler(Socket s, ArrayList<Task> taskList) {
		this.client = s;
		this.taskList = taskList;

	}

	public void run() {
		try {
			// StringBuilder sb = new StringBuilder();
			// BufferedReader br = new BufferedReader(new InputStreamReader(
			// client.getInputStream()));
			// Charset.forName("UTF-8")
			GZIPInputStream gis = new GZIPInputStream(client.getInputStream());
			ObjectInputStream ois = new ObjectInputStream(
					gis);
			// String input;
			// while ((input = br.readLine()) != null) {
			// sb.append(input + "\n");
			// }
			String operation = ois.readUTF();

			System.out.println(operation);
			// Compute|AppType|ReceivedData
			// Download|ID
			// Check|ID
			// String[] recv = sb.toString().split("\\|");
			// System.out.println(recv.length+" LENGTH");
			// String operation = recv[0];
			if (operation.equals("Compute")) {
				String appType = ois.readUTF();
				Object data = ois.readObject();
				compute(appType, data, taskList);
			} else if (operation.equals("Check")) {
				String ID = (String) ois.readObject();
				check(ID);
			} else if (operation.equals("Download")) {
				String ID = (String) ois.readObject();
				download(ID);
			} else if (operation.equals("Offload")) {
				String appType = ois.readUTF();
				Object data = ois.readObject();
				offload(appType, data, taskList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void offload(String appType, Object Data, ArrayList<Task> taskList) {
		try {
			Task t = new Task();
			if (appType.equals("Example")) {
				t.output.setData(Processes.Example());
			} else if (appType.equals("MatrixAddition")) {
				t.output.setData(Processes.MatrixAddition(Data));
			} else if (appType.equals("ImageRecognition")) {
				Long time = System.nanoTime();
				t.output.setData(Processes.ImageRecognition(Data));
				System.out.println(""+(System.nanoTime()-time)/Math.pow(10, 9));
			} else if (appType.equals("Encrypt")) {
				t.output.setData(Processes.AESEncryption(Data));
			} else if(appType.equals("M1")){
				t.output.setData(Processes.M1(Data));
			} else if(appType.equals("M2")){
				t.output.setData(Processes.M2(Data));
			} else if(appType.equals("M3")){
				t.output.setData(Processes.M3(Data));
			} else if(appType.equals("M4")){
				t.output.setData(Processes.M4(Data));
			} else if(appType.equals("M5")){
				t.output.setData(Processes.M5(Data));
			} else if(appType.equals("M6")){
				t.output.setData(Processes.M6(Data));
			} else if(appType.equals("M7")){
				t.output.setData(Processes.M7(Data));
			} else if(appType.equals("M8")){
				t.output.setData(Processes.M8(Data));
			}
			System.out.println(t.hashCode() + " Compute Done!\n");
			Long time = System.nanoTime();
			GZIPOutputStream gos = new GZIPOutputStream(client.getOutputStream());
			ObjectOutputStream oos = new ObjectOutputStream(
					gos);
			oos.writeObject(t.output.getData());
			oos.flush();
			gos.finish();
			
			
			client.shutdownOutput();
			client.close();
			
			File f = new File("/home/leoyuchuan/Log/log");
			if (!f.exists())
				f.createNewFile();
			FileWriter fw = new FileWriter(f,true);
			fw.append("Downloading Time|0|" + (System.nanoTime() - time)+"\n");
			fw.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void compute(String appType, Object Data, ArrayList<Task> taskList) {
		try {
			Task t = new Task();
			int ID = t.hashCode();
			taskList.add(t);
			// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
			// client.getOutputStream()));
			// bw.write(String.valueOf(ID));
			// bw.flush();
			ObjectOutputStream oos = new ObjectOutputStream(
					client.getOutputStream());
			oos.writeUTF(String.valueOf(ID));
			oos.flush();
			client.shutdownOutput();
			client.close();
			t.output.setData(Processes.Example());
			System.out.println(t.hashCode() + " Compute Done!\n");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void download(String ID) {
		try {
			synchronized (taskList) {
				Iterator<Task> it = taskList.iterator();
				Task t = null;
				while (it.hasNext()) {
					t = it.next();
					if (t.hashCode() == Integer.parseInt(ID)) {
						break;
					}
					t = null;
				}
				// BufferedWriter bw = new BufferedWriter(new
				// OutputStreamWriter(
				// client.getOutputStream()));
				ObjectOutputStream oos = new ObjectOutputStream(
						client.getOutputStream());
				if (t == null) {
					// bw.write("false");
					// bw.flush();
					oos.writeObject("false");
					oos.flush();
					client.shutdownOutput();
				} else {
					if (t.output.equals("")) {
						// bw.write("false");
						// bw.flush();
						oos.writeObject("false");
						oos.flush();
						client.shutdownOutput();
					} else {
						// bw.write(t.output);
						// bw.flush();
						oos.writeObject(t.output.getData());
						oos.flush();
						client.shutdownOutput();
						taskList.remove(t);
					}
				}
				client.close();
			}
			System.out.println(ID + " Download Done!\n");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void check(String ID) {
		try {
			synchronized (taskList) {
				Iterator<Task> it = taskList.iterator();
				Task t = null;
				while (it.hasNext()) {
					t = it.next();
					if (t.hashCode() == Integer.parseInt(ID)) {
						break;
					}
					t = null;
				}
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						client.getOutputStream()));
				if (t == null) {
					bw.write("false");
					bw.flush();
					client.shutdownOutput();
				} else {
					if (t.output.equals("")) {
						bw.write("false");
						bw.flush();
						client.shutdownOutput();
					} else {
						bw.write("true");
						bw.flush();
						client.shutdownOutput();
					}
				}
				client.close();
			}
			System.out.println(ID + " Check Done!\n");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
