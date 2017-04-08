package com.ossimulator.offloadingos;

import com.ossimulator.offloadingos.scheduler.GedfScheduler;
import com.ossimulator.offloadingos.scheduler.Scheduler;
import com.ossimulator.offloadingos.scheduler.SimpleScheduler;
import com.ossimulator.offloadingos.task.*;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends ActionBarActivity {
	Scheduler scheduler;
	EditText log;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		log = (EditText) findViewById(R.id.log);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void start(View view) {
		end(view);
		log.setText("");
		try {
			int cores = Integer.parseInt(((EditText) findViewById(R.id.cpuNum))
					.getText().toString());
			int taskNum = Integer
					.parseInt(((EditText) findViewById(R.id.taskNum)).getText()
							.toString());
			scheduler = new GedfScheduler(cores);
			for (int i = 0; i < taskNum; i++) {
				// Task example = new AndroidExampleTask2(i, log);
				Task example = new SimpleMatrixTask(i, log);
				scheduler.addTask(example);
			}
			scheduler.start();
		} catch (Exception e) {
			log.setText(e.getMessage());
		}
	}

	public void end(View v) {
		if (scheduler != null) {
			scheduler.stopScheduling();
			scheduler = null;
		}
	}
}
