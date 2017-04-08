package com.ossimulator.offloadingos.task;

import android.widget.EditText;

import com.ossimulator.offloadingos.offloading.OffloadingManager;
import com.ossimulator.offloadingos.params.AppType;
import com.ossimulator.offloadingos.params.OffloadingParams;
import com.ossimulator.offloadingos.params.TaskParams;
import com.ossimulator.offloadingos.params.TaskStatusParams;
import com.ossimulator.offloadingos.util.Matrix;
import com.ossimulator.offloadingos.util.postToUI;

/**
 * Matrix Calculation Task
 * 
 * @author leoyuchuan
 *
 */
public class SimpleMatrixTask extends Task {

	Matrix matrix;
	private EditText log;

	public SimpleMatrixTask(int taskID, EditText log) {
		super();
		this.setTaskID(taskID);
		this.log = log;
	}

	@Override
	protected void firstPhase() {
		checkSuspend();
		matrix = Matrix.getRandomSquareMatrix(2, 0, 10);
	}

	@Override
	protected void secondPhase() {
		if (getOffloadingDecision() == OffloadingParams.OFFLOAD) {
			checkSuspend();
			this.dataStorage.setData(matrix.toString());
			setStatusFlag(TaskStatusParams.OFFLOADING);
			OffloadingManager.offload(this);
			suspend = true;
			checkSuspendNoStatus();
			log.post(new postToUI(log, "CPU: " + getCpuID() + " TaskID: "
					+ getTaskID() + " \n" + this.receivedData.getData() + "\n\n\n\n\n"));
			checkSuspend();
		} else if (getOffloadingDecision() == OffloadingParams.LOCAL) {
			for (int i = 0; i < matrix.getRow(); i++) {
				for (int j = 0; j < matrix.getCol(); j++) {
					matrix.setValueAtIndex(i, j, matrix.getValueAtIndex(i, j) * 2);
					checkSuspend();
				}
			}
			log.post(new postToUI(log, "CPU: " + getCpuID() + " TaskID: "
					+ getTaskID() + " \n" + this.matrix.toString() + "\n\n\n\n\n"));
			checkSuspend();
		}
	}

	@Override
	protected void thirdPhase() {

	}

	@Override
	public String getType() {
		return AppType.MatrixAddition;
	}

}
