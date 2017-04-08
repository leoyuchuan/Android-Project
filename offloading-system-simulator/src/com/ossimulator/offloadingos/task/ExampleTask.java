package com.ossimulator.offloadingos.task;

import com.ossimulator.offloadingos.params.AppType;
import com.ossimulator.offloadingos.params.TaskStatusParams;

/**
 * Example Java Task (Can't Output On Android)
 * 
 * @author leoyuchuan
 *
 */
public class ExampleTask extends Task {

	public ExampleTask() {
		super();
		this.setTaskID(-1);
	}

	public ExampleTask(int taskID) {
		super();
		this.setTaskID(taskID);
	}

	@Override
	protected void firstPhase() {
		checkSuspend();
		System.out.println("This is First Phase of This Task! CPU: "
				+ getCpuID() + " TaskID: " + getTaskID() + " ");
		checkSuspend();
	}

	@Override
	protected void secondPhase() {
		checkSuspend();
		System.out.println("This is Second Phase" + " CPU: " + getCpuID()
				+ " TaskID: " + getTaskID() + " ");
		for (int i = 0; i < 10; i++) {
			checkSuspend();
			System.out.println("Result: " + i + " CPU: " + getCpuID()
					+ " TaskID: " + getTaskID() + " ");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		checkSuspend();
	}

	@Override
	protected void thirdPhase() {
		checkSuspend();
		System.out.println("This is Third Phase of This Task! CPU: "
				+ getCpuID() + " TaskID: " + getTaskID() + " ");
	}

	@Override
	public String getType() {
		return AppType.Example;
	}

}
