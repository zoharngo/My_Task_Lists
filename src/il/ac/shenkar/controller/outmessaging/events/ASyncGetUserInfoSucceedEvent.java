package il.ac.shenkar.controller.outmessaging.events;

import il.ac.shenkar.controller.application.User;

public class ASyncGetUserInfoSucceedEvent {
	public User user;

	public ASyncGetUserInfoSucceedEvent(User user) {
		super();
		this.user = user;
	}
}
