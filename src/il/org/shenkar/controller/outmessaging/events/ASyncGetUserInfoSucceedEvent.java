package il.org.shenkar.controller.outmessaging.events;

import il.org.shenkar.controller.application.User;

public class ASyncGetUserInfoSucceedEvent {
	public User user;

	public ASyncGetUserInfoSucceedEvent(User user) {
		super();
		this.user = user;
	}
}
