package il.org.shenkar.controller;


import java.util.regex.Pattern;
import com.squareup.otto.Subscribe;

import il.org.shenkar.controller.R;
import il.org.shenkar.controller.application.ApplicationRoot;
import il.org.shenkar.controller.application.User;
import il.org.shenkar.controller.bus.BusProvider;
import il.org.shenkar.controller.outmessaging.events.ASyncNewUserFailedEvent;
import il.org.shenkar.controller.outmessaging.events.ASyncNewUserSucceedEvent;
import il.org.shenkar.model.Task;
import il.org.shenkar.model.TasksListModel;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends FragmentActivity implements
		OnClickListener, OnFocusChangeListener {

	private BusProvider bus = BusProvider.getBusProvider();
	private TasksListModel tasksListModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		tasksListModel = TasksListModel.getInstance(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.sign_up_screen);
		Button createBtn = (Button) findViewById(R.id.createAccountBtn);
		createBtn.setOnClickListener(this);
		EditText userIdText = (EditText) findViewById(R.id.userId);
		userIdText.setOnFocusChangeListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		String userName = ((EditText) v).getText().toString();
		if (hasFocus && TextUtils.isEmpty(userName)) {
			String firstNameText = ((EditText) findViewById(R.id.firstName))
					.getText().toString();
			String LastNameText = ((EditText) findViewById(R.id.lastName))
					.getText().toString();
			if (!(TextUtils.isEmpty(firstNameText))
					&& !(TextUtils.isEmpty(LastNameText))) {
				StringBuffer stringBuffer = new StringBuffer(firstNameText)
						.append(".").append(LastNameText);
				((EditText) v).setText(stringBuffer.toString());
			}

		}
	}

	@Subscribe
	public void onSyncNewUserSucceedEvent(ASyncNewUserSucceedEvent e) {
		if (e.result.contains("PRIMARY")) {
			Toast.makeText(SignUpActivity.this,
					" User Name already exist try different user name !",
					Toast.LENGTH_SHORT).show();
		} else if (e.result.contains("userEmail")) {
			Toast.makeText(SignUpActivity.this,
					" Exist Account already found on this email address !",
					Toast.LENGTH_SHORT).show();
		} else if (e.result.contains("CREATED")) {
			String pass = e.result.split(":")[1];
			ApplicationRoot.user.setUserPass(pass);
			ApplicationRoot.saveUser();
			ApplicationRoot.setFirstRun(false);
			Toast.makeText(this, " Account successfully created !",
					Toast.LENGTH_LONG).show();
			Toast.makeText(this, " Soo start to remember your tasks !",
					Toast.LENGTH_LONG).show();

			Intent intent = new Intent(this, MainTaskListActivity.class);
			startActivity(intent);

			StringBuilder builderFirstTask = new StringBuilder();
			builderFirstTask.append(getString(R.string.hello))
					.append(" " + ApplicationRoot.user.getFirstName() + ",\n")
					.append(getString(R.string.first_task));
			Task task = new Task(System.currentTimeMillis(),
					builderFirstTask.toString(), "", -1);
			tasksListModel.asyncTaskToServer(task);

			finish();
		}
	}

	@Subscribe
	public void onSyncNewUserFailedEvent(ASyncNewUserFailedEvent e) {
		ApplicationRoot
				.showDialogErrorMessage(getSupportFragmentManager(),
						" Communication problem !",
						" Unfortunately, the Sign Up process has stopped due to communication failure.");
	}

	@Override
	public void onClick(View v) {
		String firstName = ((EditText) findViewById(R.id.firstName)).getText()
				.toString();
		String lastName = ((EditText) findViewById(R.id.lastName)).getText()
				.toString();
		String userId = ((EditText) findViewById(R.id.userId)).getText()
				.toString();
		String userEmail = ((EditText) findViewById(R.id.email)).getText()
				.toString();
		String userPass = ((EditText) findViewById(R.id.pass)).getText()
				.toString();
		if (TextUtils.isEmpty(firstName)) {
			Toast.makeText(SignUpActivity.this, " Please Enter First Name !",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(lastName)) {
			Toast.makeText(SignUpActivity.this, " Please Enter Last Name !",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(userId)) {
			Toast.makeText(SignUpActivity.this, " Please Enter User Name !",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(userEmail)) {
			Toast.makeText(SignUpActivity.this, " Please Enter Email Adress !",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (TextUtils.isEmpty(userPass)) {
			Toast.makeText(SignUpActivity.this, " Please Enter Password !",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (!(TextUtils.isEmpty(userEmail))) {
			Pattern pattern = Patterns.EMAIL_ADDRESS;
			if (!(pattern.matcher(userEmail).matches())) {
				Toast.makeText(SignUpActivity.this,
						" Please Enter vaild Email Adress !",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		ApplicationRoot.user = new User(userId, firstName, lastName, userPass,
				userEmail);
		tasksListModel.asyncNewUserOnServer();

	}
}
