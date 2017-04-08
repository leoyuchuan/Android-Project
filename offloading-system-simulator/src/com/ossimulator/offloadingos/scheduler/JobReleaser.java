package com.ossimulator.offloadingos.scheduler;

import java.util.Iterator;

import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.task.Task;
import com.ossimulator.offloadingos.task.TaskList;

/**
 * Periodically Release jobs of task in list.
 * 
 * @author leoyuchuan
 *
 */
public class JobReleaser extends Thread {
	TaskList taskList;
	long periodInMS;
	boolean suspend = false;
	boolean paused = false;

	/**
	 * Initialize a job Releaser.
	 * 
	 * @param taskList
	 * @param periodInMS
	 */
	public JobReleaser(TaskList taskList, long periodInMS) {
		this.taskList = taskList;
		this.periodInMS = periodInMS;
	}

	@Override
	public void run() {
		while (true) {
			suspendRelease(); // suspend job releaser if suspend = true
			if (taskList.getSize() == 0) { // if taskList has no tasks, the
											// sleep period and goto next round
				try {
					Thread.sleep(periodInMS);
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					return;
				}
				continue;
			}
			synchronized (taskList) { // release jobs for each task
				Iterator<Task> it = taskList.getIterator();
				while (it.hasNext()) {
					Task task = it.next();
					if (task.getStatusFlag() != TaskStatusParams.DONE) {
						continue;
					}
					if (task.getTaskParams().getRemainingJobs() <= 1) {
						continue;
					}
					double start = task.getTaskParams().getStartTime();
					int intervalIndex = task.getTaskParams().getIntervalIndex();
					double period = task.getTaskParams().getT();
					double current = System.nanoTime();
					if (current - start - period * intervalIndex * 1000000 >= 0) {
						task.getTaskParams().setRemainingJobs(
								task.getTaskParams().getRemainingJobs() - 1);
						task.getTaskParams().setIntervalIndex(
								task.getTaskParams().getIntervalIndex() + 1);
						task.releaseJob();
					}
				}
			}
			try {
				Thread.sleep(periodInMS);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				return;
			}
		}
	}

	/**
	 * Suspend release.
	 */
	private void suspendRelease() {
		synchronized (this) {
			while (suspend) {
				try {
					paused = true;
					wait();
					paused = false;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * suspend releaser till paused
	 */
	public void suspendReleaser() {
		suspend = true;
		while (!paused) {
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

	}

	/**
	 * resume releaser
	 */
	public void resumeReleaser() {
		synchronized (this) {
			suspend = false;
			notify();
		}
	}

	/**
	 * stop releaser (just request, not guaranty immediate stop)
	 */
	public void stopRelease() {
		suspend = true;
	}
}
