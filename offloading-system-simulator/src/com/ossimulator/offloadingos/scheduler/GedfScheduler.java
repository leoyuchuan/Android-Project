package com.ossimulator.offloadingos.scheduler;

import java.util.ArrayList;
import java.util.Iterator;

import com.ossimulator.offloadingos.decisionmaker.DecisionMaker;
import com.ossimulator.offloadingos.offloading.OffloadingManager;
import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.task.Task;
import com.ossimulator.offloadingos.task.TaskList;

public class GedfScheduler extends Scheduler {

	public GedfScheduler(int cores) {
		super(cores);
	}

	@Override
	public void checkDecision() {
		if (taskNum != taskList.getSize()) {
			taskNum = taskList.getSize();
			// DecisionMaker.makeOffloadDecision(fullList);
			// DecisionMaker.makeLocalDecision(taskList);
			// DecisionMaker.makeRandomDecision(fullList);
			DecisionMaker.makeRODADecision(fullList, cpu.length);
		}
	}

	@Override
	public void orderTasks() {
		float now = System.nanoTime();
		TaskList tmpList = new TaskList();
		while (taskList.getSize() != 0) {
			double max = taskList.getTask(0).getDeadline(now);
			Task maxT = taskList.getTask(0);
			Iterator<Task> it = taskList.getIterator();
			while (it.hasNext()) {
				Task t = it.next();
				double dl = t.getDeadline(now);
				if (dl > max) {
					max = dl;
					maxT = t;
				}
			}
			taskList.removeTask(maxT);
			tmpList.addTask(maxT);
		}
		Iterator<Task> it = tmpList.getIterator();
		while (it.hasNext()) {
			Task t = it.next();
			taskList.addTask(t);
		}
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
				// check with server async and turn status into checking, there
				// are two option to checking as follows
				// if (false) {// periodically, keep checking async, when done,
				// // turn
				// // into waiting and notify scheduler to reschedule
				// ;
				// } else if (true) {// check on reschedule, if not done, turn
				// // into
				// // offloading, if done, turn into waiting and notify
				// // scheduler to reschedule
				// OffloadingManager.check(task);
				// }
			} else if (status == TaskStatusParams.RUNNING) {
				ignoreCPU.add(task.getCpuID());
				tasknum++;
			} else if (status == TaskStatusParams.WAITING) {
				tempList.addTask(task);
				tasknum++;
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
		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// suspend = false;
	}

}
