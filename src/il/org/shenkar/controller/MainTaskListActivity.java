package il.org.shenkar.controller;

import java.util.ArrayList;

import il.org.shenkar.controller.R;
import il.org.shenkar.controller.application.ApplicationRoot;
import il.org.shenkar.controller.bus.BusProvider;
import il.org.shenkar.controller.mainListViewAdapter.ListsViewBaseAdapter;
import il.org.shenkar.controller.outmessaging.RefreshTaskListService;
import il.org.shenkar.controller.outmessaging.events.ASyncTaskFailedEvent;
import il.org.shenkar.controller.outmessaging.events.ASyncTaskSucceedEvent;
import il.org.shenkar.controller.outmessaging.events.RefreshExceptionEvent;
import il.org.shenkar.controller.outmessaging.events.RefreshTasksListEvent;
import il.org.shenkar.controller.outmessaging.events.RemoveDoneTasksFailedEvent;
import il.org.shenkar.controller.outmessaging.events.RemoveDoneTasksSucceedEvent;
import il.org.shenkar.model.Task;
import il.org.shenkar.model.TasksListModel;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

public class MainTaskListActivity extends FragmentActivity implements
		TextWatcher {

	private ListsViewBaseAdapter baseAdapter = null;
	private TasksListModel tasksListModel = null;
	private ListView taskList = null;
	private BusProvider bus = BusProvider.getBusProvider();
	private PendingIntent pendingIntentService = null;
	private int deleteActionId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

	
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Hello,  " + ApplicationRoot.user.getUserId());
		this.setContentView(R.layout.main_task_lists_activity);
		tasksListModel = TasksListModel.getInstance(this);

		EditText searchBar = (EditText) findViewById(R.id.search_bar);
		searchBar.addTextChangedListener(this);
		baseAdapter = new ListsViewBaseAdapter(this);
		taskList = (ListView) findViewById(R.id.listV_main);
		taskList.setTextFilterEnabled(true);
		taskList.setAdapter(baseAdapter);
		
			setRefreshTaskListServiceOn();
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.action_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.add_task_btn:
			Intent intent_0 = new Intent(this, AddNewTaskActivity.class);
			startActivity(intent_0);
			break;
		case R.id.delete_task_btn:
			deleteActionId = R.id.delete_task_btn;
			tasksListModel.asyncDeleteTaskActionToServer(baseAdapter
					.getDoneTasks());
			break;
		case R.id.delete_all_task_btn:
			deleteActionId = R.id.delete_all_task_btn;
			tasksListModel.asyncDeleteTaskActionToServer(tasksListModel
					.getTaskList());
			break;
		case R.id.logOutBtn:
			ApplicationRoot.setFirstRun(true);
			Intent intent_1 = new Intent(this, LoginActivity.class);
			startActivity(intent_1);
			finish();
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	private void removeAlarms(ArrayList<Task> removedTasks) {

		for (Task task : removedTasks) {
			if (task.getTimeAlarm() != null) {
				task.getTimeAlarm().cancel();
			}
			if (task.getGeoAlarm() != null) {
				task.getGeoAlarm().cancel();
			}
		}

	}

	private void setRefreshTaskListServiceOn() {
		Intent intent = new Intent(getApplicationContext(), RefreshTaskListService.class);

		pendingIntentService = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), 10000, pendingIntentService);

	}

	private void setRefreshTaskListServiceOff() {	
		pendingIntentService.cancel();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setRefreshTaskListServiceOff();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		bus.register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		bus.unregister(this);
	}

	@Subscribe
	public void refreshTaskList(RefreshTasksListEvent e) {
		if (e.tasks != null) {
			tasksListModel.setTaskList(e.tasks);
			baseAdapter.notifyDataSetChanged();
		}

	}

	@Subscribe
	public void onRefreshTaskListExcption(RefreshExceptionEvent e) {
		ApplicationRoot
				.showDialogErrorMessage(getSupportFragmentManager(),
						" Communication problem !",
						" Unfortunately, the process has stopped due to communication failure.");

	}

	@Subscribe
	public void onSyncTaskFailedEvent(ASyncTaskFailedEvent e) {
	
		ApplicationRoot
				.showDialogErrorMessage(
						getSupportFragmentManager(),
						" Communication problem !",
						" Unfortunately, synchronizing task process has stopped due to communication failure.");
	}

	@Subscribe
	public void onSyncTaskSucceedEvent(ASyncTaskSucceedEvent e) {
	
		if (e.task != null) {
			tasksListModel.setTask(e.task);
			baseAdapter.notifyDataSetChanged();
			e.task = null;
		}
	}

	@Subscribe
	public void onRemoveDoneTasksSucceedEvent(RemoveDoneTasksSucceedEvent e) {
	
		switch (deleteActionId) {
		case R.id.delete_all_task_btn:
			removeAlarms(tasksListModel.getTaskList());
			tasksListModel.removeAllTasks();
			break;
		case R.id.delete_task_btn:
			tasksListModel.removeDoneTasks(baseAdapter.getDoneTasks());
			removeAlarms(baseAdapter.getDoneTasks());
			baseAdapter.clearDoneTask();
			break;
		}
		baseAdapter.notifyDataSetChanged();

	}

	@Subscribe
	public void onRemoveDoneTasksFailedEvent(RemoveDoneTasksFailedEvent e) {
	
		ApplicationRoot
				.showDialogErrorMessage(
						getSupportFragmentManager(),
						" Communication problem !",
						" Unfortunately, synchronizing remove tasks process has stopped due to communication failure.");
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		baseAdapter.getFilter().filter(s);
		baseAdapter.notifyDataSetChanged();
	}

}