package il.org.shenkar.controller.outmessaging;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONException;
import il.org.shenkar.controller.bus.BusProvider;
import il.org.shenkar.controller.outmessaging.events.RefreshExceptionEvent;
import il.org.shenkar.controller.outmessaging.events.RefreshTasksListEvent;
import il.org.shenkar.model.Task;
import il.org.shenkar.model.TasksListModel;
import android.app.IntentService;
import android.content.Intent;

public class RefreshTaskListService extends IntentService {
	private BusProvider bus = BusProvider.getBusProvider();

	public RefreshTaskListService() {
		super("RefreshTaskListService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			TasksListModel tasksListModel = TasksListModel.getInstance(this);
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
