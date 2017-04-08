package com.ossimulator.offloadingos.decisionmaker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.ossimulator.offloadingos.params.OffloadingParams;
import com.ossimulator.offloadingos.params.TaskParams;
import com.ossimulator.offloadingos.task.Task;
import com.ossimulator.offloadingos.task.TaskList;

public class DecisionMaker {
	/**
	 * Make all tasks decision local
	 * 
	 * @param taskList
	 * @return
	 */
	public static boolean makeLocalDecision(TaskList taskList) {
		Iterator<Task> it = taskList.getIterator();
		// int num = taskList.getSize();
		while (it.hasNext()) {
			// for (int i = 0; i < num; i++) {
			// taskList.getTask(i).setOffloadingDecision(OffloadingParams.LOCAL);
			it.next().setOffloadingDecision(OffloadingParams.LOCAL);
		}
		return true;
	}

	/**
	 * Make all tasks decision random
	 * 
	 * @param taskList
	 * @return
	 */
	public static boolean makeRandomDecision(TaskList taskList) {
		Iterator<Task> it = taskList.getIterator();
		// int num = taskList.getSize();
		while (it.hasNext()) {
			// for (int i = 0; i < num; i++) {
			// taskList.getTask(i).setOffloadingDecision(
			// new Random().nextInt(2) == 0 ? OffloadingParams.LOCAL
			// : OffloadingParams.OFFLOAD);
			it.next().setOffloadingDecision(
					new Random().nextInt(2) == 0 ? OffloadingParams.LOCAL
							: OffloadingParams.OFFLOAD);
		}
		return true;
	}

	/**
	 * Make all tasks decision offloading
	 * 
	 * @param taskList
	 * @return
	 */
	public static boolean makeOffloadDecision(TaskList taskList) {
		Iterator<Task> it = taskList.getIterator();
		while (it.hasNext()) {
			// int num = taskList.getSize();
			// for (int i = 0; i < num; i++) {
			// taskList.getTask(i).setOffloadingDecision(OffloadingParams.OFFLOAD);
			it.next().setOffloadingDecision(OffloadingParams.OFFLOAD);
		}
		return true;
	}

	public static boolean makeRODADecision(TaskList taskList, int m) {
		ArrayList<Task> Tnew = new ArrayList<Task>();
		// ArrayList<Task> Told = new ArrayList<Task>();

		double sumTnewC13 = 0;
		double sumToldC13 = 0;
		double sumToldC2 = 0;
		double sumTnewC2 = 0;
		double sumTnewCed = 0;

		// compare security time
		Iterator<Task> it = taskList.getIterator();
		while (it.hasNext()) {
			Task temp = it.next();
			TaskParams tmpParam = temp.getTaskParams();
			if (tmpParam.getCe() + tmpParam.getCd() > tmpParam.getC2()) {
				temp.setOffloadingDecision(OffloadingParams.LOCAL);
				sumToldC13 += (tmpParam.getC1() + tmpParam.getC3())
						/ tmpParam.getT();
				sumToldC2 += tmpParam.getC2() / tmpParam.getT();
				// Told.add(temp);
			} else {
				sumTnewC13 += (tmpParam.getC1() + tmpParam.getC3())
						/ tmpParam.getT();
				sumTnewCed += (tmpParam.getCe() + tmpParam.getCd())
						/ tmpParam.getT();
				Tnew.add(temp);
			}
		}

		// Ordering
		ArrayList<Task> TnewOrdered = new ArrayList<Task>();
		double max = 0;
		int index = 0;

		while (Tnew.size() > 0) {
			for (int i = 0; i < Tnew.size(); i++) {
				if (i == 0) {
					TaskParams tp = Tnew.get(i).getTaskParams();
					max = tp.getS2() / tp.getT();
					index = i;
				} else {
					TaskParams tp = Tnew.get(i).getTaskParams();
					if (tp.getS2() / tp.getT() > max) {
						max = tp.getS2() / tp.getT();
						index = i;
					}
				}
			}
			TnewOrdered.add(Tnew.get(index));
			Tnew.remove(index);
		}

		// making decision for Tnew
		for (int i = 0; i < TnewOrdered.size(); i++) {
			double sumS = 0;
			for (int j = 0; j < m; j++) {
				if (i + j < TnewOrdered.size()) {
					TaskParams tp = TnewOrdered.get(i + j).getTaskParams();
					sumS += tp.getS2() / tp.getT();
				}
			}
			if (i != 0) {
				TaskParams tp = TnewOrdered.get(i - 1).getTaskParams();
				sumTnewC2 += tp.getC2() / tp.getT();
				sumTnewCed -= (tp.getCe() + tp.getCd()) / tp.getT();
			}
			if (sumS <= m - sumTnewC13 - sumToldC13 - sumToldC2 - sumTnewC2
					- sumTnewCed) {
				for (int j = i; j < TnewOrdered.size(); j++) {
					TnewOrdered.get(j).setOffloadingDecision(
							OffloadingParams.OFFLOAD);
				}
				break;
			} else {
				TnewOrdered.get(i)
						.setOffloadingDecision(OffloadingParams.LOCAL);
			}
		}

		// combine tasks
		// ArrayList<Task> outputTaskSet = new ArrayList<Task>();
		// Iterator<Task> it1 = Told.iterator();
		// while (it1.hasNext()) {
		// outputTaskSet.add(it1.next());
		// }
		// Iterator<Task> it2 = TnewOrdered.iterator();
		// while (it2.hasNext()) {
		// outputTaskSet.add(it2.next());
		// }
		return true;
	}
}
