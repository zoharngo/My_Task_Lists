package il.ac.shenkar.controller.outmessaging;

import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.outmessaging.events.ASyncNewUserFailedEvent;
import il.ac.shenkar.controller.outmessaging.events.ASyncNewUserSucceedEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import android.os.AsyncTask;


public class AsyncUserToServer extends AsyncTask<String, String, String> {

	private BusProvider bus = BusProvider.getBusProvider();

	public AsyncUserToServer() {
		super();
	}

	@Override
	protected String doInBackground(String... params) {

		try {
			
			String result = HttpClient.putUser(params[0]);
			bus.post(new ASyncNewUserSucceedEvent(result));
			return result;
		} catch (IOException e) {
			bus.post(new ASyncNewUserFailedEvent(e.getMessage()));		
			return "IOException";
		} catch (SQLException e) {
			bus.post(new ASyncNewUserFailedEvent(e.getMessage()));		
			return "SQLException";
		} catch (GeneralSecurityException e) {
			bus.post(new ASyncNewUserFailedEvent(e.getMessage()));			
			return "GeneralSecurityException";
		}

	}

}
