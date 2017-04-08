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

public class Checking implements Runnable {
	Task task;
	Data data;
	Data netID;
	Data recv;
	Scheduler scheduler;

	public Checking(Task task) {
		this.task = task;
		this.data = task.dataStorage;
		this.netID = task.netID;
		this.recv = task.receivedData;
		this.scheduler = task.getGlobalSchduler();
	}

	// Check|ID
	@Override
	public void run() {
		synchronized (task) {
			try {
				Socket sock = new Socket(OffloadingParams.USING_SERVER_IP,
						OffloadingParams.SERVER_PORT);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						sock.getOutputStream()));
				bw.write("Check|" + netID.getData());
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
				String result = sb.toString();
				// System.out.println(task.getTaskID() + " Task ID "
				// + task.getStatusFlag() + " Data: "
				// + task.receivedData.getData() + " Result: " + result);
				if (result.equals("false")) {
					task.setStatusFlag(TaskStatusParams.OFFLOADING);
					new Thread(new AsynCheck(scheduler)).start();
					return;
				} else if (result.equals("true")) {
					task.setStatusFlag(TaskStatusParams.WAITING);
					scheduler.notifyReschedule();
					return;
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}

class AsynCheck implements Runnable {

	private Scheduler scheduler;
	public AsynCheck(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(100);
			scheduler.notifyReschedule();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}