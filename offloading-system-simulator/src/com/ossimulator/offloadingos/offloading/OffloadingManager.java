package com.ossimulator.offloadingos.offloading;

import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.task.Task;

public class OffloadingManager {
	public static void upload(Task task) {
		task.setStatusFlag(TaskStatusParams.UPLOADING);
		new Thread(new Uploading(task)).start();
	}

	public static void check(Task task) {
		if (!task.receivedData.getData().equals(""))
			return;
		task.setStatusFlag(TaskStatusParams.CHECKING);
		new Thread(new Checking(task)).start();
	}

	public static void download(Task task) {
		task.setStatusFlag(TaskStatusParams.DOWNLOADING);
		new Thread(new Downloading(task)).start();
	}
	
	public static void offload(Task task){
		new Thread(new Offloading(task)).start();
	}
}
