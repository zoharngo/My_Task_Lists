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


public class AsyncTaskToServer extends AsyncTask<String, String, String> {
	
	private final Task task;
	private BusProvider bus = BusProvider.getBusProvider();

	public AsyncTaskToServer(Task task) {
		super();
		this.task = task;
	}

	@Override
	protected String doInBackground(String... params) {

		try {
		
			String response = HttpClient.putTask(params[0], task.toJSON());
			bus.post(new ASyncTaskSucceedEvent(task));
			return response;
		} catch (IOException e) {
			bus.post(new ASyncTaskFailedEvent(e.getMessage()));
			
			return "IOException";
		} catch (SQLException e) {
			bus.post(new ASyncTaskFailedEvent(e.getMessage()));
		
			return "SQLException";
		} catch (JSONException e) {
			bus.post(new ASyncTaskFailedEvent(e.getMessage()));
			
			return "JSONException";
		} catch (GeneralSecurityException e) {
			bus.post(new ASyncTaskFailedEvent(e.getMessage()));
			
			return "GeneralSecurityException";
		}

	}

	

}
