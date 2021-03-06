package il.org.shenkar.controller.outmessaging;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import il.org.shenkar.controller.bus.BusProvider;
import il.org.shenkar.controller.outmessaging.events.RemoveDoneTasksFailedEvent;
import il.org.shenkar.controller.outmessaging.events.RemoveDoneTasksSucceedEvent;
import il.org.shenkar.model.Task;
import android.os.AsyncTask;


public class AsyncDeleteTaskFromServer extends
		AsyncTask<String, String, String> {
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
			String response = HttpClient.deleteDoneTasks(arg[0], doneTasksId);
			bus.post(new RemoveDoneTasksSucceedEvent());
			return response;
		} catch (IOException e) {
			bus.post(new RemoveDoneTasksFailedEvent(e.getMessage()));			
			return "IOException";
		} catch (SQLException e) {
			bus.post(new RemoveDoneTasksFailedEvent(e.getMessage()));		
			return "SQLException";
		} catch (JSONException e) {
			bus.post(new RemoveDoneTasksFailedEvent(e.getMessage()));			
			return "JSONException";
		} catch (GeneralSecurityException e) {
			bus.post(new RemoveDoneTasksFailedEvent(e.getMessage()));
			return "GeneralSecurityException";
		}

	}

}
