package com.ossimulator.offloadingos.task;

import java.util.*;

/**
 * Task List
 * 
 * @author leoyuchuan
 *
 */
public class TaskList {
	private ArrayList<Task> taskList;

	/**
	 * Initialize an empty task list.
	 */
	public TaskList() {
		taskList = new ArrayList<Task>();
	}

	/**
	 * Add a task
	 * 
	 * @param t
	 *            See {@link com.ossimulator.offloadingos.task.Task}
	 * @return
	 */
	public boolean addTask(Task t) {
		return taskList.add(t);
	}

	/**
	 * Add a task to specific index
	 * 
	 * @param index
	 * @param t
	 * @return
	 */
	public boolean addTask(int index, Task t) {
		taskList.add(index, t);
		return true;
	}

	/**
	 * Remove a task
	 * 
	 * @param t
	 * @return
	 */
	public boolean removeTask(Task t) {
		return taskList.remove(t);
	}

	/**
	 * Get Iterator.
	 * 
	 * @return
	 */
	public Iterator<Task> getIterator() {
		return taskList.iterator();
	}

	/**
	 * Get Task at Index
	 * 
	 * @param index
	 * @return
	 */
	public Task getTask(int index) {
		return taskList.get(index);
	}

	/**
	 * Get size of task list.
	 * 
	 * @return
	 */
	public int getSize() {
		return taskList.size();
	}
}
