package com.ossimulator.offloadingos.task;

import com.ossimulator.offloadingos.cpu.*;
import com.ossimulator.offloadingos.params.*;
import com.ossimulator.offloadingos.scheduler.Scheduler;
import com.ossimulator.offloadingos.util.Data;

public abstract class Task extends Thread {
	/**
	 * Offloading decision of task.
	 */
	protected int offloadingDecision = OffloadingParams.LOCAL;
	/**
	 * The current state of task. Refer to
	 * {@link com.ossimulator.offloadingos.params.TaskStatusParams}
	 */
	private int statusFlag = TaskStatusParams.INITIALIZED;
	/**
	 * String Storage for Each Task. Will be initialed at each release.
	 */
	public Data dataStorage;
	/**
	 * String Storage for Each Task. Used for Get Server ID
	 */
	public Data netID;
	/**
	 * String Received from server. Since there are synchronization problem,
	 * need this to receive.
	 */
	public Data receivedData;
	/**
	 * Task parameters used for offloading decision, release, etc. See
	 * {@link com.ossimulator.offloadingos.params.TaskParams}
	 */
	private TaskParams taskParams;
	/**
	 * The current CPU that executing this task.
	 */
	private CPU cpu;
	/**
	 * The scheduler which is responsible for scheduling this task.
	 */
	private Scheduler globalSchduler;
	/**
	 * The identifier of task.
	 */
	private int taskID = 0;
	/**
	 * The suspend parameter for controlling task suspension.<br/>
	 * True for suspend. False for continue.<br/>
	 * Notice that the task is not immediate responding when suspend turn to
	 * true.<br/>
	 * Notice that the task is not continue when suspend turn to false. Still
	 * need to notify this task.<br/>
	 */
	protected volatile boolean suspend = false;
	/**
	 * Used to judge if task needs to be released. <br/>
	 * Please call {@link #releaseJob()} to realize release job.
	 */
	protected volatile boolean released = false;
	/**
	 * The pause parameter is used for see if task is paused. If it is true, the
	 * task is not running. If it is false, the task is still running.
	 */
	protected volatile boolean paused = false;

	/**
	 * Initialize a new task with default
	 * {@link com.ossimulator.offloadingos.params.TaskParams} and empty
	 * {@link #dataStorage}
	 */
	public Task() {
		taskParams = TaskParams.getDefaultParams();
		dataStorage = Data.getEmptyData();
		receivedData = Data.getEmptyData();
		netID = Data.getEmptyData();
	}

	/**
	 * Initialize a new task with a
	 * {@link com.ossimulator.offloadingos.params.TaskParams} taskParams and
	 * empty {@link #dataStorage}
	 * 
	 * @param taskParams
	 *            A taskParams Instance.
	 */
	public Task(TaskParams taskParams) {
		this.taskParams = taskParams;
		dataStorage = Data.getEmptyData();
		netID = Data.getEmptyData();
		receivedData = Data.getEmptyData();
	}

	/**
	 * Abstract First Phase Execution
	 */
	protected abstract void firstPhase();

	/**
	 * Abstract Second Phase Execution
	 */
	protected abstract void secondPhase();

	/**
	 * Abstract Third Phase Execution
	 */
	protected abstract void thirdPhase();

