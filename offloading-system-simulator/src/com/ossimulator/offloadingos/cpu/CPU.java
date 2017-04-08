package com.ossimulator.offloadingos.cpu;

import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.scheduler.Scheduler;
import com.ossimulator.offloadingos.task.*;

/**
 * Simulate CPU
 * 
 * @author leoyuchuan
 *
 */
public class CPU {
	private volatile Task currentTask;
	private int cpuID;
	private Scheduler scheduler;

	/**
	 * Initialize CPU without ID (-1 means not ID)
	 */
	public CPU() {
		this.cpuID = -1;
	}

	/**
	 * Initialize CPU with specific ID
	 * 
	 * @param cpuID
	 */
	public CPU(int cpuID) {
		this.cpuID = cpuID;
	}

	/**
	 * Initialize CPU with specific ID and associate Scheduler
	 * 
	 * @param cpuID
	 * @param schduler
	 */
	public CPU(int cpuID, Scheduler schduler) {
		this.cpuID = cpuID;
		this.scheduler = schduler;
	}

	/**
	 * Get Current Task
	 * 
	 * @return
	 */
	public Task getCurrentTask() {
		return currentTask;
	}

	/**
	 * Change Current Task, and associate task with this CPU <br/>
	 * If there are no current task, put task in. <br/>
	 * If there exists current task, pause current one and put another in.
	 * 
	 * @param currentTask
	 */
	public void setCurrentTask(Task currentTask) {
		if (this.currentTask == null) {
			this.currentTask = currentTask;
			this.currentTask.setCpu(this);
		} else {
			this.currentTask.pause();
			this.currentTask = currentTask;
			this.currentTask.setCpu(this);
		}
	}

	/**
	 * If current task is a new thread, then start <br/>
	 * If current task is not a new thread, then resume <br/>
	 * 
	 * @return
	 */
	public boolean start() {
		if (currentTask == null)
			return false;
		if (currentTask.getState().equals(Thread.State.NEW))
			currentTask.start();
		else {
			currentTask.proceed();
		}
		return true;
	}

	/**
	 * remove current task, and notify scheduler to reschedule if exists
	 * 
	 * @return
	 */
	public boolean notifyDone() {
		if (scheduler == null)
			return false;
		return scheduler.notifyReschedule();
	}

	/**
	 * Stop current task asynchronously
	 * 
	 * @return
	 */
	public boolean stop() {
		if (currentTask == null)
			return true;
		new Thread(new StopTaskAsync(currentTask)).start();
		return true;
	}

	/**
	 * Get current CPU id.
	 * 
	 * @return
	 */
	public int getCpuID() {
		return cpuID;
	}

	/**
	 * Get associated scheduler.
	 * 
	 * @return
	 */
	public Scheduler getAssociatedScheduler() {
		return scheduler;
	}

	/**
	 * Set associated scheduler.
	 * 
	 * @param scheduler
	 */
	public void setAssociatedScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/**
	 * Stop task asynchronously
	 * 
	 * @author leoyuchuan
	 *
	 */
	private class StopTaskAsync implements Runnable {
		Task task;

		public StopTaskAsync(Task task) {
			this.task = task;
		}

		@Override
		public void run() {
			task.pause();
		}
	}
}
