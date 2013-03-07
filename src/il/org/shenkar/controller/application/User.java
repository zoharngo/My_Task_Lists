package il.org.shenkar.controller.application;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

	private String userId;
	private String firstName;
	private String lastName;
	private String userPass;
	private String userEmail;
	private long userAgent = 1L;


	public User() {
		super();
	}

	public User(String userId, String userPass) {
		super();
		this.userId = userId;
		this.userPass = userPass;
	}

	public User(String userId, String firstName, String lastName,
			String userPass, String userEmail) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userPass = userPass;
		this.userEmail = userEmail;
	}

	public User(String userId, String firstName, String lastName,
			String userPass, String userEmail, long userAgent) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userPass = userPass;
		this.userEmail = userEmail;
		this.userAgent = userAgent;
	}

	public String getUserId() {
		return userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public long getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(long userAgent) {
		this.userAgent = userAgent;
	}

	public JSONObject toJSON() {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userId", userId);
			jsonObject.put("firstName", firstName);
			jsonObject.put("lastName", lastName);
			jsonObject.put("userPass", userPass);
			jsonObject.put("userEmail", userEmail);
			jsonObject.put("userAgent", userAgent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

}
