package il.ac.shenkar.controller.outmessaging;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.outmessaging.events.RemoveDoneTasksFailedEvent;
import il.ac.shenkar.controller.outmessaging.events.RemoveDoneTasksSucceedEvent;
import il.ac.shenkar.model.Task;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncDeleteTaskFromServer extends
		AsyncTask<String, String, String> {
	private static final String TAG = "il.ac.shenkar.controller.outmessaging.AsyncDeleteTaskFromServer";
	private final ArrayList<Task> doneTasks;
	private BusProvider bus = BusProvider.getBusProvider();

	public AsyncDeleteTaskFromServer(ArrayList<Task> doneTask) {
		super();
		this.doneTasks = doneTask;
	}

	@Override
	protected String doInBackground(String... arg) {

		try {
			JSONArray doneTasksId = new JSONArray();
			for (Task task : doneTasks) {
				doneTasksId.put(task.getTaskId());
			}

			Log.i(TAG, "Delete done task from server start! ");
			String response = HttpClient.deleteDoneTasks(arg[0], doneTasksId);
			bus.post(new RemoveDoneTasksSucceedEvent());
			return response;
		} catch (IOException e) {
			bus.post(new RemoveDoneTasksFailedEvent(e.getMessage()));
			Log.e(TAG,
					"IOException occur while trying to sync delete done task from server");
			return "IOException";
		} catch (SQLException e) {
			bus.post(new RemoveDoneTasksFailedEvent(e.getMessage()));
			Log.e(TAG,
					"SQLException occur while trying to sync delete done task from server");
			return "SQLException";
		} catch (JSONException e) {
			bus.post(new RemoveDoneTasksFailedEvent(e.getMessage()));
			Log.e(TAG,
					"JSONException occur while trying to sync delete done task from server");
			return "JSONException";
		} catch (GeneralSecurityException e) {
			bus.post(new RemoveDoneTasksFailedEvent(e.getMessage()));
			Log.e(TAG,
					"GeneralSecurityException occur while trying to sync delete done task from server");
			return "GeneralSecurityException";
		}

	}

}
