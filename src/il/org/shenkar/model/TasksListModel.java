package il.org.shenkar.model;
import il.org.shenkar.controller.outmessaging.AsyncDeleteTaskFromServer;
import il.org.shenkar.controller.outmessaging.AsyncGetUserInfo;
import il.org.shenkar.controller.outmessaging.AsyncTaskToServer;
import il.org.shenkar.controller.outmessaging.AsyncUserToServer;
import il.org.shenkar.controller.outmessaging.HttpClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import org.json.JSONException;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


public class TasksListModel {

	private static TasksListModel taskListModel = null;
	private ArrayList<Task> tasksList = null;

	private DatabaseHandler databaseHandler = null;

	private TasksListModel(Context context) {
		tasksList = new ArrayList<Task>();
		databaseHandler = new DatabaseHandler(context);
		tasksList = databaseHandler.getAllTaskList();
	}

	public static TasksListModel getInstance(Context context) {
		if (taskListModel == null) {
			taskListModel = new TasksListModel(context);
		}
		return taskListModel;
	}

	public void setSearcResult(ArrayList<Task> searcResult) {
		tasksList = searcResult;
	}

	public void removeSearcResult() {
		tasksList = getTaskList();
	}

	public int getSize() {
		return tasksList.size();
	}

	public ArrayList<Task> getTaskList() {
		return databaseHandler.getAllTaskList();
	}

	public void removeAllTasks() {
		databaseHandler.removeDoneTasks(tasksList);
		tasksList.clear();
	}

	public boolean isEmptyList() {
		return tasksList.isEmpty();
	}

	public Task getTask(int position) {
		return tasksList.get(getSize() - position - 1);
	}

	public void removeDoneTasks(ArrayList<Task> doneTasks) {
		tasksList.removeAll(doneTasks);
		databaseHandler.removeDoneTasks(doneTasks);

	}

	public void asyncTaskToServer(Task task) {
		AsyncTaskToServer taskToServer = new AsyncTaskToServer(task);
		taskToServer
				.execute("http://restwebservice.cloudapp.net/myToDoListWebApp/rest/tasks/put_task");
	}

	public void asyncDeleteTaskActionToServer(ArrayList<Task> taskToDelete) {
		AsyncDeleteTaskFromServer deleteTaskFromServer = new AsyncDeleteTaskFromServer(
				taskToDelete);
		deleteTaskFromServer
				.execute("http://restwebservice.cloudapp.net/myToDoListWebApp/rest/tasks/delete_tasks/tasks_id_arr");
	}

	public ArrayList<Task> refreshTasksList() throws IOException,
			JSONException, java.sql.SQLException, GeneralSecurityException {
		ArrayList<Task> tasks = HttpClient
				.getTasks("http://restwebservice.cloudapp.net/myToDoListWebApp/rest/tasks");
		return tasks;
	}

	public void asyncUserAuthentication() {
		AsyncGetUserInfo userToServer = new AsyncGetUserInfo();
		userToServer
				.execute("http://restwebservice.cloudapp.net/myToDoListWebApp/rest/tasks/users/get_user");
	}

	public void asyncNewUserOnServer() {
		AsyncUserToServer userToServer = new AsyncUserToServer();
		userToServer
				.execute("http://restwebservice.cloudapp.net/myToDoListWebApp/rest/tasks/users/put_user/user");
	}

	public void setTask(Task task) {// Change task Id to current time stamp
		tasksList.add(task);
		databaseHandler.addTask(task);
	}

	public void setTaskList(ArrayList<Task> tasks) {
		tasksList = tasks;
		databaseHandler.replaceAllTaskList(tasksList);
	}

	private static class DatabaseHandler extends SQLiteOpenHelper {
		
		public static final String KEY_TASK_ID = "id";
		public static final String KEY_DESCRIPTION = "description";
		public static final String KEY_LOCATION = "location";
		public static final String KEY_DATE = "date";

		private static final String DATABASE_NAME = "TaskListDB";
		private static final String DATABASE_TABLE = "Tasks";
		private static final int DATABASE_VERSION = 3;

		private static final String DATABASE_CREATE = "create table if not exists Tasks ("
				+ "id long,"
				+ "description VARCHAR,location VARCHAR,date long,"
				+ "CONSTRAINT Tasks_pk PRIMARY KEY (id)); ";

		public DatabaseHandler(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) throws SQLiteException {
			try {

				db.execSQL(DATABASE_CREATE);

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
				throws SQLiteException {
			
			db.execSQL("drop table if exists " + DATABASE_TABLE);
			onCreate(db);
		}

		public void replaceAllTaskList(ArrayList<Task> tasks)
				throws SQLiteException {
			SQLiteDatabase db = getWritableDatabase();
			try {
				db.delete("Tasks", null, null);
				db.beginTransaction();
				String sql = "Insert or Replace into Tasks (id, description,location,date) values(?,?,?,?)";
				SQLiteStatement insert = db.compileStatement(sql);

				for (Task task : tasks) {
					insert.bindLong(1, task.getTaskId());
					insert.bindString(2, task.getDescription());
					insert.bindString(3, task.getLocation());
					insert.bindLong(4, task.getDate());
					insert.execute();
				}
				db.setTransactionSuccessful();
			} catch (SQLiteException e) {
				e.printStackTrace();
				throw e;
			} finally {
				db.endTransaction();
				db.close();
			}

		}

		public void addTask(Task task) throws SQLiteException {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues contentValues = createContentValues(task);
			db.insert(DATABASE_TABLE, null, contentValues);
			db.close();
		}

		public void removeDoneTasks(ArrayList<Task> tasks)
				throws SQLiteException {
			SQLiteDatabase db = getWritableDatabase();
			try {

				String sql = "Delete from Tasks where id = ?";
				SQLiteStatement delete = db.compileStatement(sql);
				db.beginTransaction();
				for (Task task : tasks) {
					delete.bindLong(1, task.getTaskId());
					delete.execute();
				}
				db.setTransactionSuccessful();
			} catch (SQLiteException e) {
				e.printStackTrace();
				throw e;
			} finally {
				db.endTransaction();
				db.close();
			}
		}

		public ArrayList<Task> getAllTaskList() throws SQLiteException {
			SQLiteDatabase db = getReadableDatabase();
			ArrayList<Task> taskList = new ArrayList<Task>();
			Cursor cursor = db
					.rawQuery("select * from " + DATABASE_TABLE, null);
			while (cursor.moveToNext()) {
				Task task = new Task();
				task.setTaskId(cursor.getLong(0));
				task.setDescription(cursor.getString(1));
				task.setLocation(cursor.getString(2));
				task.setDate(cursor.getLong(3));
				taskList.add(task);
			}
			return taskList;
		}

		public ContentValues createContentValues(Task task) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(KEY_TASK_ID, task.getTaskId());
			contentValues.put(KEY_DESCRIPTION, task.getDescription());
			contentValues.put(KEY_LOCATION, task.getLocation());
			contentValues.put(KEY_DATE, task.getDate());
			return contentValues;
		}

	}

}
