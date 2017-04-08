package com.ossimulator.offloadingos.offloading;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.ossimulator.offloadingos.params.OffloadingParams;
import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.scheduler.Scheduler;
import com.ossimulator.offloadingos.task.Task;
import com.ossimulator.offloadingos.util.Data;

public class Downloading implements Runnable {
	Task task;
	Data data;
	Data netID;
	Data recv;
	Scheduler scheduler;

	public Downloading(Task task) {
		this.task = task;
		this.data = task.dataStorage;
		this.netID = task.netID;
		this.recv = task.receivedData;
		this.scheduler = task.getGlobalSchduler();
	}

	// Download|ID
	@Override
	public void run() {
		synchronized (task) {
			try {
				Socket sock = new Socket(OffloadingParams.USING_SERVER_IP,
						OffloadingParams.SERVER_PORT);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						sock.getOutputStream()));
				bw.write("Download|" + netID.getData());
				bw.flush();
				sock.shutdownOutput();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String tmp;
				while ((tmp = br.readLine()) != null) {
					sb.append(tmp);
				}
				recv.setData(sb.toString());
				sock.close();
				task.setStatusFlag(TaskStatusParams.WAITING);
				scheduler.notifyReschedule();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
