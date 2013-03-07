package il.org.shenkar.controller.outmessaging;

import il.org.shenkar.controller.application.User;
import il.org.shenkar.controller.bus.BusProvider;
import il.org.shenkar.controller.outmessaging.events.ASyncGetUserInfoFailedEvent;
import il.org.shenkar.controller.outmessaging.events.ASyncGetUserInfoSucceedEvent;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;


public class AsyncGetUserInfo extends AsyncTask<String, String, String> {


	private BusProvider bus = BusProvider.getBusProvider();

	public AsyncGetUserInfo() {
		super();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
		
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
			
			return "IOException";
		} catch (SQLException e) {
			bus.post(new ASyncGetUserInfoFailedEvent(e.getMessage()));
			
			return "SQLException";
		} catch (JSONException e) {
			bus.post(new ASyncGetUserInfoFailedEvent(e.getMessage()));
		
			return "JSONException";
		} catch (GeneralSecurityException e) {
			bus.post(new ASyncGetUserInfoFailedEvent(e.getMessage()));
			
			return "GeneralSecurityException";
		}

	}

}
