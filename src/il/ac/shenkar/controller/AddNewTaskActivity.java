package il.ac.shenkar.controller;

import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.notifications.NotificationReceiver;
import il.ac.shenkar.controller.outmessaging.AsyncAddressSearch;
import il.ac.shenkar.controller.outmessaging.events.AsyncFindAddressSucceedEvent;
import il.ac.shenkar.model.Task;
import il.ac.shenkar.model.TasksListModel;
import java.util.Calendar;
import com.squareup.otto.Subscribe;
import android.app.AlarmManager;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.PendingIntent;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AddNewTaskActivity extends FragmentActivity implements
		OnClickListener, OnDateSetListener, OnTimeSetListener,
		OnCheckedChangeListener, OnItemClickListener {
	public final static String TASK_EXTRA = "il.ac.shenkar.controller.TASK";
	public final static String FIND_ADDRESSS = "il.ac.shenkar.controller.FIND_ADDRESSS";
	private final static String TAG = "controller_AddNewTaskActivity";
	private DialogFragment findAddressDialog = null;
	private TasksListModel tasksListModel = null;
	private EditText editDescription = null;
	private EditText editlocation = null;
	private ToggleButton toggleTimeAlarm = null;
	private ToggleButton toggleLocationAlarm = null;
	private TextView alarmTimeTextView;
	private TextView alarmLocationTextView;
	private TextView timeTextView = null;
	private TextView dateTextView = null;
	private int timePickerHour = 0;
	private int timePickerMinute = 0;
	private int datePickerDayOfMounth = 0;
	private int datePickerMounth = 0;
	private int datePickerYear = 0;

	private BusProvider bus = BusProvider.getBusProvider();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_task);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		tasksListModel = TasksListModel.getInstance(this);
		Button timeSetBtn = (Button) findViewById(R.id.setDateBtn);
		Button dateSetBtn = (Button) findViewById(R.id.setTimeBtn);
		Button findAddressBtn = (Button) findViewById(R.id.findAddressBtn);

		timeSetBtn.setOnClickListener(this);
		dateSetBtn.setOnClickListener(this);
		findAddressBtn.setOnClickListener(this);

		alarmTimeTextView = (TextView) findViewById(R.id.alarm_time_set);
		alarmLocationTextView = (TextView) findViewById(R.id.alarm_location_set);
		timeTextView = (TextView) findViewById(R.id.time_of_task);
		dateTextView = (TextView) findViewById(R.id.date_of_task);

		editDescription = (EditText) findViewById(R.id.edit_description);
		editlocation = (EditText) findViewById(R.id.geo_location);

		toggleTimeAlarm = (ToggleButton) findViewById(R.id.button_set_time_alarm);
		toggleLocationAlarm = (ToggleButton) findViewById(R.id.button_set_location_alarm);
		toggleTimeAlarm.setOnCheckedChangeListener(this);
		toggleLocationAlarm.setOnCheckedChangeListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.add_new_task_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.save_btn:
			saveNewTask();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveNewTask() {

		Log.d(TAG, "Adding New Task: '" + editDescription.getText().toString()
				+ "' ");
		Task task = new Task();
		task.setTaskId(System.currentTimeMillis());
		task.setDescription(editDescription.getText().toString());
		task.setLocation(editlocation.getText().toString());
		task.setDate(getToDoWhen());

		if (toggleTimeAlarm.isChecked()) {
			long wakeUptimeInterval = getToDoWhen()
					- System.currentTimeMillis();

			if (wakeUptimeInterval > 0) {
				Intent intent = new Intent(AddNewTaskActivity.this,
						NotificationReceiver.class);

				intent.putExtra(TASK_EXTRA, editDescription.getText()
						.toString());

				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(),
						Integer.valueOf((int) wakeUptimeInterval), intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

				alarmManager.set(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis() + wakeUptimeInterval,
						pendingIntent);

			}
		}
		tasksListModel.asyncTaskToServer(task);
		finish();
	}

	private long getToDoWhen() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, timePickerHour);
		calendar.set(Calendar.MINUTE, timePickerMinute);
		calendar.set(Calendar.DAY_OF_MONTH, datePickerDayOfMounth);
		calendar.set(Calendar.MONTH, datePickerMounth);
		calendar.set(Calendar.YEAR, datePickerYear);
		return calendar.getTimeInMillis();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setDateBtn:
			DialogFragment dateFragment = new DatePickerFragment();
			dateFragment.show(getSupportFragmentManager(), "datePicker");
			break;
		case R.id.setTimeBtn:
			DialogFragment timeFragment = new TimePickerFragment();
			timeFragment.show(getSupportFragmentManager(), "timePicker");
			break;
		case R.id.findAddressBtn:
			AsyncAddressSearch addressSearch = new AsyncAddressSearch(this);
			String addressText = editlocation.getText().toString();
			addressSearch.execute(addressText);
			break;
		}

	}

	@Subscribe
	public void onAsyncFindAddressSucceedEvent(AsyncFindAddressSucceedEvent e) {

		findAddressDialog = new AddressDialogFragment();
		Bundle bundle = new Bundle();
		if (!(e.address.isEmpty())) {
			bundle.putStringArrayList(FIND_ADDRESSS, e.address);
			findAddressDialog.setArguments(bundle);
			findAddressDialog.show(getSupportFragmentManager(), "find address");
		} else {
			Toast.makeText(this, "No Results!.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Log.i(TAG, "Date is set!.");
		dateTextView.setText("On, " + dayOfMonth + "/" + (monthOfYear + 1)
				+ "/" + year + ".");
		datePickerDayOfMounth = dayOfMonth;
		datePickerMounth = monthOfYear;
		datePickerYear = year;
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Log.i(TAG, "Time is set!.");
		String hourStr = null;
		String minuteStr = null;
		String am_pm = null;
		if (minute < 10) {
			minuteStr = "0" + minute;
		} else {
			minuteStr = "" + minute;
		}
		if (hourOfDay < 10) {
			hourStr = "0" + hourOfDay;
		} else {
			hourStr = "" + hourOfDay;
		}
		if (hourOfDay > 11) {
			am_pm = "PM";
		} else {
			am_pm = "AM";
		}
		timeTextView.setText("At " + hourStr + ":" + minuteStr + " " + am_pm
				+ ".");
		timePickerHour = hourOfDay;
		timePickerMinute = minute;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.button_set_time_alarm:
			if (isChecked) {
				alarmTimeTextView.setText(getString(R.string.alarm_time_set)
						+ " On.");
			} else {
				alarmTimeTextView.setText(getString(R.string.alarm_time_set)
						+ " Off.");
			}
			break;
		case R.id.button_set_location_alarm:
			if (isChecked) {
				alarmLocationTextView
						.setText(getString(R.string.alarm_time_set) + " On.");
			} else {
				alarmLocationTextView
						.setText(getString(R.string.alarm_time_set) + " Off.");
			}

			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String citiy = (String) arg0.getItemAtPosition(arg2);
		editlocation.setText(citiy);
		findAddressDialog.dismiss();
	}

	@Override
	protected void onPause() {
		super.onPause();
		bus.unregister(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		bus.register(this);
	}

}
