package il.ac.shenkar.controller;

import com.squareup.otto.Subscribe;

import il.ac.shenkar.controller.application.ApplicationRoot;
import il.ac.shenkar.controller.application.User;
import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.outmessaging.events.ASyncGetUserInfoFailedEvent;
import il.ac.shenkar.controller.outmessaging.events.ASyncGetUserInfoSucceedEvent;
import il.ac.shenkar.model.TasksListModel;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends FragmentActivity implements OnClickListener {

	private BusProvider bus = BusProvider.getBusProvider();
	private TasksListModel tasksListModel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		tasksListModel = TasksListModel.getInstance(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.log_in_screen);
		Button logInBtn = (Button) findViewById(R.id.logInBtn);
		logInBtn.setOnClickListener(this);
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
	public void onASyncGetUserInfoFailedEvent(ASyncGetUserInfoFailedEvent e) {
	
		ApplicationRoot
				.showDialogErrorMessage(getSupportFragmentManager(),
						" Communication problem !",
						" Unfortunately, the Log In process has stopped due to communication failure.");
	}

	@Subscribe
	public void onASyncGetUserInfoSucceedEvent(ASyncGetUserInfoSucceedEvent e) {
		
		if (e.user == null) {
			Toast.makeText(
					this,
					"One of the details you entered is incorrect, please try again.",
					Toast.LENGTH_SHORT).show();
		} else {
			ApplicationRoot.user.setUserId(e.user.getUserId());
			ApplicationRoot.user.setUserPass(e.user.getUserPass());
			ApplicationRoot.user.setFirstName(e.user.getFirstName());
			ApplicationRoot.user.setLastName(e.user.getLastName());
			ApplicationRoot.user.setUserEmail(e.user.getUserEmail());
			ApplicationRoot.saveUser();
			ApplicationRoot.setFirstRun(false);
			Toast.makeText(this,
					" Hello again " + ApplicationRoot.user.getUserId() + ".",
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, MainTaskListActivity.class);
			startActivity(intent);
			finish();
		}
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
	public void onClick(View arg0) {
		String userId = ((EditText) findViewById(R.id.userIdLI)).getText()
				.toString();
		String userPass = ((EditText) findViewById(R.id.passLI)).getText()
				.toString();
		if (TextUtils.isEmpty(userId)) {
			Toast.makeText(LoginActivity.this, " Please Enter User Name !",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(userPass)) {
			Toast.makeText(LoginActivity.this, " Please Enter Password !",
					Toast.LENGTH_SHORT).show();
			return;
		}

		ApplicationRoot.user = new User(userId, userPass);
		tasksListModel.asyncUserAuthentication();
	}

}
