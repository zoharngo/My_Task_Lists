package il.org.shenkar.controller;

import com.google.analytics.tracking.android.EasyTracker;

import il.org.shenkar.controller.R;
import il.org.shenkar.controller.application.ApplicationRoot;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WelocomeActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (ApplicationRoot.isFirstRun()) {
			setContentView(R.layout.welcome);
			Button contBtn = (Button) findViewById(R.id.continueBtn);
			Button signUpBtn = (Button) findViewById(R.id.signUpBtn);
			TextView welcomeText = (TextView) findViewById(R.id.welcome_txt);
			Spanned text = Html.fromHtml(getString(R.string.welcome));
			welcomeText.setMovementMethod(LinkMovementMethod.getInstance());
			welcomeText.setText(text);
			contBtn.setOnClickListener(this);
			signUpBtn.setOnClickListener(this);
		} else {
			Intent intent = new Intent(this, MainTaskListActivity.class);
			startActivity(intent);
			finish();
		}
	}

	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.continueBtn:
			intent = new Intent(WelocomeActivity.this, LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.signUpBtn:
			intent = new Intent(WelocomeActivity.this, SignUpActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this); 
	}
	 @Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance().activityStart(this); 
	  }

}
