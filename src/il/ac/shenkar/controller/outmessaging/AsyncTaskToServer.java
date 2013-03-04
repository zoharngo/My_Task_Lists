package il.ac.shenkar.controller.outmessaging;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

import org.json.JSONException;

import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.outmessaging.events.ASyncTaskFailedEvent;
import il.ac.shenkar.controller.outmessaging.events.ASyncTaskSucceedEvent;
import il.ac.shenkar.model.Task;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskToServer extends AsyncTask<String, String, String> {
	private static final String TAG = "il.ac.shenkar.controller.outmessaging.AsyncTaskToServer";
	private final Task task;
	private BusProvider bus = BusProvider.getBusProvider();

	public AsyncTaskToServer(Task task) {
		super();
		this.task = task;
	}

	@Override
	protected String doInBackground(String... params) {

		try {
			Log.i(TAG, "Sync task to server start");
			String response = HttpClient.putTask(params[0], task.toJSON());
			bus.post(new ASyncTaskSucceedEvent(task));
			return response;
		} catch (IOException e) {
			bus.post(new ASyncTaskFailedEvent(e.getMessage()));
			Log.e(TAG,
					"IOException occur while trying to sync new task to server");
			return "IOException";
		} catch (SQLException e) {
			bus.post(new ASyncTaskFailedEvent(e.getMessage()));
			Log.e(TAG,
					"SQLException occur while trying to sync new task to server");
			return "SQLException";
		} catch (JSONException e) {
			bus.post(new ASyncTaskFailedEvent(e.getMessage()));
			Log.e(TAG,
					"JSONException occur while trying to sync new task to server");
			return "JSONException";
		} catch (GeneralSecurityException e) {
			bus.post(new ASyncTaskFailedEvent(e.getMessage()));
			Log.e(TAG,
					"GeneralSecurityException occur while trying to sync new task to server");
			return "GeneralSecurityException";
		}

	}

	@Override
	protected void onPostExecute(String responseStatus) {
		Log.i(TAG, "Server response: " + responseStatus);
	}

}
