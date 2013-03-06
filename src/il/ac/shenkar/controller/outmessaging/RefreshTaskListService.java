package il.ac.shenkar.controller.outmessaging;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONException;
import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.outmessaging.events.RefreshExceptionEvent;
import il.ac.shenkar.controller.outmessaging.events.RefreshTasksListEvent;
import il.ac.shenkar.model.Task;
import il.ac.shenkar.model.TasksListModel;
import android.app.IntentService;
import android.content.Intent;


public class RefreshTaskListService extends IntentService {
	private BusProvider bus = BusProvider.getBusProvider();
	private TasksListModel tasksListModel = TasksListModel
			.getInstance(getBaseContext());

	public RefreshTaskListService() {
		super("RefreshTaskListService");	
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			ArrayList<Task> refreshedTask = tasksListModel.refreshTasksList();
			bus.post(new RefreshTasksListEvent(refreshedTask));
		} catch (IOException e) {		
			bus.post(new RefreshExceptionEvent("IOException"));
		} catch (JSONException e) {	
			bus.post(new RefreshExceptionEvent("JSONException"));
		} catch (SQLException e) {	
			bus.post(new RefreshExceptionEvent("SQLException"));
		} catch (GeneralSecurityException e) {
			bus.post(new RefreshExceptionEvent("GeneralSecurityException"));
		}

	}

}
