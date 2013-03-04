package il.ac.shenkar.controller.outmessaging;

import il.ac.shenkar.controller.application.ApplicationRoot;
import il.ac.shenkar.controller.application.User;
import il.ac.shenkar.model.Task;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class HttpClient {
	private static final String TAG = "il.ac.shenkar.controller.outmessaging.HttpClient";

	public static String getUserInfo(String URL) throws IOException,
			SQLException, GeneralSecurityException {
		String resultString = null;
		try {
			User user = ApplicationRoot.user;
			resultString = HttpClientUtils.sendPostRequest(user.toJSON(), URL);
			return resultString;
		} catch (IOException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		}
	}

	public static String deleteDoneTasks(String URL, JSONArray doneTasks)
			throws IOException, SQLException, JSONException, GeneralSecurityException {
		String resultString = null;
		try {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("userInfo", ApplicationRoot.user.toJSON());
			jsonObject.put("taskIdArr", doneTasks);
			resultString = HttpClientUtils.sendPostRequest(jsonObject, URL);
			ApplicationRoot.saveUserAgent(Long.valueOf(resultString));
			return resultString;
		} catch (IOException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		} catch (JSONException e) {
			throw e;
		}

	}

	public static String putTask(String URL, JSONObject jsonTask)
			throws IOException, SQLException, JSONException, GeneralSecurityException {
		String resultString = null;
		try {
			jsonTask.put("user", ApplicationRoot.user.toJSON());
			resultString = HttpClientUtils.sendPostRequest(jsonTask, URL);
			ApplicationRoot.saveUserAgent(Long.valueOf(resultString));
			return resultString;

		} catch (IOException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		} catch (JSONException e) {
			throw e;
		}

	}

	public static ArrayList<Task> getTasks(String URL) throws IOException,
			JSONException, SQLException, GeneralSecurityException {

		ArrayList<Task> tasks = null;
		try {

			User user = ApplicationRoot.user;
			String resultString = HttpClientUtils.sendPostRequest(
					user.toJSON(), URL);
			if (!(resultString.equals("UpdateNotNecessary"))) {
				JSONArray jsonArrayOfTasks = new JSONArray(resultString);
				JSONObject jsonTask;
				tasks = new ArrayList<Task>();
				for (int index = 0; index < jsonArrayOfTasks.length(); ++index) {
					Task task = new Task();
					jsonTask = (JSONObject) jsonArrayOfTasks.get(index);
					task.setTaskId(jsonTask.getLong("taskId"));
					task.setDescription(jsonTask.getString("description"));
					task.setLocation(jsonTask.getString("location"));
					task.setDate(jsonTask.getLong("date"));
					tasks.add(task);
				}

			} else {
				Log.i(TAG, "Update Not Necessary");
			}

		} catch (IOException e) {
			throw e;
		} catch (JSONException e) {
			throw e;
		} catch (SQLException e) {
			throw e;
		}
		return tasks;
	}

	public static String putUser(String URL) throws IOException, SQLException, GeneralSecurityException {
		String resultString = null;
		try {
			resultString = HttpClientUtils.sendPostRequest(
					ApplicationRoot.user.toJSON(), URL);
			return resultString;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;

		}

	}
}
