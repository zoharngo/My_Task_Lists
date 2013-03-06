package il.ac.shenkar.controller;

import il.ac.shenkar.controller.application.ApplicationRoot;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelocomeActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		if (ApplicationRoot.isFirstRun()) {
			setContentView(R.layout.welcome);
			Button contBtn = (Button) findViewById(R.id.continueBtn);
			Button signUpBtn = (Button) findViewById(R.id.signUpBtn);
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
		finish();
	}

}
