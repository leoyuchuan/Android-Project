package com.ossimulator.offloadingos.scheduler;

import java.util.Iterator;

import com.ossimulator.offloadingos.cpu.CPU;
import com.ossimulator.offloadingos.decisionmaker.DecisionMaker;
import com.ossimulator.offloadingos.task.Task;
import com.ossimulator.offloadingos.task.TaskList;

/**
 * Base Scheduler
 * 
 * @author leoyuchuan
 *
 */
public abstract class Scheduler extends Thread {
	/**
	 * Dynamic TaskList
	 */
	protected volatile TaskList taskList;
	/**
	 * TaskList for JobReleasing
	 */
	protected volatile TaskList fullList;
	/**
	 * Temporary Task Counting, used to decide if reschedule is needed. (Very
	 * rough)
	 */
	protected int taskNum = 0;
	/**
	 * Scheduler Accessible CPUs
	 */
	protected CPU[] cpu;
	/**
	 * running is used for stop scheduler
	 */
	protected volatile boolean running = false;
	/**
	 * the stop state of this scheduler
	 */
	protected volatile boolean stopped = false;
	/**
	 * suspend is used for suspend the scheduler
	 */
	protected volatile boolean suspend = false;
	/**
	 * Used for managing job releasing
	 */
	JobReleaser jobReleaser;

	/**
	 * Initialize a scheduler with n CPUs
	 * 
	 * @param cores
	 */
	public Scheduler(int cores) {
		taskList = new TaskList();
		fullList = new TaskList();
		cpu = new CPU[cores];
		for (int i = 0; i < cores; i++) {
			cpu[i] = new CPU(i);
			cpu[i].setAssociatedScheduler(this);
		}
		taskNum = taskList.getSize();
	}

	/**
	 * Make Decision to local for all task
	 * 
	 * @return
	 */

	@Override
	public void run() {
		jobReleaser = new JobReleaser(fullList, 10); // try to release task
														// every 10ms
		Iterator<Task> it = taskList.getIterator();// initialize start time for
													// every task
		while (it.hasNext()) {
			it.next().getTaskParams().setStartTime(System.nanoTime());
		}
		jobReleaser.start(); // start jobReleaser
		running = true;
		while (true) {
			suspend = true;
			checkDecision();
			orderTasks();
			allocateTasks();
			suspendScheduler();
		}
	}

	/**
	 * check offloading decision, by default the scheduler will suspend each
	 * scheduling if suspend is not turn to false.
	 */
	public abstract void checkDecision();

	/**
	 * order task (e.g. GEDF, FIFO), by default the scheduler will suspend each
	 * scheduling if suspend is not turn to false.
	 */
	public abstract void orderTasks();

	/**
	 * allocate task to CPU, by default the scheduler will suspend each
	 * scheduling if suspend is not turn to false.
	 */
	public abstract void allocateTasks();

	/**
	 * If requesting to stop scheduler, then stop scheduler<br/>
	 * If Scheduler is still running, suspend based on suspend parameter.
	 */
	public void suspendScheduler() {
		synchronized (this) {
			while (!running) {
				try {
					stopped = true;
					wait();
					stopped = false;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
		synchronized (this) {
			while (suspend) {
				try {
					wait();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * proceed scheduler if scheduler is paused by suspend parameter
	 * 
	 * @return
	 */
	public boolean proceedScheduler() {
		synchronized (this) {
			suspend = false;
			notify();
			return true;

		}
	}

	/**
	 * stop scheduler and return after the scheduling is stopped
	 * 
	 * @return
	 */
	public boolean stopScheduling() {
		running = false;
		if (!suspend) {
			while (!stopped) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			}
		}
		for (int i = 0; i < cpu.length; i++) {
			cpu[i].stop();
		}
		try {
			if (jobReleaser != null) {
				jobReleaser.stopRelease();
				jobReleaser = null;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return true;
	}

	/**
	 * Add task to taskList. If scheduler is running, set StartTime to new task.
	 * 
	 * @param task
	 */
	public void addTask(Task task) {
		task.setGlobalSchduler(this);
		if (running) {
			task.getTaskParams().setStartTime(System.nanoTime());
		}
		this.taskList.addTask(task);
		this.fullList.addTask(task);
	}

	/**
	 * Remove task from taskList. This may cause synchronization error.
	 * 
	 * @param task
	 */
	public void removeTask(Task task) {
		synchronized (taskList) {
			this.taskList.removeTask(task);
			this.fullList.removeTask(task);
		}
	}

	/**
	 * Remove all task from taskList. This may cause synchronization error.
	 */
	public void removeAllTask() {
		synchronized (taskList) {
			this.taskList = new TaskList();
			this.fullList = new TaskList();
		}
	}

	/**
	 * notify reschedule from other places
	 * 
	 * @return
	 */
	public boolean notifyReschedule() {
		synchronized (this) {
			suspend = false;
			notify();
			return true;
		}
	}

}