	/**
	 * Trying to Pause the Task and This function will block the thread Until
	 * the task is paused.
	 * 
	 * @return true if the task is paused and false if there are exception.
	 */
	public boolean pause() {
		try {
			suspend = true;
			while (!paused) {
				Thread.sleep(1);
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	/**
	 * Change suspend parameter to false and notify task.
	 * 
	 * @return true if task is notified, false if exists exception.
	 */
	public boolean proceed() {
		synchronized (this) {
			try {
				suspend = false;
				notify();
				return true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			reset();// initialize task
			statusFlag = TaskStatusParams.RUNNING;// set state running and go
													// into first phase.
			firstPhase();
			cpu.notifyDone();// first phase execution done, notify cpu that the
								// current phase is done, and then suspend
								// execution
								// this task.
			suspend = true;
			checkSuspend();
			secondPhase(); // start to execute second phase.
			cpu.notifyDone(); // second phase execution done, notify cpu that
								// the
								// current phase is done, and then suspend
								// execution of this task.
			suspend = true;
			checkSuspend();
			thirdPhase(); // start to execute third phase.
			statusFlag = TaskStatusParams.DONE; // third execution phase done,
												// if new job is not released
												// the following code will
												// suspend this task.
			synchronized (this) {
				while (!released) {
					try {
						paused = true;
						cpu.notifyDone();
						wait();
						paused = false;
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Initialize Task
	 */
	protected void reset() {
		dataStorage = Data.getEmptyData();
		netID = Data.getEmptyData();
		receivedData = Data.getEmptyData();
		released = false;
		paused = false;
		statusFlag = TaskStatusParams.INITIALIZED;
		synchronized (this) {
			while (suspend) {
				try {
					globalSchduler.notifyReschedule();
					paused = true;
					wait();
					paused = false;
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * Get offloading decision for this task.
	 * 
	 * @return offloading decision
	 */
	public int getOffloadingDecision() {
		return offloadingDecision;
	}

	/**
	 * Set offloading decision for this task.
	 * 
	 * @param offloadingDecision
	 *            A offloading decision
	 *            {@link com.ossimulator.offloadingos.params.OffloadingParams}
	 */
	public void setOffloadingDecision(int offloadingDecision) {
		this.offloadingDecision = offloadingDecision;
	}

	/**
	 * Get status of this task.
	 * 
	 * @return Status of Task. See
	 *         {@link com.ossimulator.offloadingos.params.TaskStatusParams}
	 */
	public int getStatusFlag() {
		return statusFlag;
	}

	/**
	 * Set status of this task.
	 * 
	 * @param statusFlag
	 *            Status of Task. See
	 *            {@link com.ossimulator.offloadingos.params.TaskStatusParams}
	 */
	public void setStatusFlag(int statusFlag) {
		this.statusFlag = statusFlag;
	}

	/**
	 * get the current cpu identifier that executing this task
	 * 
	 * @return positive for concrete id, -1 means this task is not executing by
	 *         a cpu.
	 */
	public int getCpuID() {
		return cpu.getCpuID();
	}

	/**
	 * get the current cpu that executing this task
	 * 
	 * @return not null for current cpu, null means the task is not executing by
	 *         cpu.
	 */
	public CPU getCpu() {
		return cpu;
	}

	/**
	 * set the current cpu that will execute this task.
	 * 
	 * @param cpu
	 *            the cpu that will execute this task. See
	 *            {@link com.ossimulator.offloadingos.cpu.CPU}
	 */
	public void setCpu(CPU cpu) {
		this.cpu = cpu;
	}

	/**
	 * get {@link #taskID}
	 * 
	 * @return
	 */
	public int getTaskID() {
		return taskID;
	}

	/**
	 * set {@link #taskID}
	 * 
	 * @param taskID
	 */
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	/**
	 * Task Checkpoint. If need task to be suspend at some point, insert this
	 * method in. <br/>
	 * If task suspends at this point, the task will first turn into waiting
	 * state.<br/>
	 * Once resume, the task will turn to running state.
	 */
	public void checkSuspend() {
		synchronized (this) {
			while (suspend) {
				try {
					paused = true;
					setStatusFlag(TaskStatusParams.WAITING);
					cpu.notifyDone();
					wait();
					paused = false;
					setStatusFlag(TaskStatusParams.RUNNING);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * Task Checkpoint. if need task to be suspend point without change state
	 * automatically, insert this method in. <br/>
	 * The state of task will not change by invoking this method.
	 */
	public void checkSuspendNoStatus() {
		synchronized (this) {
			while (suspend) {
				try {
					paused = true;
					cpu.notifyDone();
					wait();
					paused = false;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * Release a job if previous job is done. And task will suspend right after
	 * {@link #reset()}.
	 * 
	 * @return true if job is released, false if job is not released or
	 *         exception.
	 */
	public boolean releaseJob() {
		synchronized (this) {
			try {
				if (statusFlag != TaskStatusParams.DONE)
					return false;
				released = true;
				suspend = true;
				notify();
				return true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}
	}

	/**
	 * Get TaskParams {@link com.ossimulator.offloadingos.params.TaskParams}
	 * 
	 * @return TaskParams
	 */
	public TaskParams getTaskParams() {
		return taskParams;
	}

	/**
	 * Set TaskParams {@link com.ossimulator.offloadingos.params.TaskParams}
	 * 
	 * @param taskParams
	 *            A TaskParams
	 */
	public void setTaskParams(TaskParams taskParams) {
		this.taskParams = taskParams;
	}

	/**
	 * Get Relative Deadline with repect to now.
	 * 
	 * @param now
	 *            time which deadline with respect to.
	 * @return Will be negative if not miss deadline, and positive if missed.
	 */
	public double getDeadline(float now) {
		double dl = now
				- (this.getTaskParams().getIntervalIndex()
						* this.getTaskParams().getT() * 1000000 + this
						.getTaskParams().getStartTime());
		return dl;
	}

	/**
	 * See {@link #globalSchduler}
	 * 
	 * @return Scheduler
	 */
	public Scheduler getGlobalSchduler() {
		return globalSchduler;
	}

	/**
	 * See {@link #globalSchduler}
	 * 
	 * @param globalSchduler
	 *            scheduler
	 */
	public void setGlobalSchduler(Scheduler globalSchduler) {
		this.globalSchduler = globalSchduler;
	}

	public abstract String getType();
}
