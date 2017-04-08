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

class Uploading implements Runnable {
	Data data;
	Data netID;
	Data recv;

	Task task;
	Scheduler scheduler;

	public Uploading(Task task) {
		this.data = task.dataStorage;
		this.netID = task.netID;
		this.recv = task.receivedData;
		this.task = task;
		this.scheduler = task.getGlobalSchduler();
	}

	// Compute|AppType|Data
	@Override
	public void run() {
		synchronized (task) {
			try {
				Socket sock = new Socket(OffloadingParams.USING_SERVER_IP,
						OffloadingParams.SERVER_PORT);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						sock.getOutputStream()));
				if (data == null)
					System.out.println("null data");
				bw.write("Compute|Example|empty");
				bw.flush();
				sock.shutdownOutput();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						sock.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String tmp;
				while ((tmp = br.readLine()) != null) {
					sb.append(tmp);
				}
				sock.close();
				netID.setData(sb.toString());
				task.setStatusFlag(TaskStatusParams.OFFLOADING);
				scheduler.notifyReschedule();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}