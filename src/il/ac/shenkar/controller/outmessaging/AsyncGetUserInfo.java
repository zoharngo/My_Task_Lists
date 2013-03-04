package il.ac.shenkar.controller.outmessaging;

import il.ac.shenkar.controller.application.User;
import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.outmessaging.events.ASyncGetUserInfoFailedEvent;
import il.ac.shenkar.controller.outmessaging.events.ASyncGetUserInfoSucceedEvent;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncGetUserInfo extends AsyncTask<String, String, String> {

	private static final String TAG = "il.ac.shenkar.controller.outmessaging.AsyncGetUserInfo";
	private BusProvider bus = BusProvider.getBusProvider();

	public AsyncGetUserInfo() {
		super();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			Log.i(TAG, "Getting User Info from server start!.");
			String result = HttpClient.getUserInfo(params[0]);
			if (result.contains("User not found!.")) {
				bus.post(new ASyncGetUserInfoSucceedEvent(null));
			} else {
				JSONObject jsonUser = new JSONObject(result);
				User user = new User();
				user.setUserId(jsonUser.getString("userId"));
				user.setUserPass(jsonUser.getString("userPass"));
				user.setFirstName(jsonUser.getString("firstName"));
				user.setLastName(jsonUser.getString("lastName"));
				user.setUserEmail(jsonUser.getString("userEmail"));
				bus.post(new ASyncGetUserInfoSucceedEvent(user));
			}
			return result;
		} catch (IOException e) {
			bus.post(new ASyncGetUserInfoFailedEvent(e.getMessage()));
			Log.e(TAG,
					"IOException occur while trying to get User Info from server!.");
			return "IOException";
		} catch (SQLException e) {
			bus.post(new ASyncGetUserInfoFailedEvent(e.getMessage()));
			Log.e(TAG,
					"IOException occur while trying to get User Info from server!");
			return "SQLException";
		} catch (JSONException e) {
			bus.post(new ASyncGetUserInfoFailedEvent(e.getMessage()));
			Log.e(TAG,
					"JSONException occur while trying to get User Info from server!");
			return "JSONException";
		} catch (GeneralSecurityException e) {
			bus.post(new ASyncGetUserInfoFailedEvent(e.getMessage()));
			Log.e(TAG,
					"GeneralSecurityException occur while trying to get User Info from server!");
			return "GeneralSecurityException";
		}

	}

}
