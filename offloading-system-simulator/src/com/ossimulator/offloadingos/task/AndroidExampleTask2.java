package com.ossimulator.offloadingos.task;

import com.ossimulator.offloadingos.offloading.OffloadingManager;
import com.ossimulator.offloadingos.params.AppType;
import com.ossimulator.offloadingos.params.OffloadingParams;
import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.util.postToUI;

import android.widget.EditText;

/**
 * Offloading Preemptive Task
 * 
 * @author leoyuchuan
 *
 */
public class AndroidExampleTask2 extends Task {

	private EditText log;

	public AndroidExampleTask2(int taskID, EditText log) {
		super();
		this.setTaskID(taskID);
		this.log = log;
	}

	@Override
	protected void firstPhase() {
		checkSuspend();
		System.out.println("This is First Phase of This Task! CPU: "
				+ getCpuID() + " TaskID: " + getTaskID() + " ");
		log.post(new postToUI(log, "This is First Phase of This Task! CPU: "
				+ getCpuID() + " TaskID: " + getTaskID() + " \n"));
		checkSuspend();
	}

	@Override
	protected void secondPhase() {
		checkSuspend();
		if (offloadingDecision == OffloadingParams.LOCAL) {
			int sum = 0;
			System.out.println("This is Second Phase" + " CPU: " + getCpuID()
					+ " TaskID: " + getTaskID() + " ");
			log.post(new postToUI(log, "This is Second Phase" + " CPU: "
					+ getCpuID() + " TaskID: " + getTaskID() + " \n"));
			for (int i = 0; i < 10; i++) {
				checkSuspend();
				sum += i;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				checkSuspend();
			}
			System.out.println("Second Phase Output: " + sum + " CPU: "
					+ getCpuID() + " TaskID: " + getTaskID() + " ");
			log.post(new postToUI(log, "This is Second Phase" + " CPU: "
					+ getCpuID() + " TaskID: " + getTaskID() + " \n"));
		} else if (offloadingDecision == OffloadingParams.OFFLOAD) {
			// Offloading by Checking regularly
			// offloading pre overhead
			// checkSuspend();
			// send to server async, and send datastorage to server and get
			// identification in datastorage
			// setStatusFlag(TaskStatusParams.UPLOADING);
			// OffloadingManager.upload(this);
			// set suspend
			// suspend = true;
			// suspend and get notify when offloading done, scheduler will turn
			// this task waiting when offloading computation done
			// checkSuspendNoStatus();
			// receiving from server async and notify this program when done,
			// send identification to server and receive processed data, when
			// data received, turn status of this task to waiting and notify
			// scheduler to reschedule
			// setStatusFlag(TaskStatusParams.DOWNLOADING);
			// OffloadingManager.download(this);
			// suspend when downloading
			// suspend = true;
			// checkSuspendNoStatus();
			// offloading post overhead
			// System.out.println("Second Phase Output: "
			// + this.receivedData.getData() + " CPU: " + getCpuID()
			// + " TaskID: " + getTaskID() + " ");
			// log.post(new postToUI(log, "This is Second Phase" + " CPU: "
			// + getCpuID() + " TaskID: " + getTaskID() + " \n"));
			// checkSuspend();

			checkSuspend();
			setStatusFlag(TaskStatusParams.OFFLOADING);
			OffloadingManager.offload(this);
			suspend = true;
			checkSuspendNoStatus();
			System.out.println("Second Phase Output: "
					+ this.receivedData.getData() + " CPU: " + getCpuID()
					+ " TaskID: " + getTaskID() + " ");
			log.post(new postToUI(log, "This is Second Phase" + " CPU: "
					+ getCpuID() + " TaskID: " + getTaskID() + " \n"));
			checkSuspend();
		}
	}

	@Override
	protected void thirdPhase() {
		checkSuspend();
		System.out.println("This is Third Phase of This Task! CPU: "
				+ getCpuID() + " TaskID: " + getTaskID() + " ");
		log.post(new postToUI(log, "This is Third Phase of This Task! CPU: "
				+ getCpuID() + " TaskID: " + getTaskID() + " \n"));
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return AppType.Example;
	}
}
