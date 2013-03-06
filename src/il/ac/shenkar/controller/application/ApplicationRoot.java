package il.ac.shenkar.controller.application;

import il.ac.shenkar.controller.DialogErrorMessage;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class ApplicationRoot extends Application {
	public static final String EXTRA_TITLE_ERROR_DIALOG = "il.ac.shenkar.controller.application.ApplicationRoot.EXTRA_TITLE_ERROR_DIALOG";
	public static final String EXTRA_MESSAGE_ERROR_DIALOG = "il.ac.shenkar.controller.application.ApplicationRoot.EXTRA_MESSAGE_ERROR_DIALOG";
	public static final String filename = "UserDetails";
	public static User user = null;
	private static SharedPreferences sp;

	@Override
	public void onCreate() {
		super.onCreate();	
		sp = getSharedPreferences(ApplicationRoot.filename, 0);
		if (!(isFirstRun())) {
			loadUser();
		}
	}

	public static Boolean isFirstRun() {
		int firstRun = sp.getInt("first_run", 0);
		if (firstRun == 1) {
			return false;
		} else
			return true;
	}

	public static void setFirstRun(Boolean status) {
		int firstRun;
		if (status) {
			firstRun = 0;
		} else {
			firstRun = 1;
		}
		Editor editor = sp.edit();
		editor.putInt("first_run", firstRun);
		editor.commit();
	}

	public static void saveUser() {
		Editor editor = sp.edit();
		editor.putString("userId", user.getUserId());
		editor.putString("firstName", user.getFirstName());
		editor.putString("lastName", user.getLastName());
		editor.putString("userEmail", user.getUserEmail());
		editor.putString("userPass", user.getUserPass());
		editor.putLong("userAgent", user.getUserAgent());
		editor.commit();
	}

	public static void saveUserAgent(long userAgent) {
		Editor editor = sp.edit();
		editor.putLong("userAgent", userAgent);
		user.setUserAgent(userAgent);
		editor.commit();
	}

	public static void loadUser() {
		String userId = sp.getString("userId", "none");
		String firstName = sp.getString("firstName", "none");
		String lastName = sp.getString("lastName", "none");
		String userPass = sp.getString("userPass", "none");
		String userEmail = sp.getString("userEmail", "none");
		long userAgent = sp.getLong("userAgent", 1L);
		user = new User(userId, firstName, lastName, userPass, userEmail,
				userAgent);
	}

	public static void showDialogErrorMessage(FragmentManager fragmentManager,
			String title, String meesage) {
		DialogFragment dialogMessage = new DialogErrorMessage();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_TITLE_ERROR_DIALOG, title);
		bundle.putString(EXTRA_MESSAGE_ERROR_DIALOG, meesage);
		dialogMessage.setArguments(bundle);
		dialogMessage.show(fragmentManager, "missiles");
	}

}
