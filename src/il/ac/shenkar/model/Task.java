package il.ac.shenkar.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Task {

	private long taskId;
	private String description;
	private String location;
	private long date;

	public Task() {
		super();
	}

	public Task(long taskId, String description, String location, long date) {
		super();
		this.taskId = taskId;
		this.description = description;
		this.location = location;
		this.date = date;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public JSONObject toJSON() {

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("taskId", taskId);
			jsonObject.put("description", description);
			jsonObject.put("date", date);
			jsonObject.put("location", location);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

}
