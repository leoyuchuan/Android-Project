package com.ossimulator.offloadingos.scheduler;

import java.util.ArrayList;
import java.util.Iterator;

import com.ossimulator.offloadingos.cpu.CPU;
import com.ossimulator.offloadingos.decisionmaker.DecisionMaker;
import com.ossimulator.offloadingos.params.TaskParams;
import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.task.ExampleTask;
import com.ossimulator.offloadingos.task.Task;
import com.ossimulator.offloadingos.task.TaskList;

/**
 * An example of Simple Periodic Scheduler.
 * 
 * @author leoyuchuan
 *
 */
public class SimpleScheduler extends Scheduler {
	public SimpleScheduler(int cores) {
		super(cores);
	}

	@Override
	public void checkDecision() {
		if (taskNum != taskList.getSize()) {
			taskNum = taskList.getSize();
			// makeLocalDecision();
			DecisionMaker.makeLocalDecision(fullList);
		}
	}

	@Override
	public void orderTasks() {
		Task t = taskList.getTask(0);
		taskList.removeTask(t);
		taskList.addTask(t);
	}

	@Override
	public void allocateTasks() {
		Iterator<Task> iter = taskList.getIterator();
		TaskList tempList = new TaskList();
		ArrayList<Integer> ignoreCPU = new ArrayList<Integer>();
		int tasknum = 0;
		while (iter.hasNext()) {
			Task task = iter.next();
			int status = task.getStatusFlag();
			if (status == TaskStatusParams.INITIALIZED) {
				tempList.addTask(task);
				tasknum++;
			} else if (status == TaskStatusParams.OFFLOADING) {

			} else if (status == TaskStatusParams.RUNNING) {
				ignoreCPU.add(task.getCpuID());
				tasknum++;
			} else if (status == TaskStatusParams.WAITING) {
				tempList.addTask(task);
				tasknum++;
			} else if (status == TaskStatusParams.DONE) {

			}
			if (tasknum >= cpu.length) {
				break;
			}
		}
		// load task to cpu
		for (int i = 0; i < cpu.length; i++) {
			if (tempList.getSize() == 0)
				break;
			if (ignoreCPU.contains(i)) {
				continue;
			}
			Task task = tempList.getTask(0);
			tempList.removeTask(task);
			cpu[i].setCurrentTask(task);
			cpu[i].start();
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
