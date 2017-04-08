package com.ossimulator.offloadingos.task;

import com.ossimulator.offloadingos.offloading.OffloadingManager;
import com.ossimulator.offloadingos.params.AppType;
import com.ossimulator.offloadingos.params.OffloadingParams;
import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.util.postToUI;

import android.widget.EditText;

/**
 * An android local task example. <br/>
 * <br/>
 * Cannot run offloading
 * 
 * @author leoyuchuan
 *
 */
public class AndroidExampleTask extends Task {

	private EditText log;

	public AndroidExampleTask(int taskID, EditText log) {
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
			System.out.println("This is Second Phase" + " CPU: " + getCpuID()
					+ " TaskID: " + getTaskID() + " ");
			log.post(new postToUI(log, "This is Second Phase" + " CPU: "
					+ getCpuID() + " TaskID: " + getTaskID() + " \n"));
			for (int i = 0; i < 10; i++) {
				checkSuspend();
				log.post(new postToUI(log, "Result: " + i + " CPU: "
						+ getCpuID() + " TaskID: " + getTaskID() + " \n"));
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				checkSuspend();
			}
		} else if (offloadingDecision == OffloadingParams.OFFLOAD) {
			// offloading pre overhead

			// send to server async, and send datastorage to server and get
			// identification in datastorage
			setStatusFlag(TaskStatusParams.OFFLOADING);
			OffloadingManager.upload(this);
			// set suspend
			// suspend = true;
			// suspend and get notify when offloading done, scheduler will turn
			// this task waiting when offloading computation done
			// checkOffloadSuspend();
			// receiving from server async and notify this program when done,
			// send identification to server and receive processed data, when
			// data received, turn status of this task to waiting and notify
			// scheduler to reschedule
			// Receiver.receive(dataStorage);
			// suspend when downloading
			checkSuspendNoStatus();
			// offloading post overhead

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
