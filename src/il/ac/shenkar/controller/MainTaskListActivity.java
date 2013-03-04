package il.ac.shenkar.controller;

import il.ac.shenkar.controller.application.ApplicationRoot;
import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.mainListViewAdapter.ListsViewBaseAdapter;
import il.ac.shenkar.controller.outmessaging.RefreshTaskListService;
import il.ac.shenkar.controller.outmessaging.events.RefreshExceptionEvent;
import il.ac.shenkar.controller.outmessaging.events.RefreshTasksListEvent;
import il.ac.shenkar.controller.outmessaging.events.RemoveDoneTasksFailedEvent;
import il.ac.shenkar.controller.outmessaging.events.RemoveDoneTasksSucceedEvent;
import il.ac.shenkar.controller.outmessaging.events.ASyncTaskFailedEvent;
import il.ac.shenkar.controller.outmessaging.events.ASyncTaskSucceedEvent;
import il.ac.shenkar.model.TasksListModel;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

public class MainTaskListActivity extends FragmentActivity implements
		TextWatcher {
	private final String TAG = "il.ac.shenkar.controller.MainTaskListActivity";
	private ListsViewBaseAdapter baseAdapter = null;
	private TasksListModel tasksListModel = null;
	private ListView taskList = null;
	private BusProvider bus = BusProvider.getBusProvider();
	private static PendingIntent pendingIntentService = null;
	private int deleteActionId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, "Loading Main Application Activity");
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
		try {
			setRefreshTaskListServiceOn();
		} catch (RuntimeException e) {
			Log.e(TAG, " RuntimeException Loading Main Application Activity ! ");
		}
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

	private void setRefreshTaskListServiceOn() {
		Intent intent = new Intent(this, RefreshTaskListService.class);

		pendingIntentService = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), 10000, pendingIntentService);

	}

	private void setRefreshTaskListServiceOff() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntentService);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			setRefreshTaskListServiceOff();
		} catch (RuntimeException e) {
			Log.e(TAG, " onDestroy MainTaskListActivity RuntimeException");
			e.printStackTrace();
		}
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
			Log.i(TAG, " Refresh succeeded! ");
		}

	}

	@Subscribe
	public void onRefreshTaskListExcption(RefreshExceptionEvent e) {
		Log.i(TAG,
				" Refresh task list Exception Event occur , Refresh failed! ");
		ApplicationRoot
				.showDialogErrorMessage(getSupportFragmentManager(),
						" Communication problem !",
						" Unfortunately, the process has stopped due to communication failure.");

	}

	@Subscribe
	public void onSyncTaskFailedEvent(ASyncTaskFailedEvent e) {
		Log.i(TAG, " Sync Task to server failed ! ");
		ApplicationRoot
				.showDialogErrorMessage(
						getSupportFragmentManager(),
						" Communication problem !",
						" Unfortunately, synchronizing task process has stopped due to communication failure.");
	}

	@Subscribe
	public void onSyncTaskSucceedEvent(ASyncTaskSucceedEvent e) {
		Log.i(TAG, " Sync Task to server succeeded ! ");
		if (e.task != null) {
			tasksListModel.setTask(e.task);
			baseAdapter.notifyDataSetChanged();
			e.task = null;
		}
	}

	@Subscribe
	public void onRemoveDoneTasksSucceedEvent(RemoveDoneTasksSucceedEvent e) {
		Log.i(TAG, " Sync remove done Task from server succeeded ! ");
		switch (deleteActionId) {
		case R.id.delete_all_task_btn:
			tasksListModel.removeAllTasks();
			break;
		case R.id.delete_task_btn:
			tasksListModel.removeDoneTasks(baseAdapter.getDoneTasks());
			baseAdapter.clearDoneTask();
			break;
		}
		baseAdapter.notifyDataSetChanged();

	}

	@Subscribe
	public void onRemoveDoneTasksFailedEvent(RemoveDoneTasksFailedEvent e) {
		Log.i(TAG, " Sync Task to server failed ! ");
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